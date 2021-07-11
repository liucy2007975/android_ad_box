package com.cow.liucy.box;

import android.content.Intent;
import android.util.Log;

import com.cow.liucy.box.service.CowService;
import com.cow.liucy.box.service.dns.CowDNSService;
import com.cow.liucy.face.BuildConfig;

import com.cow.liucy.libcommon.base.BaseApplication;
import com.cow.liucy.libcommon.db.CowBoxStore;
import com.cow.liucy.libcommon.db.objectbox.MyObjectBox;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.utils.Constants;
import com.cow.liucy.libcommon.utils.CrashUtils;
import com.cow.liucy.libcommon.utils.FileUtils;
import com.cow.liucy.libcommon.utils.Utils;

import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.exception.DbExceptionListener;
import io.reactivex.plugins.RxJavaPlugins;



/**
 * Created by cow on 2019-04-11.
 */

public class AppMain extends BaseApplication {

//    http://sg-gateway.dev1.dyajb.com/

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogger.init(this);   //Looger初始化
        AppLogger.e(">>>>AppMain onCreate");
        Utils.init(this);
        CrashUtils.init();
        //设置最大读事务
        MyObjectBox.builder().maxReaders(1000);
        MyObjectBox.builder().queryAttempts(2);
        CowBoxStore.boxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            boolean started = new AndroidObjectBrowser(CowBoxStore.boxStore).start(this);
            Log.i("ObjectBrowser", "Started: " + started);
        }
        CowBoxStore.boxStore.setDbExceptionListener(new DbExceptionListener() {
            @Override
            public void onDbException(Exception e) {
                AppLogger.e(">>>>>CowBoxStore diagnose:" + CowBoxStore.boxStore.diagnose());
                AppLogger.e(">>>>>CowBoxStore Exception:" + e.getMessage());
            }
        });
        checkAndCreateDefDir();
//        initData();
//        AppUtils.init(this);
        //自动关屏时间
//        AppUtils.setScreenTime(-1);
//        AjbHostUtils.openLedLight(true);
//        NetworkUitls.init(getApplicationContext());
//        setSystemTime();
        setRxJavaErrorHandler();
//        AppLogger.e("SpeechUtility.createUtility");
//        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5da14611");
//        LeakCanary.install(this);
//        ContentResolver contentResolver=this.getContentResolver();
//        Settings.System.putInt(contentResolver, "ethernet_use_static_ip", 1);
//
//        Settings.System.putString(contentResolver,"ethernet_static_ip","192.168.41.39");
//        Settings.System.putString(contentResolver, "ethernet_static_gateway", "192.168.41.254");
//        Settings.System.putString(contentResolver, "ethernet_static_netmask", "24");
//        Settings.System.putString(contentResolver, "ethernet_static_dns1", "8.8.8.8");
//        try {
//            NetUtil.setIp("192.168.41.39/24","8.8.8.8","192.168.41.254");
//        } catch (Exception e) {
//            e.printStackTrace();
//            ToastUtils.getShortToastByString(this, "设置失败！请输入正确的IP信息");
//            return;
//        }
//        //开启调试模式
//        CommandResult resultSetporp = Shell.run("setprop service.adb.tcp.port 5555");
//        CommandResult resultStop = Shell.run("stop adbd");
//        CommandResult resultStart = Shell.run("start adbd");
//        if (resultSetporp.isSuccessful()) {
//            AppLogger.e(">>>>" + resultSetporp.getStdout());
//        }
        //启动服务
        startService(new Intent(Utils.getContext(), CowService.class));
        startService(new Intent(Utils.getContext(), CowDNSService.class));


    }


    public void checkAndCreateDefDir() {
        FileUtils.isExist(Constants.APP_DEF_PATH);
        FileUtils.isExist(Constants.PICTURE_PATH);
//        FileUtils.isExist(Constants.ADVERTISEMENT_PATH);
        FileUtils.isExist(Constants.INSTALL_APK_PATH);
        FileUtils.isExist(Constants.VIDEO_PATH);
        FileUtils.isExist(Constants.SYNC_PATH);
        FileUtils.isExist(Constants.FTP_DEF_PATH);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    /**
     * 设置Rxjava错误打印
     */
    private void setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
        });
    }


}
