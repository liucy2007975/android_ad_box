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
 * Created by anjubao on 2019-04-11.
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

//    public void initData(){
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).removeAll();
//        CameraInfoEntity cameraInfoEntity=new CameraInfoEntity();
//        cameraInfoEntity.setCameraIp("192.168.8.100");
//        cameraInfoEntity.setDeviceType(2);
//        cameraInfoEntity.setId(0);
//        cameraInfoEntity.setPortName("出口1");
//        cameraInfoEntity.setParkCode("4545412121");
//        cameraInfoEntity.setPortId("5456444");
//        cameraInfoEntity.setTerminalId("111111111111111");
//        cameraInfoEntity.setPortDirect(0);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity);
//
//        CameraInfoEntity cameraInfoEntity2=new CameraInfoEntity();
//        cameraInfoEntity2.setCameraIp("192.168.8.101");
//        cameraInfoEntity2.setDeviceType(1);
//        cameraInfoEntity2.setId(0);
//        cameraInfoEntity2.setPortName("入口1");
//        cameraInfoEntity2.setParkCode("4545412121");
//        cameraInfoEntity2.setPortId("5456444");
//        cameraInfoEntity2.setTerminalId("111111111111111");
//        cameraInfoEntity2.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity2);
//
//
////        外口1（入主）：192.168.11.104
////        外口1（入副）：192.168.11.105
////        里口1（入主）：192.168.11.106
////        里口1（入副）：192.168.11.110
////        外口2（出主）：192.168.11.112
////        外口2（出副）：192.168.11.113
////        里口2（出主）：192.168.11.114
////        里口2（出副）：192.168.11.115
//        CameraInfoEntity cameraInfoEntity3=new CameraInfoEntity();
//        cameraInfoEntity3.setCameraIp("192.168.11.104");
//        cameraInfoEntity3.setDeviceType(1);
//        cameraInfoEntity3.setId(0);
//        cameraInfoEntity3.setPortName("外口1（入主）");
//        cameraInfoEntity3.setParkCode("4545412121");
//        cameraInfoEntity3.setPortId("5456444");
//        cameraInfoEntity3.setTerminalId("111111111111111");
//        cameraInfoEntity3.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity3);
//
//        CameraInfoEntity cameraInfoEntity4=new CameraInfoEntity();
//        cameraInfoEntity4.setCameraIp("192.168.11.106");
//        cameraInfoEntity4.setDeviceType(1);
//        cameraInfoEntity4.setId(0);
//        cameraInfoEntity4.setPortName("里口1（入主）");
//        cameraInfoEntity4.setParkCode("4545412121");
//        cameraInfoEntity4.setPortId("5456444");
//        cameraInfoEntity4.setTerminalId("111111111111111");
//        cameraInfoEntity4.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity4);
//
//        CameraInfoEntity cameraInfoEntity5=new CameraInfoEntity();
//        cameraInfoEntity5.setCameraIp("192.168.11.112");
//        cameraInfoEntity5.setDeviceType(1);
//        cameraInfoEntity5.setId(0);
//        cameraInfoEntity5.setPortName("外口2（出主）");
//        cameraInfoEntity5.setParkCode("4545412121");
//        cameraInfoEntity5.setPortId("5456444");
//        cameraInfoEntity5.setTerminalId("111111111111111");
//        cameraInfoEntity5.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity5);
//
//        CameraInfoEntity cameraInfoEntity6=new CameraInfoEntity();
//        cameraInfoEntity6.setCameraIp("192.168.11.114");
//        cameraInfoEntity6.setDeviceType(1);
//        cameraInfoEntity6.setId(0);
//        cameraInfoEntity6.setPortName("里口2（出主）");
//        cameraInfoEntity6.setParkCode("4545412121");
//        cameraInfoEntity6.setPortId("5456444");
//        cameraInfoEntity6.setTerminalId("111111111111111");
//        cameraInfoEntity6.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity6);
//
//        CameraInfoEntity cameraInfoEntity7=new CameraInfoEntity();
//        cameraInfoEntity7.setCameraIp("192.168.11.105");
//        cameraInfoEntity7.setDeviceType(1);
//        cameraInfoEntity7.setId(0);
//        cameraInfoEntity7.setPortName("外口1（入副）");
//        cameraInfoEntity7.setParkCode("4545412121");
//        cameraInfoEntity7.setPortId("5456444");
//        cameraInfoEntity7.setTerminalId("111111111111111");
//        cameraInfoEntity7.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity7);
//
//        CameraInfoEntity cameraInfoEntity8=new CameraInfoEntity();
//        cameraInfoEntity8.setCameraIp("192.168.11.110");
//        cameraInfoEntity8.setDeviceType(1);
//        cameraInfoEntity8.setId(0);
//        cameraInfoEntity8.setPortName("里口1（入副）");
//        cameraInfoEntity8.setParkCode("4545412121");
//        cameraInfoEntity8.setPortId("5456444");
//        cameraInfoEntity8.setTerminalId("111111111111111");
//        cameraInfoEntity8.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity8);
//
//        CameraInfoEntity cameraInfoEntity9=new CameraInfoEntity();
//        cameraInfoEntity9.setCameraIp("192.168.11.113");
//        cameraInfoEntity9.setDeviceType(1);
//        cameraInfoEntity9.setId(0);
//        cameraInfoEntity9.setPortName("外口2（出副）");
//        cameraInfoEntity9.setParkCode("4545412121");
//        cameraInfoEntity9.setPortId("5456444");
//        cameraInfoEntity9.setTerminalId("111111111111111");
//        cameraInfoEntity9.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity9);
//
//        CameraInfoEntity cameraInfoEntity10=new CameraInfoEntity();
//        cameraInfoEntity10.setCameraIp("192.168.11.115");
//        cameraInfoEntity10.setDeviceType(1);
//        cameraInfoEntity10.setId(0);
//        cameraInfoEntity10.setPortName("里口2（出副）");
//        cameraInfoEntity10.setParkCode("4545412121");
//        cameraInfoEntity10.setPortId("5456444");
//        cameraInfoEntity10.setTerminalId("111111111111111");
//        cameraInfoEntity10.setPortDirect(1);
//        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity10);
//
//        List<CameraInfoEntity> cameraInfoEntityList= CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).query().build().find();
//        AppLogger.e(JSON.toJSONString(cameraInfoEntityList));
//    }

    public void checkAndCreateDefDir() {
        FileUtils.isExist(Constants.APP_DEF_PATH);
        FileUtils.isExist(Constants.PICTURE_PATH);
        FileUtils.isExist(Constants.ADVERTISEMENT_PATH);
        FileUtils.isExist(Constants.INSTALL_APK_PATH);
        FileUtils.isExist(Constants.RECORD_PATH);
        FileUtils.isExist(Constants.SYNC_PATH);
        FileUtils.isExist(Constants.FTP_DEF_PATH);
//        //创建ftp//枪IP地址目录
//        List<CameraInfoEntity> cameraInfoEntityList= CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).query().order(CameraInfoEntity_.deviceType).build().find();
//        for (CameraInfoEntity cameraInfoEntity:cameraInfoEntityList){
//            AppLogger.e("create dir:"+Constants.FTP_DEF_PATH+cameraInfoEntity.getCameraIp()+"/");
//            FileUtils.isExist(Constants.FTP_DEF_PATH+cameraInfoEntity.getCameraIp()+"/");
//        }
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
