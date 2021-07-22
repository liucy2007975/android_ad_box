package com.cow.liucy.libcommon.rxnetty;



import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.utils.ConvertUtil;


import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.client.TcpClient;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;
import rx.functions.Action1;

/**
 * RxNetty TCP连接管理类
 * Created by cow on 2018/6/8.
 */

public class CowNettyManager {
    private Connection<byte[],byte[]> mConnection;
    private String serverIP;
    private int port;
    private volatile boolean isOnLine = false;
    /**
     * 心跳时间间隔
     */
    private final int KEEP_ALIVED_TIME = 30;
    /**
     * 重连时间间隔
     */
    private final int RECONNET_INTERVAL_TIME = 10;
    private CowNettyEvent nettyEvent;
    private Disposable mDisposableReconnect;
    private Disposable mDisposableKeepAlived;
    private int count=100;

    public CowNettyManager(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
        AppLogger.e(">>>>serverIp:" + serverIP + ">>>port:" + port);
    }

    public void start() {
        if (mConnection != null) {
            mConnection.closeNow();
            mConnection = null;
        }
        SocketAddress socketAddress = new InetSocketAddress(serverIP, port);
        TcpClient.newClient(socketAddress)
                .<byte[], byte[]>pipelineConfigurator(new Action1<ChannelPipeline>() {
                    @Override
                    public void call(ChannelPipeline pipeline) {
                        // Decoders
                        pipeline.addLast("frameDecoder", new FixedLengthFrameDecoder(109));
                        pipeline.addLast("bytesDecoder", new ByteArrayDecoder());
                        // Encoder
                        pipeline.addLast("frameEncoder", new FixedLengthFrameDecoder(109));
                        pipeline.addLast("bytesEncoder", new ByteArrayDecoder());

                    }
                })
                .channelOption(ChannelOption.SO_KEEPALIVE, true)
                .channelOption(ChannelOption.TCP_NODELAY, true)
                .channelOption(ChannelOption.AUTO_READ, true)
                //设置读取超时时间，
                .readTimeOut(KEEP_ALIVED_TIME + 15 , TimeUnit.SECONDS)
                .createConnectionRequest()
                .subscribeOn(rx.schedulers.Schedulers.io())
                .subscribe(newConnection -> {
                    mConnection = newConnection;
                    mConnection.getInput().subscribe(message -> {
                        String msg=new String(message ,Charset.forName("UTF-8"));
                        AppLogger.e(">>>>>>message:"+msg+ ">>>length:"+message.length);
                        if (msg.startsWith("{")){
                            if (msg.contains("Reserved1=001;")){
                                AppLogger.e(">>播放业务数据接收正常>>>"+msg);
                                if (nettyEvent!=null) {
                                    nettyEvent.cowOnReciveData(msg);
                                }
                            }else{
                                AppLogger.e(">>心跳数据接收正常>>>"+msg);
                            }
                        }else{
                            AppLogger.e(">>>>数据格式错误>>>"+msg);
                        }
                    }, e -> {
                        e.printStackTrace();
                        reconnect(e);
                    });
                }, e -> {
                    e.printStackTrace();
                    reconnect(e);
                }, () -> {
                    isOnLine = true;

                    AppLogger.e("connect success");
                    cancelReconnect();
//                    //连接成功后，开启心跳
                    startKeepAlived();
                });

    }


    /**
     * 断开自动重新连接
     */
    private void reconnect(Throwable e) {
        AppLogger.e(">>>>reconnect e:" + e.getMessage());
        cancleKeepAlived();
        cancelReconnect();
        //定时任务，指定时间内重新连接
        mDisposableReconnect = Flowable.interval(RECONNET_INTERVAL_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(l -> {
                    if (mConnection != null) {
                        mConnection.closeNow();
                        mConnection = null;
                    }
                    AppLogger.e("reconnect");
                    if (isOnLine) {
                    }
                    isOnLine = false;
                    start();
                });
    }

    /**
     * 取消订阅
     */
    public void cancelReconnect() {
        if (mDisposableReconnect != null && !mDisposableReconnect.isDisposed()) {
            mDisposableReconnect.dispose();
        }
    }

    /**
     * 发送数据
     *
     * @param s
     * @return
     */
    public synchronized boolean send(byte[] s) {
//        AppLogger.e("send>>>" + ConvertUtil.bytesToHexString(s));
        if (mConnection == null) {
            AppLogger.e("mConnection == null");
            return false;
        }
        mConnection.writeBytes(Observable.just(s)).subscribe(v -> {
            AppLogger.e(">>>>>>writeBytes>>onNext");
        }, e -> {
            //发送失败，出现异常
            AppLogger.e(">>>>>>writeBytes>>e:" + e.getMessage());
        }, () -> {
            //发送成功
//            AppLogger.e(">>>>>>writeBytes>>OK");
        });
        return true;
    }

    /**
     * 停止心跳服务
     */
    public void cancleKeepAlived() {
        if (mDisposableKeepAlived != null && !mDisposableKeepAlived.isDisposed()) {
            mDisposableKeepAlived.dispose();
        }
    }

    public CowNettyEvent getNettyEvent() {
        return nettyEvent;
    }

    public void setNettyEvent(CowNettyEvent nettyEvent) {
        this.nettyEvent = nettyEvent;
    }

    /**
     * 停止服务
     */
    public synchronized void stop() {
        cancleKeepAlived();
        cancelReconnect();
        if (mConnection != null) {
            mConnection.closeNow();
            mConnection = null;
        }
    }

    private void startKeepAlived() {
        mDisposableKeepAlived = Flowable.interval(KEEP_ALIVED_TIME, TimeUnit.SECONDS).subscribe(aLong -> {
//            if (isSyncData) return;

            if (count++>=998){
                count=100;
                count++;
            }
            String data="{Video_No=000;Video_Mod=00;Video_Circ=00;Audio_No=000;Audio_Mod=00;Audio_Circ=00;Reserved1="+(count)+";Reserved2=000}";
            send(data.getBytes(Charset.forName("utf-8")));
        });
    }


}
