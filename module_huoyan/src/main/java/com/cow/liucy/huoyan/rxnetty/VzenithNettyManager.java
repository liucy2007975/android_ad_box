package com.cow.liucy.huoyan.rxnetty;



import com.cow.liucy.hdxm.libcommon.logger.AppLogger;

import com.cow.liucy.hdxm.libcommon.utils.ConvertUtil;
import com.cow.liucy.hdxm.libcommon.utils.Valid;
import com.cow.liucy.huoyan.model.IvsGetDeviceTimestampReq;
import com.cow.liucy.huoyan.model.IvsResultRequest;
import com.cow.liucy.huoyan.model.IvsResultResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
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
 * Created by anjubao on 2018/6/8.
 */

public class VzenithNettyManager {
    private Connection<byte[], byte[]> mConnection;
    private String serverIP;
    private int port;
    private volatile boolean isOnLine = false;
    /**
     * 心跳时间间隔
     */
    private final int KEEP_ALIVED_TIME = 25;
    /**
     * 重连时间间隔
     */
    private final int RECONNET_INTERVAL_TIME = 10;
    private VzenithNettyEvent nettyEvent;
    private Disposable mDisposableReconnect;
    private Disposable mDisposableKeepAlived;

    public VzenithNettyManager(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
        AppLogger.e(">>>>serverIp:" + serverIP + ">>>port:" + port);
    }

    byte[] data=null;
    byte[] fullImage=null;
    byte[] clipImage=null;
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
                        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, Integer.MAX_VALUE, 4, 4, 0, 0, true));
                        pipeline.addLast("bytesDecoder", new ByteArrayDecoder());
                        // Encoder
                        pipeline.addLast("frameEncoder", new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, Integer.MAX_VALUE, 4, 4, 0, 0, true));
                        pipeline.addLast("bytesEncoder", new ByteArrayDecoder());

                    }
                })
                .channelOption(ChannelOption.SO_KEEPALIVE, true)
                .channelOption(ChannelOption.TCP_NODELAY, true)
                .channelOption(ChannelOption.AUTO_READ, true)
                //设置读取超时时间，
                .readTimeOut(KEEP_ALIVED_TIME * 2 + 3, TimeUnit.SECONDS)
                .createConnectionRequest()
                .subscribeOn(rx.schedulers.Schedulers.io())
                .subscribe(newConnection -> {
                    mConnection = newConnection;
                    mConnection.getInput().subscribe(message -> {
//                        AppLogger.e("receive : " + ConvertUtil.bytesToHexString(message));
                        VzenithDataPacket vzenithDataPacket = new VzenithDataPacket(message);
//                        AppLogger.e(">>>>>getHeader:" + ConvertUtil.bytesToHexString(vzenithDataPacket.getHeader()));
                        if (Valid.valid(vzenithDataPacket)) {
                            if (vzenithDataPacket.getData()!=null && vzenithDataPacket.getData().length>0){

                            }
                            if (nettyEvent != null && vzenithDataPacket.getData()!=null && vzenithDataPacket.getData().length>0) {
                                data=null;
                                data=vzenithDataPacket.getData();
//                                AppLogger.e(">>>>>venithOnReciveData:"+ data.length);

                                //找字符串分隔符 '/0'
                                int position = -1;
                                for (int i = 0; i < data.length; i ++) {
                                    if (data[i] == '\0') {
                                        position = i;
                                        break;
                                    }
                                }
                                String json = null;
                                if (position < 0) {
                                    json = new String(data, Charset.forName("GBK"));
                                } else {
                                    json = new String(data, 0, position, Charset.forName("GBK"));
                                }
//                                AppLogger.e(">>>json:"+json);

                                JSONObject jsonObject=JSONObject.parseObject(json);
//                                AppLogger.e("cmd:"+jsonObject.get("cmd"));
                                //如何是车牌数据回调结果
                                if (jsonObject.get("cmd").toString().equals("ivs_result")){
                                    IvsResultResponse ivsResultResponse= (IvsResultResponse) JSONObject.parseObject(json,IvsResultResponse.class);
                                    AppLogger.e(">>>>:"+ivsResultResponse.getPlateResult().getLicense()+"  "+ivsResultResponse.getImageformat());

                                    int fullImageLength=jsonObject.getIntValue("fullImgSize");
                                    int clipImageLength=jsonObject.getIntValue("clipImgSize");
                                    fullImage=null;
                                    clipImage=null;
                                    fullImage = new byte[fullImageLength];
                                    clipImage = new byte[clipImageLength];
                                    if (fullImageLength>0) {
                                        System.arraycopy(data, position + 1, fullImage, 0, fullImage.length);
                                    }
                                    if (clipImageLength>0){
                                        System.arraycopy(data, position + 1 + fullImage.length, clipImage, 0, clipImage.length);
                                    }
                                    //接收数据回调
                                    nettyEvent.venithOnReciveData(serverIP,ivsResultResponse,fullImage,clipImage);
                                }

                            }
                        }
                    }, e -> {
                        e.printStackTrace();
                        reconnect(e);
                    });
                }, e -> {
                    e.printStackTrace();
                    reconnect(e);
                }, () -> {
                    if (nettyEvent != null) {
//                        nettyEvent.venithOnLine();
                        //配置车牌数据推送模式
                        setDataPushModel();
                    }
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
                        if (nettyEvent != null) {
//                            nettyEvent.venithOffLine();
                        }
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

    public VzenithNettyEvent getNettyEvent() {
        return nettyEvent;
    }

    public void setNettyEvent(VzenithNettyEvent nettyEvent) {
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

            VzenithDataPacket vzenithDataPacket=new VzenithDataPacket(new byte[]{'V', 'Z', (byte)0x01, 0, 0,0,0,0});

//            AppLogger.e(">>>proto>>>" + "send_keepalived_message:"+ConvertUtil.bytesToHexString(vzenithDataPacket.getPacket()));
            send(vzenithDataPacket.getPacket());
        });
    }

    /**
     * 车牌识别 配置推送数据方式
     */
    private void setDataPushModel() {

            VzenithDataPacket vzenithDataPacket=new VzenithDataPacket(new byte[]{'V', 'Z', 0, 0, 0,0,0,0});
            vzenithDataPacket.getPacket();

            IvsResultRequest ivsResultRequest=new IvsResultRequest();
            byte[] data=JSON.toJSONString(ivsResultRequest).getBytes();
            vzenithDataPacket.setData(data);
            vzenithDataPacket.setLen(ConvertUtil.intToByteBig(data.length));
            AppLogger.e(">>>proto>>>" + "setDataPushModel:"+ConvertUtil.bytesToHexString(vzenithDataPacket.getPacket()));
            send(vzenithDataPacket.getPacket());

    }

    /*
    *获取设备当前时间
     */
    public void getDeviceTimeStamp(){
        VzenithDataPacket vzenithDataPacket=new VzenithDataPacket(new byte[]{'V', 'Z', 0, 0, 0,0,0,0});
        vzenithDataPacket.getPacket();

        IvsGetDeviceTimestampReq ivsGetDeviceTimestampReq=new IvsGetDeviceTimestampReq();
        byte[] data=JSON.toJSONString(ivsGetDeviceTimestampReq).getBytes();
        vzenithDataPacket.setData(data);
        vzenithDataPacket.setLen(ConvertUtil.intToByteBig(data.length));
//        AppLogger.e(">>>proto>>>" + "getDeviceTimeStamp:"+ConvertUtil.bytesToHexString(vzenithDataPacket.getPacket()));
        send(vzenithDataPacket.getPacket());
    }
}
