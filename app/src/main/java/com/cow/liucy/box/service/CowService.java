package com.cow.liucy.box.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cow.liucy.box.ui.MainActivity;
import com.cow.liucy.face.R;

import com.cow.liucy.libcommon.logger.AppLogger;

import com.cow.liucy.libcommon.rxnetty.CommandVo;
import com.cow.liucy.libcommon.rxnetty.CowNettyEvent;
import com.cow.liucy.libcommon.utils.AppPrefs;
import com.cow.liucy.libcommon.utils.CommonUtils;
import com.cow.liucy.libcommon.utils.FileUtils;
import com.cow.liucy.libcommon.utils.Valid;

import com.cow.liucy.libcommon.rxnetty.CowNettyManager;

import com.blankj.utilcode.util.ShellUtils;


import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by cow on 2019-04-18.
 */

public class CowService extends Service   {


    private CowNettyManager cowNettyManager;

    private CompositeDisposable compositeDisposable;
    private     volatile boolean isFinish = false;

    private static final String HELLO="/status";
    private static final String FILE="/file";
    private static final String HTTP_SET="/set";

    private Disposable mDisposableRecreate;

    private FtpServer ftpServer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        compositeDisposable = new CompositeDisposable();

        onNetWorkReady();
        AppLogger.e(">>>>>>cowHttpService onCreate>>>>:");

        /**
         * ,?????????7???????????????????????????????????????
         */
        Flowable.interval(1, TimeUnit.DAYS)
                .observeOn(Schedulers.io())
                .subscribe(l-> {
                        FileUtils.deleteOldLog();
                },e->{
                    e.printStackTrace();
                    AppLogger.e(">>>>>??????????????????????????????>>>");
                });

    }


    //??????Ftp Server
    private void startFTPServer(){
//            compositeDisposable.add(Flowable.just(0).subscribe(
//                    l->{
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2121);
//                        factory.setServerAddress("192.168.46.125");
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.addListener("default", factory.createListener());
        //FTP?????????
        BaseUser user = new BaseUser();
        user.setName("admin");
        user.setPassword("654321");
        //FTP?????????
        String rootPath= Environment.getExternalStorageDirectory().getPath();
        AppLogger.e(">>>>>>rootPath:"+rootPath);
        user.setHomeDirectory(rootPath);
        //FTP??????
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(user);
            if(ftpServer != null) {
                ftpServer.stop();
            }
            ftpServer = serverFactory.createServer();
            ftpServer.start();
            AppLogger.e(">>>>FTP Server start success!");
        }catch (Exception e){
            e.printStackTrace();
            AppLogger.e(">>>>FTP Server start failed!");
        }

//                    },e->{
//                        e.printStackTrace();
//                        AppLogger.e(">>>>FTP Server start failed!");
//                    }
//            ));
    }

    //??????FTP Server
    private void stopFTPServer(){
//        if (ftpServer!=null) {
//            compositeDisposable.add(Flowable.just(0).subscribe(
//                    l -> {
        if (ftpServer != null) {
            ftpServer.stop();
        }
        ftpServer = null;
        AppLogger.e(">>>>FTP Server stop success!");
//                    }, e -> {
//                        e.printStackTrace();
//                        AppLogger.e(">>>>FTP Server stop failed!");
//                    }
//            ));
//        }
    }

   @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onRebootEvent(RebootEvent rebootEvent) {
        if (rebootEvent.isReboot()){
            //10s???????????????
            Flowable.just(0)
                    .observeOn(Schedulers.io())
                    .subscribe(l->{
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ShellUtils.execCmd("reboot",false);
                    });
        }
    }


    /**
     * ??????????????????
     */
    private final int RECREATE_INTERVAL_TIME = 20;

    /**
     * ????????????IP???????????????????????????????????????
     */
    private void onNetWorkReady(){

       String localIp = CommonUtils.getIPAddress();
        if (Valid.valid(localIp)){
            AppLogger.e(">>>>localIp:"+localIp);

            stopFTPServer();
            startFTPServer();
            if (cowNettyManager!=null){
                cowNettyManager.stop();
                cowNettyManager=null;
            }
            cowNettyManager =new CowNettyManager(AppPrefs.getInstance().getServer(),AppPrefs.getInstance().getFtpPort());
            cowNettyManager.setNettyEvent(new CowNettyEvent() {
                @Override
                public void cowOnReciveData(String msg) {
                    String[] allCommand=msg.split(";");
                    if (allCommand.length!=8){
                        AppLogger.e(">>>>???????????????????????????");
                    }else {
                        CommandVo commandVo=new CommandVo();
                        commandVo.setVideoNo(allCommand[0].substring(allCommand[0].indexOf("=")+1));
                        commandVo.setVideoMod(allCommand[1].substring(allCommand[1].indexOf("=")+1));
                        commandVo.setVideoCirc(allCommand[2].substring(allCommand[2].indexOf("=")+1));
                        commandVo.setAudioNo(allCommand[3].substring(allCommand[3].indexOf("=")+1));
                        commandVo.setAudioMod(allCommand[4].substring(allCommand[4].indexOf("=")+1));
                        commandVo.setAudioCirc(allCommand[5].substring(allCommand[5].indexOf("=")+1));
//                        AppLogger.e(">>>>commandVo:"+JSON.toJSONString(commandVo));
                        EventBus.getDefault().post(commandVo);
                    }
                }
            });
            cowNettyManager.start();

            cancelReconnect();

        }else{
            AppLogger.e(">>>>??????????????????IP??????");
            reCreate();
        }

    }

    /**
     * ?????????????????????
     */
    private void reCreate() {
        cancelReconnect();
        //??????????????????????????????????????????
        mDisposableRecreate = Flowable.interval(RECREATE_INTERVAL_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(l -> {
                    onNetWorkReady();
                });
    }

    /**
     * ????????????
     */
    public void cancelReconnect() {
        if (mDisposableRecreate != null && !mDisposableRecreate.isDisposed()) {
            mDisposableRecreate.dispose();
            AppLogger.e(">>>>>??????????????????>>>>");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLogger.e(">>>>>>cowHttpService onStartCommand>>>>:");
        showNotification();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        isFinish = true;
        super.onDestroy();
        stopFTPServer();
        if (cowNettyManager!=null){
            cowNettyManager.stop();
            cowNettyManager=null;
        }
        EventBus.getDefault().unregister(this);
        if (compositeDisposable != null)
            compositeDisposable.clear();


    }

    /**
     * ??????Service?????????????????????????????????
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("AD_BOX??????");
        builder.setContentText("??????????????????...");
        builder.setContentInfo("");
        builder.setWhen(System.currentTimeMillis());
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(1000, notification);
    }

}
