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

import com.cow.liucy.box.service.dns.CowDSNDto;
import com.cow.liucy.box.service.dns.DeviceInfo;
import com.cow.liucy.box.ui.MainActivity;
import com.cow.liucy.face.BuildConfig;
import com.cow.liucy.face.R;
import com.cow.liucy.libcommon.api.CommonConfig;
import com.cow.liucy.libcommon.api.http.RetrofitManager;
import com.cow.liucy.libcommon.api.http.model.BaseResponse;

import com.cow.liucy.libcommon.logger.AppLogger;

import com.cow.liucy.libcommon.rxnetty.CommandVo;
import com.cow.liucy.libcommon.rxnetty.CowNettyEvent;
import com.cow.liucy.libcommon.utils.AppPrefs;
import com.cow.liucy.libcommon.utils.CommonUtils;
import com.cow.liucy.libcommon.utils.Constants;
import com.cow.liucy.libcommon.utils.DateTimeUtils;
import com.cow.liucy.libcommon.utils.FileUtils;
import com.cow.liucy.libcommon.utils.NetUtil;
import com.cow.liucy.libcommon.utils.ToastUtils;
import com.cow.liucy.libcommon.utils.Valid;

import com.cow.liucy.libcommon.rxnetty.CowNettyManager;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ShellUtils;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.http.AsyncHttpClient;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by cow on 2019-04-18.
 */

public class CowService extends Service implements HttpServerRequestCallback {


    private CowNettyManager cowNettyManager;

    private CompositeDisposable compositeDisposable;
    private     volatile boolean isFinish = false;

    private static int count=0;
    private AsyncHttpServer server;
    private boolean startFlag = false;
    final String[] getUrlList = new String[]{HELLO};
    final String[] urlList = new String[]{FILE,HTTP_SET};
    private static final String HELLO="/status";
    private static final String FILE="/file";
    private static final String HTTP_SET="/set";


    private FtpServer ftpServer;
    private Disposable mDisposableRecreate;

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
//        Flowable.interval(500,TimeUnit.MILLISECONDS)
//                .subscribe(l->{
//                        String dateTime=DateTimeUtils.getFormatedDataString();
//                        if (dateTime.contains(" 02:00:00")){
//                            AppLogger.e(">>>>>>dateTime:>>>>>"+dateTime);
//                            AppLogger.e(">>>>>>设备定时重启>>>>>");
//
//                            try {
//                                //清空/ftp目录
//                                FileUtil.del(Constants.FTP_DEF_PATH);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//                            ShellUtils.execCmd("reboot",false);
//                        }
//
//                },e->{
//                    e.printStackTrace();
//                });

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
                AppLogger.e(">>>>>>>onDirectoryCreate:"+directory.getPath());
        }

        @Override
        public void onDirectoryChange(File directory) {
            AppLogger.e(">>>>>>>onDirectoryChange:"+directory.getPath());

        }

        @Override
        public void onDirectoryDelete(File directory) {
            AppLogger.e(">>>>>>>onDirectoryDelete:"+directory.getPath());

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
            AppLogger.e(">>>>>>>onFileChange:"+file.getPath());
        }

        @Override
        public void onFileDelete(File file) {
            AppLogger.e(">>>>>>>onFileDelete:"+file.getPath());
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

            if (cowNettyManager!=null){
                cowNettyManager.stop();
                cowNettyManager=null;
            }
            cowNettyManager =new CowNettyManager("192.168.8.3",9990);
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

            stopHttpServer();
            initHttpServer();
            startHttpServer();
            stopFTPServer();
            startFTPServer();
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

        stopFTPServer();
    }

    /**
     * 设置Service前台运行，在通知栏显示
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("广告机服务");
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
