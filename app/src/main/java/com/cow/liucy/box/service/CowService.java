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

import com.cow.liucy.box.service.dns.CowDSNDto;
import com.cow.liucy.box.service.dns.DeviceInfo;
import com.cow.liucy.box.ui.MainActivity;
import com.cow.liucy.face.BuildConfig;
import com.cow.liucy.face.R;
import com.cow.liucy.libcommon.api.CommonConfig;
import com.cow.liucy.libcommon.api.http.RetrofitManager;
import com.cow.liucy.libcommon.api.http.model.BaseResponse;
import com.cow.liucy.libcommon.api.http.model.DeviceLoginRes;
import com.cow.liucy.libcommon.api.http.model.HeartBeatReq;
import com.cow.liucy.libcommon.api.http.model.HeartBeatResp;

import com.cow.liucy.libcommon.logger.AppLogger;

import com.cow.liucy.libcommon.utils.AppPrefs;
import com.cow.liucy.libcommon.utils.CommonUtils;
import com.cow.liucy.libcommon.utils.Constants;
import com.cow.liucy.libcommon.utils.DateTimeUtils;
import com.cow.liucy.libcommon.utils.FileUtils;
import com.cow.liucy.libcommon.utils.NetUtil;
import com.cow.liucy.libcommon.utils.ToastUtils;
import com.cow.liucy.libcommon.utils.Valid;

import com.cow.liucy.libcommon.huoyan.rxnetty.VzenithNettyManager;

import com.alibaba.fastjson.JSON;
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
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;



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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by cow on 2019-04-18.
 */

public class CowService extends Service implements HttpServerRequestCallback {

    /**
     * 火眼SDK列表 IP《=》SDK
     */
    private Map<String,VzenithNettyManager> vzenithNettyManagerMap=new HashMap<>();

    private CompositeDisposable compositeDisposable;
    private     volatile boolean isFinish = false;

    private static int count=0;
    private AsyncHttpServer server;
    private boolean startFlag = false;
    final String[] getUrlList = new String[]{HELLO};
    final String[] urlList = new String[]{FILE,HTTP_SET,HTTP_RESET,POST_UPLOAD_URL,POST_UPLOAD_IMAGE_URL,HTTP_DEBUG};
    private static final String HELLO="/status";
    private static final String FILE="/file";
    private static final String HTTP_SET="/set";
    private static final String HTTP_DEBUG="/debug";
    private static final String HTTP_RESET="/reset";

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
//                    EventBus.getDefault().post(new FileUploadEvent(file.getPath()));
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
//            initVzenithNettyManager();

            stopHttpServer();
            initHttpServer();
            startHttpServer();
            stopFTPServer();
            startFTPServer();
//
//            Flowable.just(0)
//                    .observeOn(Schedulers.io())
//                    .subscribe(l->{
//                        try {
//                            //设备登陆
//                            Call<BaseResponse<DeviceLoginRes>> deviceLogin = RetrofitManager.getInstance()
//                                    .postDeviceLogin(AppPrefs.getInstance().getSn().toString());
//                            Response<BaseResponse<DeviceLoginRes>> responseBody = deviceLogin.execute();
//                            if (Valid.valid(responseBody) && Valid.valid(responseBody.body())){
//                                if (responseBody.body().code==200){
//                                    DeviceLoginRes deviceLoginRes=responseBody.body().data;
//                                    if (Valid.valid(deviceLoginRes)){
//                                        AppPrefs.getInstance().setParkingId(deviceLoginRes.getParkingLotId()+"");
//                                        AppPrefs.getInstance().setTerminalId(deviceLoginRes.getDeviceId()+"");
//                                        //设置系统时间
//                                        SystemClock.setCurrentTimeMillis(deviceLoginRes.getTime());    //需要系统权限
//                                    }
//                                    AppLogger.e(">>>>>>>deviceLogin:"+JSON.toJSONString(deviceLoginRes));
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    },e->{
//                        e.printStackTrace();
//                    });
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

//        //HTTP心跳时间同步定时任务
//        Flowable.interval(5, 60*5, TimeUnit.SECONDS)
//                .subscribe(l -> {
//                    if (Valid.valid(AppPrefs.getInstance().getTerminalId())) {
//                        HeartBeatReq heartBeatReq=new HeartBeatReq();
//                        heartBeatReq.setDeviceId(Long.parseLong(AppPrefs.getInstance().getTerminalId()));
//                        heartBeatReq.setLastUpdateTime(DateTimeUtils.getFormatedDataString());
//                        heartBeatReq.setVersion(BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
//                        RetrofitManager.getInstance().postDeviceHeartbeatV2(heartBeatReq)
//                                .subscribe(response->{
//                                    if (response!=null && response.code==200){
//                                            HeartBeatResp heartBeatResp=response.data;
//                                            if (heartBeatResp!=null && heartBeatResp.getType()==1){
//                                                    if (Valid.valid(heartBeatResp.getDownloadUrl()) && !heartBeatResp.getDownloadUrl().equalsIgnoreCase("null")){
//                                                        if (updating==true){
//                                                            return;
//                                                        }
//                                                        updating=true;
//                                                        FileUtils.deleteFile("/sdcard/smartparking/update.apk");
//                                                        AsyncHttpGet request=new AsyncHttpGet(heartBeatResp.getDownloadUrl());
//                                                        AsyncHttpClient.getDefaultInstance().executeFile(request, "/sdcard/smartparking/update.apk", new AsyncHttpClient.FileCallback() {
//                                                            @Override
//                                                            public void onCompleted(Exception e, AsyncHttpResponse response, File result) {
//                                                                if (e != null) {
//                                                                    e.printStackTrace();
//                                                                    return;
//                                                                }
//                                                                AppLogger.e("my file is available at: " + result.getPath());
//                                                                execCommand("pm", "install", "-r", result.getPath());
//                                                            }
//                                                        });
//                                                    }
//                                            }
//                                    }else{
//                                        AppLogger.e(">>>>心跳失败，code:"+response.code);
//                                    }
//                                },e->{
//                                    AppLogger.e(">>>>心跳失败");
//                                    e.printStackTrace();
//                                });
//
//                    }else{
//                        AppLogger.e(">>>设备暂未初始化>>>即将进行设备登陆>>");
//                        try {
//                            //设备登陆
//                            Call<BaseResponse<DeviceLoginRes>> deviceLogin = RetrofitManager.getInstance()
//                                    .postDeviceLogin(AppPrefs.getInstance().getSn().toString());
//                            Response<BaseResponse<DeviceLoginRes>> responseBody = deviceLogin.execute();
//                            if (Valid.valid(responseBody) && Valid.valid(responseBody.body())){
//                                if (responseBody.body().code==200){
//                                    DeviceLoginRes deviceLoginRes=responseBody.body().data;
//                                    if (Valid.valid(deviceLoginRes)){
//                                        AppPrefs.getInstance().setParkingId(deviceLoginRes.getParkingLotId()+"");
//                                        AppPrefs.getInstance().setTerminalId(deviceLoginRes.getDeviceId()+"");
//                                        //设置系统时间
//                                        SystemClock.setCurrentTimeMillis(deviceLoginRes.getTime());    //需要系统权限
//                                    }
//                                    AppLogger.e(">>>>>>>deviceLogin:"+JSON.toJSONString(deviceLoginRes));
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, e -> {
//                    AppLogger.e(">>>e:" + e.getMessage());
//                });


        return super.onStartCommand(intent, flags, startId);
    }

    //启动Ftp Server
    private void startFTPServer(){
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
    }

    //停止FTP Server
    private void stopFTPServer(){
                        if (ftpServer != null) {
                            ftpServer.stop();
                        }
                        ftpServer = null;
                        AppLogger.e(">>>>FTP Server stop success!");
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
                deviceInfo.setTime(DateTimeUtils.getFormatedDataString());
                deviceInfo.setUrl(AppPrefs.getInstance().getServer());
                deviceInfo.setVersion(BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
                deviceInfo.setParkingId(AppPrefs.getInstance().getParkingId());
                deviceInfo.setDeviceId(AppPrefs.getInstance().getTerminalId());
                baseResponse.data=deviceInfo;//"";
                baseResponse.message="广告机";
                response.send(JSON.toJSONString(baseResponse));
                break;
            case FILE:
//                AppLogger.e(">>>>>json:"+request.getBody().getContentType()); ;
                break;
            case HTTP_DEBUG:
                //构建对象
                String json12= null;
                try {
                    json12 = URLDecoder.decode(request.getBody().get().toString(), "UTF-8");
                    AppLogger.e(">>>>>json:" + json12);
                    Object parse1 = JSON.parse(json12);
                    String s = parse1.toString();

                    CowDSNDto cowDSNDto1 = JSON.parseObject(s, CowDSNDto.class);
                    if (Valid.valid(cowDSNDto1)) {
                        //开启调试模式
                        if (cowDSNDto1.getCellNo().equalsIgnoreCase("110")){
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

                    CowDSNDto cowDSNDto1 = JSON.parseObject(s, CowDSNDto.class);
                    if (Valid.valid(cowDSNDto1)) {
                        //重置数据库
                        if (cowDSNDto1.getIsRest() == 1) {
                            AppLogger.e("恢复出厂设置");
                            AppPrefs.getInstance().setServer("");
                            AppPrefs.getInstance().setSn("");
                            AppPrefs.getInstance().setTerminalId("");
                            AppPrefs.getInstance().setParkingId("");


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

                    CowDSNDto cowDSNDto = JSON.parseObject(s, CowDSNDto.class);
                    if (Valid.valid(cowDSNDto)) {
//                if (cowDSNDto.getLocalIp().split("/").length < 2) {
//                    AppLogger.e(">>>>>>>>>设置IP错误，格式不对");
//                    return;
//                }
//                        if (!CommonUtils.getMacAddress().equals(cowDSNDto.getMacAddress())) {
//                            AppLogger.e(">>>>>>远程配置：mac=" + cowDSNDto.getMacAddress() + "不是本机mac,本机=" + CommonUtils.getMacAddress());
//                            return;
//                        }
//                        //开启调试模式
//                        if (cowDSNDto.getCellNo().equalsIgnoreCase("110")){
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
                        String serverIp = cowDSNDto.getServerIp();

                        String ftpPort=cowDSNDto.getElevatorServerIp();
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
                        String deviceSn = cowDSNDto.getDeviceSn();
                        if (Valid.valid(deviceSn)) {
                            try {
                                AppPrefs.getInstance().setSn(deviceSn);
                            } catch (Exception e) {
                                AppLogger.e(">>>>>>远程配置deviceSn失败");
                            }
                        }
                        //梯控服务器
                        String elevatorServerIp = cowDSNDto.getElevatorServerIp();
                        if (Valid.valid(elevatorServerIp) && !elevatorServerIp.equals(AppPrefs.getInstance().getElevatorServer())) {
                            if (elevatorServerIp.split(":").length == 2) {
                                AppPrefs.getInstance().setElevatorServer(elevatorServerIp);
                            }
                        }
                        //楼栋号
                        String buildNo = cowDSNDto.getBuildNo();
                        if (Valid.valid(buildNo)) {
                            try {
                                Integer.parseInt(buildNo);
                                AppPrefs.getInstance().setBuildNo(buildNo);
                            } catch (Exception e) {
                                AppLogger.e(">>>>>>远程配置:楼栋号不是正整数");
                            }
                        }
                        //单元号
                        String cellNo = cowDSNDto.getCellNo();
                        if (Valid.valid(cellNo)) {
                            try {
                                Integer.parseInt(cellNo);
                                AppPrefs.getInstance().setCellNo(cellNo);
                            } catch (Exception e) {
                                AppLogger.e(">>>>>>远程配置:单元号不是正整数");
                            }
                        }
                        //本地ip
                        String ip = cowDSNDto.getLocalIp();
                        String netMask = cowDSNDto.getLocalNetMask();
                        String gateway = cowDSNDto.getLocalGateway();
                        if (Valid.valid(ip) && Valid.valid(netMask) && Valid.valid(gateway)) {
                            String localIp = CommonUtils.getIPAddress();
                            String localNetMask = CommonUtils.getNetMask();
                            String localGateWay = CommonUtils.getGatewayForStatic();
                            if (!ip.equals(localIp) || !netMask.equals(localNetMask) || !gateway.equals(localGateWay)) {
                                AppLogger.e("设置ip=" + ip + ",netMask=" + netMask + ",localGateway=" + gateway);
                                setIp(ip, netMask, gateway);
                            }
                        }

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
    }
}
