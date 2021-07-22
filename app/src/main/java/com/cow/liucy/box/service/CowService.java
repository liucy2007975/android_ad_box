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


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
         * ,删除前7天的过期日记，一天执行一次
         */
        Flowable.interval(1, TimeUnit.DAYS)
                .observeOn(Schedulers.io())
                .subscribe(l-> {
                        FileUtils.deleteOldLog();
                },e->{
                    e.printStackTrace();
                    AppLogger.e(">>>>>定时任务删除日志出错>>>");
                });

    }


   @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onRebootEvent(RebootEvent rebootEvent) {
        if (rebootEvent.isReboot()){
            //10s后重启盒子
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
     * 重试时间间隔
     */
    private final int RECREATE_INTERVAL_TIME = 20;

    /**
     * 正常获取IP地址后初始化后台服务。。。
     */
    private void onNetWorkReady(){

       String localIp = CommonUtils.getIPAddress();
        if (Valid.valid(localIp)){
            AppLogger.e(">>>>localIp:"+localIp);

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
                        AppLogger.e(">>>>服务器数据格式有误");
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
            AppLogger.e(">>>>设备还未配置IP地址");
            reCreate();
        }

    }

    /**
     * 不断重新初始化
     */
    private void reCreate() {
        cancelReconnect();
        //定时任务，指定时间内重新连接
        mDisposableRecreate = Flowable.interval(RECREATE_INTERVAL_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(l -> {
                    onNetWorkReady();
                });
    }

    /**
     * 取消订阅
     */
    public void cancelReconnect() {
        if (mDisposableRecreate != null && !mDisposableRecreate.isDisposed()) {
            mDisposableRecreate.dispose();
            AppLogger.e(">>>>>取消定时任务>>>>");
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
        if (cowNettyManager!=null){
            cowNettyManager.stop();
            cowNettyManager=null;
        }
        EventBus.getDefault().unregister(this);
        if (compositeDisposable != null)
            compositeDisposable.clear();


    }

    /**
     * 设置Service前台运行，在通知栏显示
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("AD_BOX服务");
        builder.setContentText("服务正在运行...");
        builder.setContentInfo("");
        builder.setWhen(System.currentTimeMillis());
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(1000, notification);
    }

}
