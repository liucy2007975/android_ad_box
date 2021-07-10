package com.cow.liucy.box.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;

import com.cow.liucy.box.model.ConfigValue;
import com.cow.liucy.box.model.qy.AlarmInfoPlate;
import com.cow.liucy.box.model.qy.PlateResult;
import com.cow.liucy.box.model.qy.QYPlateInfo;
import com.cow.liucy.box.service.dns.CowDSNDto;
import com.cow.liucy.box.service.dns.DeviceInfo;
import com.cow.liucy.box.ui.MainActivity;
import com.cow.liucy.face.BuildConfig;
import com.cow.liucy.face.R;
import com.cow.liucy.hdxm.libcommon.api.CommonConfig;
import com.cow.liucy.hdxm.libcommon.api.http.RetrofitManager;
import com.cow.liucy.hdxm.libcommon.api.http.model.BaseResponse;
import com.cow.liucy.hdxm.libcommon.api.http.model.DeviceLoginRes;
import com.cow.liucy.hdxm.libcommon.api.http.model.EntryReq;
import com.cow.liucy.hdxm.libcommon.api.http.model.ExitReq;
import com.cow.liucy.hdxm.libcommon.api.http.model.FileUploadReq;
import com.cow.liucy.hdxm.libcommon.api.http.model.HeartBeatReq;
import com.cow.liucy.hdxm.libcommon.api.http.model.HeartBeatResp;
import com.cow.liucy.hdxm.libcommon.db.CowBoxStore;
import com.cow.liucy.hdxm.libcommon.db.objectbox.CameraInfoEntity;
import com.cow.liucy.hdxm.libcommon.db.objectbox.CameraInfoEntity_;
import com.cow.liucy.hdxm.libcommon.db.objectbox.CarEnterExitEntity;
import com.cow.liucy.hdxm.libcommon.db.objectbox.CarEnterExitEntity_;
import com.cow.liucy.hdxm.libcommon.enums.QianYiPlateColorType;
import com.cow.liucy.hdxm.libcommon.enums.VzenithCarColorType;
import com.cow.liucy.hdxm.libcommon.enums.VzenithPlateColorType;
import com.cow.liucy.hdxm.libcommon.eventbus.PlateEvent;
import com.cow.liucy.hdxm.libcommon.logger.AppLogger;

import com.cow.liucy.hdxm.libcommon.utils.AppPrefs;
import com.cow.liucy.hdxm.libcommon.utils.CommonUtils;
import com.cow.liucy.hdxm.libcommon.utils.Constants;
import com.cow.liucy.hdxm.libcommon.utils.DateTimeUtils;
import com.cow.liucy.hdxm.libcommon.utils.FileUtils;
import com.cow.liucy.hdxm.libcommon.utils.NetUtil;
import com.cow.liucy.hdxm.libcommon.utils.ToastUtils;
import com.cow.liucy.hdxm.libcommon.utils.Valid;

import com.cow.liucy.huoyan.model.IvsResultResponse;
import com.cow.liucy.huoyan.rxnetty.VzenithNettyEvent;
import com.cow.liucy.huoyan.rxnetty.VzenithNettyManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ShellUtils;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.async.http.body.UrlEncodedFormBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.koushikdutta.async.parser.StringParser;


import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by anjubao on 2019-04-18.
 */

public class CowService extends Service implements HttpServerRequestCallback {

    /**
     * 火眼SDK列表 IP《=》SDK
     */
    private Map<String,VzenithNettyManager> vzenithNettyManagerMap=new HashMap<>();



    /**
     * 出入口相机列表
     */
    private List<CameraInfoEntity> cameraInfoEntityList=new ArrayList<>();

    /**
     * 出入口相机列表
     */
    private Map<String,CameraInfoEntity> cameraInfoEntityMap=new HashMap<>();

    private Box<CameraInfoEntity> cameraInfoEntityBox;
    private Box<CarEnterExitEntity> carEnterExitEntityBox;
    private QueryBuilder<CarEnterExitEntity> carEnterExitEntityQueryBuilder;
    private Query<CarEnterExitEntity> carEnterExitEntityQuery;

    private Query<CameraInfoEntity> cameraInfoEntityQuery;

    private CompositeDisposable compositeDisposable;
    private     volatile boolean isFinish = false;

    private static int count=0;
    private AsyncHttpServer server;
    private boolean startFlag = false;
    final String[] getUrlList = new String[]{HELLO};
    final String[] urlList = new String[]{FILE,HTTP_SET,HTTP_RESET,POST_UPLOAD_URL,POST_UPLOAD_IMAGE_URL,HTTP_DEBUG, POST_CUSTOME_URL,RESULT_HEARTBEAT,POST_HUAXIA_URL,POST_QIANYI_URL,POST_HUOYAN_URL};
    private static final String HELLO="/status";
    private static final String FILE="/file";
    private static final String HTTP_SET="/set";
    private static final String HTTP_DEBUG="/debug";
    private static final String HTTP_RESET="/reset";
    private static final String RESULT_HEARTBEAT="/ParkAPI/Heartbeat";
    private static final String POST_CUSTOME_URL ="/ParkAPI/customer";

    private static final String POST_HUAXIA_URL="/ParkAPI/hx";
    private static final String POST_QIANYI_URL="/ParkAPI/sendScanCar";
    private static final String POST_HUOYAN_URL="/ParkAPI/hy";

    private static final String POST_UPLOAD_URL="/ParkAPI/upload";
    private static final String POST_UPLOAD_IMAGE_URL="/ParkAPI/uploadImage";
    AsyncHttpClient client=AsyncHttpClient.getDefaultInstance();
    public static int uploadCount=0;
    public static boolean updating=false;

    private FtpServer ftpServer;
    private Disposable mDisposableRecreate;

    List<String> ipList=new ArrayList<>();
//    QYNetSDK qyNetSDK=null;


    FileAlterationObserver observer=null;
    FileAlterationObserverCallBack fileAlterationObserverCallBack=null;
    FileAlterationMonitor fileAlterationMonitor=null;

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

//        setIp("192.168.8.115","255.255.255.0","192.168.8.1");

        initObjectBox();
        onNetWorkReady();
        AppLogger.e(">>>>>>AnjubaoHttpService onCreate>>>>:");

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

        /**
         * 设备定时重启，凌晨2点左右定时重启
         * 每500ms检查一次时间是否是02:00:00
         */
        Flowable.interval(500,TimeUnit.MILLISECONDS)
                .subscribe(l->{
                        String dateTime=DateTimeUtils.getFormatedDataString();
                        if (dateTime.contains(" 02:00:00")){
                            AppLogger.e(">>>>>>dateTime:>>>>>"+dateTime);
                            AppLogger.e(">>>>>>设备定时重启>>>>>");

                            //清空上传记录表
                            carEnterExitEntityBox.removeAll();

                            try {
                                //清空/ftp目录
                                FileUtil.del(Constants.FTP_DEF_PATH);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            ShellUtils.execCmd("reboot",false);
                        }

                },e->{
                    e.printStackTrace();
                });

//        File file = FileUtil.file(Environment.getExternalStorageDirectory().getPath() + "/ftp");
//        WatchMonitor.createAll(file, new SimpleWatcher(){
//            @Override
//            public void onCreate(WatchEvent<?> event, Path currentPath) {
//                AppLogger.e(">>>>>createAll>>>onCreate:"+currentPath);
//            }
//        }).start();

        //初始化监听ftp目录
//        fileObserverJni = new FileObserverJni(Environment.getExternalStorageDirectory().getPath()  + "/ftp", FileObserverJni.CREATE);
//        fileObserverJni.setmCallback(new FileObserverJniCallBack());

        observer = new FileAlterationObserver(new File(Environment.getExternalStorageDirectory().getPath()  + "/ftp"));
        fileAlterationObserverCallBack=new FileAlterationObserverCallBack();
        observer.addListener(fileAlterationObserverCallBack);
        fileAlterationMonitor=new  FileAlterationMonitor(1000, observer);
       try {
           fileAlterationMonitor.start();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    class  FileAlterationObserverCallBack implements FileAlterationListener {

        @Override
        public void onStart(FileAlterationObserver observer) {

        }

        @Override
        public void onDirectoryCreate(File directory) {
//                AppLogger.e(">>>>>>>onDirectoryCreate:"+directory.getPath());
        }

        @Override
        public void onDirectoryChange(File directory) {
//            AppLogger.e(">>>>>>>onDirectoryChange:"+directory.getPath());

        }

        @Override
        public void onDirectoryDelete(File directory) {
//            AppLogger.e(">>>>>>>onDirectoryDelete:"+directory.getPath());

        }

        @Override
        public void onFileCreate(File file) {
            AppLogger.e(">>>>>>>onFileCreate:"+file.getPath());
            try {
                //获取文件的后缀名 .jpg
                String suffix = file.getPath().substring(file.getPath().lastIndexOf(".") + 1);
                AppLogger.e(">>>>>>>File suffix:"+suffix);
                if (suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("json") || suffix.equalsIgnoreCase("jpeg")) {
                    //开启线程将文件上传>>>>>
                    AppLogger.e(">>>>onFileCreate path:" + file.getPath() + ">>>开启线程将文件上传");
                    EventBus.getDefault().post(new FileUploadEvent(file.getPath()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFileChange(File file) {
//            AppLogger.e(">>>>>>>onFileChange:"+file.getPath());
        }

        @Override
        public void onFileDelete(File file) {
//            AppLogger.e(">>>>>>>onFileDelete:"+file.getPath());
        }

        @Override
        public void onStop(FileAlterationObserver observer) {

        }
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onFileUploadEvent(FileUploadEvent fileUploadEvent) {
        try{
            if (!Valid.valid(CommonUtils.getSN())){
                AppLogger.e(">>>>>设备SN未初始化，不上传图片数据>>>");
                return;
            }
            File file=new File(fileUploadEvent.getFilePath());

            if (file.getName().endsWith("json")){
                //转的PC传过来的JSON数据上传
                AppLogger.e(">>>>PC图片JSON数据上传>>>");
                String jsonString=new String(FileUtil.readBytes(file.getPath()),Charset.forName("UTF-8"));
                RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString);
                RetrofitManager.getInstance().postUpload(fileUploadEvent.getFilePath(),body)
                        .subscribe(response-> {
                            if (response != null && response.code == 200) {
                                AppLogger.e(">>>>PC图片JSON数据上传>>>");
                                FileUtil.del(fileUploadEvent.getFilePath());
                                uploadCount++;
                                if (uploadCount>=9999){
                                    uploadCount=0;
                                }
                            }
                        });
            }else{
                //文件上传
                AppLogger.e(">>>>FTP图片数据上传>>>");
                FileUploadReq fileUploadReq=new FileUploadReq();
                fileUploadReq.setDataTime(DateTimeUtils.getFormatedDataString());
                fileUploadReq.setFilePath(fileUploadEvent.getFilePath());
                fileUploadReq.setType(3);
                fileUploadReq.setFileName(file.getName());
                fileUploadReq.setPic(new String(
                        Base64.encodeToString( FileUtil.readBytes(file.getPath()), Base64.DEFAULT)
                ));
                RetrofitManager.getInstance().postUpload(fileUploadReq)
                        .subscribe(response-> {
                            if (response != null && response.code == 200) {
                                AppLogger.e(">>>>FTP图片数据上传成功>>>");
                                FileUtil.del(fileUploadReq.getFilePath());
                                uploadCount++;
                                if (uploadCount>=9999){
                                    uploadCount=0;
                                }
                            }
                        });
            }
        }catch (Exception e){
            e.printStackTrace();
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
            this.server = new AsyncHttpServer();
            initObjectBox();
            initVzenithNettyManager();

            stopHttpServer();
            initHttpServer();
            startHttpServer();
            stopFTPServer();
            startFTPServer();

            Flowable.just(0)
                    .observeOn(Schedulers.io())
                    .subscribe(l->{
                        try {
                            //设备登陆
                            Call<BaseResponse<DeviceLoginRes>> deviceLogin = RetrofitManager.getInstance()
                                    .postDeviceLogin(AppPrefs.getInstance().getSn().toString());
                            Response<BaseResponse<DeviceLoginRes>> responseBody = deviceLogin.execute();
                            if (Valid.valid(responseBody) && Valid.valid(responseBody.body())){
                                if (responseBody.body().code==200){
                                    DeviceLoginRes deviceLoginRes=responseBody.body().data;
                                    if (Valid.valid(deviceLoginRes)){
                                        AppPrefs.getInstance().setParkingId(deviceLoginRes.getParkingLotId()+"");
                                        AppPrefs.getInstance().setTerminalId(deviceLoginRes.getDeviceId()+"");
                                        //设置系统时间
                                        SystemClock.setCurrentTimeMillis(deviceLoginRes.getTime());    //需要系统权限
                                    }
                                    AppLogger.e(">>>>>>>deviceLogin:"+JSON.toJSONString(deviceLoginRes));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },e->{
                        e.printStackTrace();
                    });
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
        AppLogger.e(">>>>>>AnjubaoHttpService onStartCommand>>>>:");
        showNotification();

//        AppPrefs.getInstance().setTerminalId(1+"");
//        AppPrefs.getInstance().setParkingId(1+"");

        //HTTP心跳时间同步定时任务
        Flowable.interval(5, 60*5, TimeUnit.SECONDS)
                .subscribe(l -> {
                    if (Valid.valid(AppPrefs.getInstance().getTerminalId())) {
                        HeartBeatReq heartBeatReq=new HeartBeatReq();
                        heartBeatReq.setDeviceId(Long.parseLong(AppPrefs.getInstance().getTerminalId()));
                        heartBeatReq.setLastUpdateTime(DateTimeUtils.getFormatedDataString());
                        heartBeatReq.setVersion(BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
                        RetrofitManager.getInstance().postDeviceHeartbeatV2(heartBeatReq)
                                .subscribe(response->{
                                    if (response!=null && response.code==200){
                                            HeartBeatResp heartBeatResp=response.data;
                                            if (heartBeatResp!=null && heartBeatResp.getType()==1){
                                                    if (Valid.valid(heartBeatResp.getDownloadUrl()) && !heartBeatResp.getDownloadUrl().equalsIgnoreCase("null")){
                                                        if (updating==true){
                                                            return;
                                                        }
                                                        updating=true;
                                                        FileUtils.deleteFile("/sdcard/smartparking/update.apk");
                                                        AsyncHttpGet request=new AsyncHttpGet(heartBeatResp.getDownloadUrl());
                                                        AsyncHttpClient.getDefaultInstance().executeFile(request, "/sdcard/smartparking/update.apk", new AsyncHttpClient.FileCallback() {
                                                            @Override
                                                            public void onCompleted(Exception e, AsyncHttpResponse response, File result) {
                                                                if (e != null) {
                                                                    e.printStackTrace();
                                                                    return;
                                                                }
                                                                AppLogger.e("my file is available at: " + result.getPath());
                                                                execCommand("pm", "install", "-r", result.getPath());
                                                            }
                                                        });
                                                    }
                                            }
                                    }else{
                                        AppLogger.e(">>>>心跳失败，code:"+response.code);
                                    }
                                },e->{
                                    AppLogger.e(">>>>心跳失败");
                                    e.printStackTrace();
                                });

                    }else{
                        AppLogger.e(">>>设备暂未初始化>>>即将进行设备登陆>>");
                        try {
                            //设备登陆
                            Call<BaseResponse<DeviceLoginRes>> deviceLogin = RetrofitManager.getInstance()
                                    .postDeviceLogin(AppPrefs.getInstance().getSn().toString());
                            Response<BaseResponse<DeviceLoginRes>> responseBody = deviceLogin.execute();
                            if (Valid.valid(responseBody) && Valid.valid(responseBody.body())){
                                if (responseBody.body().code==200){
                                    DeviceLoginRes deviceLoginRes=responseBody.body().data;
                                    if (Valid.valid(deviceLoginRes)){
                                        AppPrefs.getInstance().setParkingId(deviceLoginRes.getParkingLotId()+"");
                                        AppPrefs.getInstance().setTerminalId(deviceLoginRes.getDeviceId()+"");
                                        //设置系统时间
                                        SystemClock.setCurrentTimeMillis(deviceLoginRes.getTime());    //需要系统权限
                                    }
                                    AppLogger.e(">>>>>>>deviceLogin:"+JSON.toJSONString(deviceLoginRes));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, e -> {
                    AppLogger.e(">>>e:" + e.getMessage());
                });
        timeSendCarEntryExitRecord();

        return super.onStartCommand(intent, flags, startId);
    }

    //启动Ftp Server
    private void startFTPServer(){
//            compositeDisposable.add(Flowable.just(0).subscribe(
//                    l->{
                        ListenerFactory factory = new ListenerFactory();
                        factory.setPort(AppPrefs.getInstance().getFtpPort());
//                        factory.setServerAddress("192.168.46.125");
                        FtpServerFactory serverFactory = new FtpServerFactory();
                        serverFactory.addListener("default", factory.createListener());
                        //FTP用户名
                        BaseUser user = new BaseUser();
                        user.setName("user");
                        user.setPassword("user");
                        //FTP根目录
                        String rootPath=Environment.getExternalStorageDirectory().getPath();
                        AppLogger.e(">>>>>>rootPath:"+rootPath);
                        user.setHomeDirectory(rootPath);
                        //FTP权限
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

    //停止FTP Server
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

    /**
     * 初始化连接相机SDK
     */
    private void initVzenithNettyManager() {

        if (Valid.valid(cameraInfoEntityMap) && Valid.valid(cameraInfoEntityList)){

            clear();//先停止所有相机连接
            for (CameraInfoEntity cameraInfoEntity:cameraInfoEntityList) {

               if (cameraInfoEntity.getDeviceType()==2){//火眼相机
                    try {
                        VzenithNettyManager vzenithNettyManager = new VzenithNettyManager(cameraInfoEntity.getCameraIp(), 8131);
                        vzenithNettyManager.setNettyEvent(new VzenithNettyEvent() {
                            @Override
                            public void venithOnReciveData(String deviceIp, IvsResultResponse ivsResultResponse, byte[] fullImage, byte[] clipImage) {
                                PlateEvent plateEvent = new PlateEvent();
                                plateEvent.setNumber(ivsResultResponse.getPlateResult().getLicense());//车牌
                                plateEvent.setVehicleColor(VzenithCarColorType.getDescByType(ivsResultResponse.getPlateResult().getCarColor()) + "");//车身颜色
                                plateEvent.setPlateColor(VzenithPlateColorType.getDescByType(ivsResultResponse.getPlateResult().getColorType()) + "");//车牌颜色
                                plateEvent.setDeviceIp(deviceIp);//相机Ip
                                plateEvent.setPlateConfidence(ivsResultResponse.getPlateResult().getConfidence());//车牌可信度
                                plateEvent.setPicdata(fullImage);//车牌图片

                                //EventBus发现车牌事件
                                EventBus.getDefault().post(plateEvent);

                            }
                        });
                        vzenithNettyManager.start();
                        vzenithNettyManagerMap.put(cameraInfoEntity.getCameraIp(), vzenithNettyManager);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (cameraInfoEntity.getDeviceType()==3){//芊熠相机
                    ipList.add(cameraInfoEntity.getCameraIp());
//                    QYNetSDK qyNetSDK=new QYNetSDK();
                }
            }

        }

//        QianYiNettyManager qianYiNettyManager=new QianYiNettyManager("192.168.8.10",40000);
//        qianYiNettyManager.setNettyEvent(new QianYiNettyEvent() {
//            @Override
//            public void venithOnReciveData(String deviceIp) {
//
//            }
//        });
//        qianYiNettyManager.start();
    }


    /**
     * 初始化数据库
     */
    private void initObjectBox() {
        cameraInfoEntityBox= CowBoxStore.boxStore.boxFor(CameraInfoEntity.class);
        cameraInfoEntityQuery=cameraInfoEntityBox.query().order(CameraInfoEntity_.deviceType).build();
        cameraInfoEntityList=cameraInfoEntityQuery.find();

        carEnterExitEntityBox= CowBoxStore.boxStore.boxFor(CarEnterExitEntity.class);
        carEnterExitEntityQueryBuilder=carEnterExitEntityBox.query();
        carEnterExitEntityQuery=carEnterExitEntityQueryBuilder.build();
        cameraInfoEntityMap.clear();
        if (Valid.valid(cameraInfoEntityList)){
            for (CameraInfoEntity cameraInfoEntity: cameraInfoEntityList) {
                cameraInfoEntityMap.put(cameraInfoEntity.getCameraIp(),cameraInfoEntity);
            }
        }
    }

    /**
     * 定时发送出入记录
     */
    private void timeSendCarEntryExitRecord(){
        compositeDisposable.add(Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(integer -> {
                    while (!isFinish) {
                        CarEnterExitEntity carEnterExitEntity = carEnterExitEntityBox.query().orderDesc(CarEnterExitEntity_.id).build().findFirst();
                        if (Valid.valid(carEnterExitEntity)) {
                            try {
                                if (carEnterExitEntity.getType()==0){//入场数据上传

                                    EntryReq entryReq=new EntryReq();
                                    entryReq.setCarType(carEnterExitEntity.getCarType());
                                    entryReq.setDataProviderId(carEnterExitEntity.getId()+"");
                                    entryReq.setEntryTime(DateTimeUtils.getCustomizedDateTime(carEnterExitEntity.getCreateTime(),DateTimeUtils.FORMAT_DATETIME_UI));
                                    entryReq.setLpn(carEnterExitEntity.getCarNo());
                                    entryReq.setParkingLotBoxId(carEnterExitEntity.getTerminalId());
                                    entryReq.setParkingLotId(carEnterExitEntity.getParkId());
                                    entryReq.setPlateColor(carEnterExitEntity.getPlateColor());
                                    entryReq.setVehicleColor(carEnterExitEntity.getVehicleColor());
                                    entryReq.setPlateConfidence(carEnterExitEntity.getPlateConfidence());
                                    AppLogger.e(">>>>发送入场数据:"+JSON.toJSONString(entryReq));
                                    entryReq.setEntryPicBase64s(new String[]{
                                            Base64.encodeToString( FileUtil.readBytes(carEnterExitEntity.getImageUrl()), Base64.DEFAULT)
                                    });
//                                    AppLogger.e(">>>>base64:"+entryReq.getEntryPicBase64s()[0].substring(0,1000));
                                    Call<BaseResponse<Object>> call = RetrofitManager.getInstance().postEntry(entryReq);
                                    Response<BaseResponse<Object>> response = call.execute();
                                    if (response.isSuccessful()) {
//                                        AppLogger.e("发送入场记录成功");
                                        FileUtil.del(carEnterExitEntity.getImageUrl());//删除本地图片文件
                                        carEnterExitEntityBox.remove(carEnterExitEntity.getId());
                                    } else {
                                        AppLogger.e("发送入场记录失败" + response.toString());
                                    }
                                }else if (carEnterExitEntity.getType()==1){//出场数据上传

                                    ExitReq exitReq=new ExitReq();

                                    exitReq.setCarType(carEnterExitEntity.getCarType());
                                    exitReq.setDataProviderId(carEnterExitEntity.getId()+"");
                                    exitReq.setExitTime(DateTimeUtils.getCustomizedDateTime(carEnterExitEntity.getCreateTime(),DateTimeUtils.FORMAT_DATETIME_UI));
                                    exitReq.setLpn(carEnterExitEntity.getCarNo());
                                    exitReq.setParkingLotBoxId(carEnterExitEntity.getTerminalId());
                                    exitReq.setParkingLotId(carEnterExitEntity.getParkId());
                                    exitReq.setPlateColor(carEnterExitEntity.getPlateColor());
                                    exitReq.setVehicleColor(carEnterExitEntity.getVehicleColor());
                                    exitReq.setPlateConfidence(carEnterExitEntity.getPlateConfidence());
                                    AppLogger.e(">>>>发送出场数据:"+JSON.toJSONString(exitReq));
                                    exitReq.setExitPicBase64s(new String[]{
                                            Base64.encodeToString( FileUtil.readBytes(carEnterExitEntity.getImageUrl()), Base64.DEFAULT)
                                    });
                                    Call<BaseResponse<Object>> call = RetrofitManager.getInstance().postExit(exitReq);
                                    Response<BaseResponse<Object>> response = call.execute();
                                    if (response.isSuccessful()) {
//                                        AppLogger.e("发送出场记录成功");
                                        FileUtil.del(carEnterExitEntity.getImageUrl());//删除本地图片文件
                                        carEnterExitEntityBox.remove(carEnterExitEntity.getId());

                                    } else {
                                        AppLogger.e("发送出场记录失败" + response.toString());
                                    }
                                }



                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Thread.sleep(1000);
                    }
                }));
    }


    private  void clear(){

        if (Valid.valid(vzenithNettyManagerMap)){
            for (String ip: vzenithNettyManagerMap.keySet()){
                vzenithNettyManagerMap.get(ip).stop();
            }
            vzenithNettyManagerMap.clear();
        }

        ipList.clear();
    }


    @Override
    public void onDestroy() {
        isFinish = true;
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (compositeDisposable != null)
            compositeDisposable.clear();
        clear();
        stopFTPServer();
    }




    /**
     * 设置Service前台运行，在通知栏显示
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("停车场数据采集服务");
        builder.setContentText("服务正在运行...");
        builder.setContentInfo("");
        builder.setWhen(System.currentTimeMillis());
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(1000, notification);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void saveRecord(PlateEvent plateEvent) {
        AppLogger.e(">>>>>>>saveRecord>>>"+ plateEvent.getNumber());
        if (!Valid.valid(AppPrefs.getInstance().getParkingId()) && !Valid.valid(AppPrefs.getInstance().getTerminalId())){
            AppLogger.e(">>>>>设备还未初始化>>>>");
            return;
        }
        if (Valid.valid(plateEvent)) {
            //停车记录入库
            CameraInfoEntity cameraInfoEntity=cameraInfoEntityMap.get(plateEvent.getDeviceIp());
            CarEnterExitEntity carEnterExitEntity=new CarEnterExitEntity();
            carEnterExitEntity.setId(0);
            carEnterExitEntity.setCarNo(plateEvent.getNumber());
            carEnterExitEntity.setCarType(plateEvent.getType()+"");
            carEnterExitEntity.setPlateColor(plateEvent.getPlateColor());
            carEnterExitEntity.setPlateConfidence(plateEvent.getPlateConfidence()+"");
            carEnterExitEntity.setVehicleColor(plateEvent.getVehicleColor());
            carEnterExitEntity.setTerminalId(AppPrefs.getInstance().getTerminalId());
            carEnterExitEntity.setPortName(cameraInfoEntity.getPortName());
            carEnterExitEntity.setParkId(AppPrefs.getInstance().getParkingId());
            carEnterExitEntity.setType(cameraInfoEntity.getPortDirect());//进出类型，0：入场，1：出场
//            Bitmap bitmap3= BitmapFactory.decodeByteArray(plateEvent.getPicdata(),0,plateEvent.getPicdata().length);
//             com.blankj.utilcode.util.ImageUtils.save(bitmap3,"/sdcard/smartparking/"+plateEvent.getNumber()+".jpg", Bitmap.CompressFormat.JPEG);
//             if (bitmap3!=null && !bitmap3.isRecycled()){
//                 bitmap3.recycle();
//                 bitmap3=null;
//             }
            count++;
            if (count>=100000){
                count=1;
            }
            String parentPath=DateTimeUtils.getCustomizedDateTime(new Date(), "yyyy/MM/dd");
            String tmpPath = parentPath + "/" + cameraInfoEntity.getCameraIp() + "/" + "_"+(count)+".jpg" ;
            String filePath = Constants.PICTURE_PATH + tmpPath;
            FileUtil.writeBytes(plateEvent.getPicdata(),filePath);
            carEnterExitEntity.setImageUrl(filePath);
            carEnterExitEntityBox.put(carEnterExitEntity);
        }
    }


    /**
     * 重置应用数据库
     */
    @SuppressLint("CheckResult")
    private void resetApp(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
//        response.send(JSON.toJSONString(new ResultInfo<>(1, true, "重置应用通知成功！")));
//
//        Observable.just(1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(integer -> {
//                    //                    initAppPrams();
////                    AppUtils.stopProtoNetty();
////                    AppUtils.deleteTables();
////                    FileUtils.deleteAllFile(new File(FaceEngineFactory.fileRootPath));
////                    AppUtils.reboot(CowService.this);
//                });
    }

    /**
     * 重置应用参数
     */
    private void initAppPrams() {
//        DeviceSetBean deviceSetBean = new DeviceSetBean();
//        SPUtil.saveDeviceData(DeviceSetBean.BeanName, deviceSetBean);
//
//        FacePassConfigBean facePassConfigBean = new FacePassConfigBean();
//        SPUtil.saveDeviceData(FacePassConfigBean.BeanName, facePassConfigBean);
//
//        //访问记录回传地址
//        SPUtil.setStringSF(Contants.ACCESS_RECORD_HTTP_POST_URL, "");
//        SPUtil.setBooleanSF(Contants.ACCESS_RECORD_IS_PIC, false);
//        SPUtil.setBooleanSF(Contants.ACCESS_RECORD_IS_FAIL, false);
//        //识别距离
//        SPUtil.setIntergerSF(Contants.SMALL_RECT_SIZE, 3);
//        SPUtil.setBooleanSF(Contants.SMALL_RECT_CHECK, false);
//        //出入记录保存时间
//        SPUtil.setIntergerSF(Contants.ACCESS_RECORD_DELETE_TIME, 365);
    }

    private void initHttpServer() {
        for (String url : this.urlList) {
            this.server.post(url, this);
        }
        for (String url : this.getUrlList) {
            this.server.get(url, this);
        }
    }

    private void startHttpServer() {
        AppLogger.d("startHttpServer");
        if (!this.startFlag) {
            this.server.listen(8080);
            this.startFlag = true;
        }
    }

    private void stopHttpServer() {
        AppLogger.d("stopHttpServer");
        if (this.server != null) {
            this.server.stop();
            this.startFlag = false;
        }
    }

    int i=0;

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        AppLogger.e(">>>>>>request url>>>>:" + request.getPath().trim());
        switch (request.getPath().trim()) {
            case HELLO:
                BaseResponse baseResponse=new BaseResponse();
                baseResponse.code=0;
                baseResponse.datetime=DateTimeUtils.getFormatedDataString();
                DeviceInfo deviceInfo=new DeviceInfo();
                deviceInfo.setDeviceSn(AppPrefs.getInstance().getSn());
                deviceInfo.setCameraInfoEntityList(CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).query().build().find());
                deviceInfo.setTime(DateTimeUtils.getFormatedDataString());
                deviceInfo.setUrl(AppPrefs.getInstance().getServer());
                deviceInfo.setVersion(BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
                deviceInfo.setParkingId(AppPrefs.getInstance().getParkingId());
                deviceInfo.setDeviceId(AppPrefs.getInstance().getTerminalId());
                baseResponse.data=deviceInfo;//"";
                baseResponse.message="安居宝停车场数据采集器";
                response.send(JSON.toJSONString(baseResponse));
                break;
            case FILE:
//                AppLogger.e(">>>>>json:"+request.getBody().getContentType()); ;
                try {
                    String json= URLDecoder.decode(request.getBody().get().toString(),"UTF-8");
                    AppLogger.e(">>>>>json:"+json);
                    Object parse1 = JSON.parse(json);
                    String s = parse1.toString();
                    List<CameraInfoEntity> cameraInfoEntityList= (List<CameraInfoEntity>) JSON.parseArray(s,CameraInfoEntity.class);
                    if (Valid.valid(cameraInfoEntityList)){
                        //清空所有
                        CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).removeAll();
//                        AppLogger.e(">>>>>removeAll:"+cameraInfoEntityList.size());
                        for (int i=0;i<cameraInfoEntityList.size();i++){
                            CameraInfoEntity cameraInfoEntity=cameraInfoEntityList.get(i);
                            cameraInfoEntity.setId(0);
                          AppLogger.e(">>>>cameraInfoEntity:"+JSON.toJSONString(cameraInfoEntity));
                            CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).put(cameraInfoEntity);
                        }
                    }
                    AppLogger.e(">>>>>json:"+JSON.toJSONString(cameraInfoEntityList));
                    BaseResponse baseResponse1=new BaseResponse();
                    baseResponse1.code=0;
                    baseResponse1.datetime=DateTimeUtils.getFormatedDataString();
                    baseResponse1.data=cameraInfoEntityList;
                    response.send(JSON.toJSONString(baseResponse1));

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

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case HTTP_DEBUG:
                //构建对象
                String json12= null;
                try {
                    json12 = URLDecoder.decode(request.getBody().get().toString(), "UTF-8");
                    AppLogger.e(">>>>>json:" + json12);
                    Object parse1 = JSON.parse(json12);
                    String s = parse1.toString();

                    CowDSNDto anjubaoDSNDto1 = JSON.parseObject(s, CowDSNDto.class);
                    if (Valid.valid(anjubaoDSNDto1)) {
                        //开启调试模式
                        if (anjubaoDSNDto1.getCellNo().equalsIgnoreCase("110")){
                            CommandResult resultSetporp = Shell.run("setprop service.adb.tcp.port 5555");
                            CommandResult resultStop = Shell.run("stop adbd");
                            CommandResult resultStart = Shell.run("start adbd");
                            if (resultSetporp.isSuccessful()) {
                                AppLogger.e(">>>>" + resultSetporp.getStdout());
                            }
                            BaseResponse baseResponse22=new BaseResponse();
                            baseResponse22.code=0;
                            baseResponse22.datetime=DateTimeUtils.getFormatedDataString();
                            response.send(JSON.toJSONString(baseResponse22));
                        }
                    }

                }catch (Exception eee){

                }
                break;
            case HTTP_RESET:
//                execCommand("pm", "install", "-r", "/sdcard/v3.0.0_1_2021-06-04.apk");

                //构建对象
                String json1= null;
                try {
                    json1 = URLDecoder.decode(request.getBody().get().toString(), "UTF-8");
                    AppLogger.e(">>>>>json:" + json1);
                    Object parse1 = JSON.parse(json1);
                    String s = parse1.toString();

                    CowDSNDto anjubaoDSNDto1 = JSON.parseObject(s, CowDSNDto.class);
                    if (Valid.valid(anjubaoDSNDto1)) {
                        //重置数据库
                        if (anjubaoDSNDto1.getIsRest() == 1) {
                            AppLogger.e("恢复出厂设置");
                            AppPrefs.getInstance().setServer("");
                            AppPrefs.getInstance().setSn("");
                            AppPrefs.getInstance().setTerminalId("");
                            AppPrefs.getInstance().setParkingId("");
                            CowBoxStore.boxStore.boxFor(CameraInfoEntity.class).removeAll();
                            CowBoxStore.boxStore.boxFor(CarEnterExitEntity.class).removeAll();

                            BaseResponse baseResponse22=new BaseResponse();
                            baseResponse22.code=0;
                            baseResponse22.datetime=DateTimeUtils.getFormatedDataString();
                            response.send(JSON.toJSONString(baseResponse22));

                            FileUtils.deleteAllFile(new File(Constants.PICTURE_PATH));

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
                            return;
                        }
                    }
                }catch (Exception e){

                }
                break;
            case HTTP_SET:
                //构建对象
                String json= null;
                try {
                    json = URLDecoder.decode(request.getBody().get().toString(),"UTF-8");
                    AppLogger.e(">>>>>json:"+json);
                    Object parse1 = JSON.parse(json);
                    String s = parse1.toString();

                    CowDSNDto anjubaoDSNDto = JSON.parseObject(s, CowDSNDto.class);
                    if (Valid.valid(anjubaoDSNDto)) {
//                if (anjubaoDSNDto.getLocalIp().split("/").length < 2) {
//                    AppLogger.e(">>>>>>>>>设置IP错误，格式不对");
//                    return;
//                }
//                        if (!CommonUtils.getMacAddress().equals(anjubaoDSNDto.getMacAddress())) {
//                            AppLogger.e(">>>>>>远程配置：mac=" + anjubaoDSNDto.getMacAddress() + "不是本机mac,本机=" + CommonUtils.getMacAddress());
//                            return;
//                        }
//                        //开启调试模式
//                        if (anjubaoDSNDto.getCellNo().equalsIgnoreCase("110")){
//
//                            CommandResult resultSetporp = Shell.run("setprop service.adb.tcp.port 5555");
//                            CommandResult resultStop = Shell.run("stop adbd");
//                            CommandResult resultStart = Shell.run("start adbd");
//                            if (resultSetporp.isSuccessful()) {
//                                AppLogger.e(">>>>" + resultSetporp.getStdout());
//                            }
//
//                        }

                        //服务器ip
                        String serverIp = anjubaoDSNDto.getServerIp();

                        String ftpPort=anjubaoDSNDto.getElevatorServerIp();
                        if (Valid.valid(ftpPort)){
                            try{
                                int fp= Integer.parseInt(ftpPort);
                                if (fp>=21 && fp<=50000){
                                    AppPrefs.getInstance().setFtpPort(fp);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
//                if (!serverIp.startsWith("http")) {
//                    serverIp = "http://" + serverIp;
//                }
//                if (!serverIp.endsWith("/")) {
//                    serverIp = serverIp+"/";
//                }
                        if (Valid.valid(serverIp) && !serverIp.equals(AppPrefs.getInstance().getServer())) {

                            //设置tcp ip
                            AppLogger.e("serverIp="+serverIp);
//                    URI uri = new URI(serverIp);
//                    if (Valid.valid(uri.getHost()))
//                        serverIp = uri.getHost();
                            AppPrefs.getInstance().setServer(serverIp);

                            //设置http ip
                            RetrofitManager.resetRetrofitManager();

                        }
                        //设备Sn
                        String deviceSn = anjubaoDSNDto.getDeviceSn();
                        if (Valid.valid(deviceSn)) {
                            try {
                                AppPrefs.getInstance().setSn(deviceSn);
                            } catch (Exception e) {
                                AppLogger.e(">>>>>>远程配置deviceSn失败");
                            }
                        }
                        //梯控服务器
                        String elevatorServerIp = anjubaoDSNDto.getElevatorServerIp();
                        if (Valid.valid(elevatorServerIp) && !elevatorServerIp.equals(AppPrefs.getInstance().getElevatorServer())) {
                            if (elevatorServerIp.split(":").length == 2) {
                                AppPrefs.getInstance().setElevatorServer(elevatorServerIp);
                            }
                        }
                        //楼栋号
                        String buildNo = anjubaoDSNDto.getBuildNo();
                        if (Valid.valid(buildNo)) {
                            try {
                                Integer.parseInt(buildNo);
                                AppPrefs.getInstance().setBuildNo(buildNo);
                            } catch (Exception e) {
                                AppLogger.e(">>>>>>远程配置:楼栋号不是正整数");
                            }
                        }
                        //单元号
                        String cellNo = anjubaoDSNDto.getCellNo();
                        if (Valid.valid(cellNo)) {
                            try {
                                Integer.parseInt(cellNo);
                                AppPrefs.getInstance().setCellNo(cellNo);
                            } catch (Exception e) {
                                AppLogger.e(">>>>>>远程配置:单元号不是正整数");
                            }
                        }
                        //本地ip
                        String ip = anjubaoDSNDto.getLocalIp();
                        String netMask = anjubaoDSNDto.getLocalNetMask();
                        String gateway = anjubaoDSNDto.getLocalGateway();
                        if (Valid.valid(ip) && Valid.valid(netMask) && Valid.valid(gateway)) {
                            String localIp = CommonUtils.getIPAddress();
                            String localNetMask = CommonUtils.getNetMask();
                            String localGateWay = CommonUtils.getGatewayForStatic();
                            if (!ip.equals(localIp) || !netMask.equals(localNetMask) || !gateway.equals(localGateWay)) {
                                AppLogger.e("设置ip=" + ip + ",netMask=" + netMask + ",localGateway=" + gateway);
                                setIp(ip, netMask, gateway);
                            }
                        }


                        Flowable.just(0)
                                .observeOn(Schedulers.io())
                                .subscribe(l->{
                                    try {
                                        //设备登陆
                                        Call<BaseResponse<DeviceLoginRes>> deviceLogin = RetrofitManager.getInstance()
                                                .postDeviceLogin(deviceSn);
                                        Response<BaseResponse<DeviceLoginRes>> responseBody = deviceLogin.execute();
                                        if (Valid.valid(responseBody) && Valid.valid(responseBody.body())){
                                            if (responseBody.body().code==200){
                                                DeviceLoginRes deviceLoginRes=responseBody.body().data;
                                                if (Valid.valid(deviceLoginRes)){
                                                    AppPrefs.getInstance().setParkingId(deviceLoginRes.getParkingLotId()+"");
                                                    AppPrefs.getInstance().setTerminalId(deviceLoginRes.getDeviceId()+"");
                                                    //设置系统时间
                                                    SystemClock.setCurrentTimeMillis(deviceLoginRes.getTime());    //需要系统权限
                                                }
                                                AppLogger.e(">>>>>>>deviceLogin:"+JSON.toJSONString(deviceLoginRes));
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                },e->{
                                    e.printStackTrace();
                                });

                        BaseResponse baseResponse2=new BaseResponse();
                        baseResponse2.code=0;
                        baseResponse2.datetime=DateTimeUtils.getFormatedDataString();
                        response.send(JSON.toJSONString(baseResponse2));

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

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case POST_HUAXIA_URL:
                AppLogger.e(">>>>>>>>>>getContentType:"+request.getBody().getContentType());

                StringParser.forcedCharset=Charset.forName("UTF-8");

                UrlEncodedFormBody urlEncodedFormBody=request.getBody();
                Multimap multimap= urlEncodedFormBody.get();
                String carNo= multimap.getString("car_plate");
                AppLogger.e(">>>>>>"+carNo);
                String color=multimap.getString("color");
                String vehicleType=multimap.getString("VehicleType");
                //图片base64字符串被decode之后 + 号被替换成空格了，需还原回来
                String picture=multimap.getString("picture").replaceAll(" ","+");
                AppLogger.e(">>>>>>picture:"+picture);

                String closeup_pic=multimap.getString("closeup_pic").replaceAll(" ","+");
                String park_id=multimap.getString("park_id");

                PlateEvent plateEvent=new PlateEvent();
               CameraInfoEntity cameraInfoEntity= cameraInfoEntityBox.query().equal(CameraInfoEntity_.portName,park_id).build().findFirst();
                plateEvent.setDeviceIp(cameraInfoEntity.getCameraIp());
                plateEvent.setNumber(carNo);
                plateEvent.setPlateColor(color);
                plateEvent.setVehicleColor(vehicleType);
                plateEvent.setPlateConfidence(90);
                plateEvent.setPicdata(Base64.decode(picture,Base64.DEFAULT));

                EventBus.getDefault().post(plateEvent);

                break;
            case POST_QIANYI_URL:
                AppLogger.e(">>>>>>>>>>getContentType:"+request.getBody().getContentType());
                /////ParkAPI/sendScanCar
//                StringParser.forcedCharset=Charset.forName("UTF-8");
                StringParser.forcedCharset=Charset.forName("GBK");
                Object body=request.getBody().get();
                QYPlateInfo qyPlateInfo = JSON.parseObject(body.toString(), QYPlateInfo.class);
                AppLogger.e(">>>>qyPlateInfo:"+qyPlateInfo.getAlarmInfoPlate().getResult().getPlateResult().getLicense()+"    "+qyPlateInfo.getAlarmInfoPlate().getResult().getPlateResult().getImageFile().length());
                AlarmInfoPlate alarmInfoPlate=qyPlateInfo.getAlarmInfoPlate();
                PlateResult plateResult=alarmInfoPlate.getResult().getPlateResult();
                PlateEvent plateEvent1=new PlateEvent();
                plateEvent1.setDeviceIp(alarmInfoPlate.getIpaddr());
                plateEvent1.setNumber(plateResult.getLicense());
                plateEvent1.setPlateConfidence(plateResult.getConfidence());
                plateEvent1.setPicdata(Base64.decode(plateResult.getImageFile(),Base64.DEFAULT));
                try {
                    plateEvent1.setPlateColor(QianYiPlateColorType.getDescByType(plateResult.getColorType()));
                    plateEvent1.setVehicleColor("普通车牌");
                }catch (Exception e){
                    e.printStackTrace();
                    plateEvent1.setPlateColor("未知");
                    plateEvent1.setVehicleColor("未知车牌");
                }
                //EventBus发现车牌事件
                EventBus.getDefault().post(plateEvent1);

                AppLogger.e(">>>>>>>>>>LICENSE:"+plateEvent1.getNumber() );
                String resp="{\n" +
                        "\t\"Response_AlarmInfoPlate\": {\n" +
                        "\t\t\"info\": \"error\",\n" +
                        "        \"content\":\"retransfer_stop\",\n" +
                        "\t\t\"channelNum\": 0,\n" +
                        "\t\t\"serialData\": null\n" +
                        "\t}\n" +
                        "}";
                response.send(resp);
                break;
            case POST_HUOYAN_URL:
                AppLogger.e(">>>>>>>>>>getContentType:"+request.getBody().getContentType());

                break;

            case POST_CUSTOME_URL:
                AppLogger.e(">>>>>>>>>>getContentType:"+request.getBody().getContentType());

                Object body1=request.getBody().get();
                 JSONObject jsonObject=JSON.parseObject(new String(body1.toString().getBytes(),Charset.forName("UTF-8")));
//                String   vehicleLaneKey= jsonObject.get("vehicleLaneKey").toString();

                PlateEvent plateEvent2=new PlateEvent();
                plateEvent2.setDeviceIp(jsonObject.get(ConfigValue.DEVICE_IP).toString());
                plateEvent2.setNumber(jsonObject.get(ConfigValue.LICENSE).toString());
                plateEvent2.setPlateConfidence(Float.parseFloat(jsonObject.get(ConfigValue.CONFIDENCE).toString()));
                try {
                    plateEvent2.setPlateColor(ConfigValue.plantColorMap.get(Integer.parseInt(jsonObject.get(ConfigValue.PLATE_COLOR_TYPE).toString())));
                    plateEvent2.setVehicleColor(ConfigValue.plantTypeMap.get(Integer.parseInt(jsonObject.get(ConfigValue.PLATE_TYPE).toString())));
                }catch (Exception e){
                    e.printStackTrace();
                    plateEvent2.setPlateColor("未知");
                    plateEvent2.setVehicleColor("未知车牌");
                }
                plateEvent2.setPicdata(Base64.decode(jsonObject.get(ConfigValue.IMAGE_FILE).toString(),Base64.DEFAULT));
//
                break;
            case POST_UPLOAD_URL:
                //代理转发至服务器
//                request.getHeaders()
                //获取远端socket
                try{
                    AsyncNetworkSocket asyncSocket= (AsyncNetworkSocket) request.getSocket();
                    AppLogger.e(">>>>>remoteIp:"+asyncSocket.getRemoteAddress().getHostString());
                    Multimap nameValuePairs= request.getHeaders().getMultiMap();

                    String sourceHeader="";
                    Iterator<NameValuePair> nameValuePairIterator=nameValuePairs.iterator();
                    while (nameValuePairIterator.hasNext()){
                        NameValuePair nameValuePair=nameValuePairIterator.next();
                        String name	=(String) nameValuePair.getName();
                        String value = nameValuePair.getValue();
                        sourceHeader+=(name+"-"+value)+";";
//                       AppLogger.e(">>>>header："+name+"="+value);
                    }

                    String baseUrl = AppPrefs.getInstance().getServer();
                    if (!Valid.valid(baseUrl)){
                        baseUrl = CommonConfig.BASE_URL;
                        AppPrefs.getInstance().setServer(CommonConfig.BASE_URL);
                    }
                    if (!baseUrl.endsWith("/")){
                        baseUrl=baseUrl+"/";
                    }
//                    baseUrl="http://192.168.8.3:8080/";
                    AppLogger.e(">>>>>post url:"+baseUrl+"parkingRecord/upload");
//                    AsyncHttpRequest request1=new AsyncHttpRequest(Uri.parse(baseUrl+"parkingRecord/upload"),"GET",request.getHeaders());

                    AsyncHttpPost post = new AsyncHttpPost(baseUrl+"parkingRecord/upload");
                    post.disableProxy();
                    post.setBody(request.getBody());
                    post.setHeader("sourceHost",asyncSocket.getRemoteAddress().getHostString());
                    post.setHeader("deviceSN",CommonUtils.getSN());
                    post.setHeader("Content-Type",request.getHeaders().get("Content-Type"));
                    post.setHeader("sourceHeader",sourceHeader);

//                    AppLogger.e(">>>post Body:"+ JSON.toJSONString(request.getBody().get()));
                    AppLogger.e(">>>post sourceHost:"+asyncSocket.getRemoteAddress().getHostString());
                    AppLogger.e(">>>post sourceHeader:"+sourceHeader);

                    client.getSocketMiddleware().setIdleTimeoutMs(20*1000);//20s超时

                    client.executeString(post, new AsyncHttpClient.StringCallback() {
                        @Override
                        public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                            if (source!=null){
                                AppLogger.e(">>>>respone code:"+source.code()+">>>>>uploadCount:"+uploadCount );
                                if (source.code()==200){
                                    uploadCount++;
                                    if (uploadCount>=9999){
                                        uploadCount=0;
                                    }
                                }
                            }else{
                                AppLogger.e(">>>>>respone null please check network");
                            }

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case POST_UPLOAD_IMAGE_URL:
                try{

                    //将图片存储到FTP目录下，FTP目录触发自动上传
                    String filePath=Constants.FTP_DEF_PATH+ UUID.fastUUID().toString()+".json";
                    AppLogger.e(">>>>收到PC图片，存储路径为>>:"+filePath);
//                    JSONObject jsonObject=JSON.parseObject();
                    Object bodyJson=request.getBody().get();
                    FileUtil.writeBytes(bodyJson.toString().getBytes(),filePath);

                    BaseResponse baseResponseImage=new BaseResponse();
                    baseResponseImage.code=0;
                    baseResponseImage.datetime=DateTimeUtils.getFormatedDataString();
                    response.send(JSON.toJSONString(baseResponseImage));

                }catch (Exception e){
                    e.printStackTrace();
                    AppLogger.e(">>>>收到PC图片，写文件发生异常>>:"+e.getMessage());
                }
                break;
        }
    }



    // 执行指定命令
    public static String execCommand(String... command) {
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        String result = "";

        try {
            process = new ProcessBuilder().command(command).start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            result = new String(baos.toByteArray());
            if (inIs != null)
                inIs.close();
            if (errIs != null)
                errIs.close();
            process.destroy();
        } catch (IOException e) {
            AppLogger.e( ">>>>>:"+e.getLocalizedMessage());
            result = e.getMessage();
        }
        return result;
    }

    private void setIp(String localIp, String mask, String localGateway) {
        int maskInt = NetUtil.maskStr2InetMask(mask);
        String ipAndMask = localIp + "/" + maskInt;
        String localDNS = "114.114.114.114";
        ContentResolver contentResolver = getContentResolver();
        Settings.System.putInt(contentResolver, "ethernet_use_static_ip", 1);

        Settings.System.putString(contentResolver, "ethernet_static_ip", ipAndMask);
        Settings.System.putString(contentResolver, "ethernet_static_gateway", localGateway);
        Settings.System.putString(contentResolver, "ethernet_static_netmask", mask);
        Settings.System.putString(contentResolver, "ethernet_static_dns1", localDNS);
        compositeDisposable.add(Flowable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .onBackpressureLatest()
                .subscribe(integer -> NetUtil.setIp(ipAndMask, localDNS, localGateway), e -> {
                    e.printStackTrace();
                    ToastUtils.getShortToastByString(this, "设置失败！请输入正确的IP信息");
                }));
//            AppPrefs.getInstance().set
//            AppPrefs.getInstance().setNetMask(mask);
    }
}
