package com.cow.liucy.box.service.dns;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.cow.liucy.box.service.CowService;
import com.cow.liucy.face.BuildConfig;
import com.cow.liucy.libcommon.api.http.RetrofitManager;
import com.cow.liucy.libcommon.api.http.model.BaseResponse;
import com.cow.liucy.libcommon.api.http.model.DeviceLoginRes;
import com.cow.liucy.libcommon.logger.AppLogger;

import com.cow.liucy.libcommon.utils.AppPrefs;
import com.cow.liucy.libcommon.utils.CommonUtils;
import com.cow.liucy.libcommon.utils.Constants;
import com.cow.liucy.libcommon.utils.FileUtils;
import com.cow.liucy.libcommon.utils.NetUtil;
import com.cow.liucy.libcommon.utils.ToastUtils;
import com.cow.liucy.libcommon.utils.Utils;
import com.cow.liucy.libcommon.utils.Valid;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ShellUtils;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by cow on 2019-03-30.
 */

public class CowDNSService extends Service {

    private CompositeDisposable compositeDisposable;
    private final static int BROADCAST_PORT = 30110;
    private final static String BROADCAST_IP = "239.0.0.155";
    private final static int BROADCAST_PORT2 = 30111;
    private final static String BROADCAST_IP2 = "239.0.0.156";
    private MulticastSocket socket;
    private InetAddress address;
    private MulticastSocket socket1;
    private InetAddress address1;
    private Disposable mDisposableRecreate;
    /**
     * 重试时间间隔
     */
    private final int RECREATE_INTERVAL_TIME = 10;

    private static int UDP_PACKET_COUNT=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        compositeDisposable = new CompositeDisposable();
        AppLogger.e(">>>>组播初始化");
        create();
        //接收组播
        compositeDisposable.add(Flowable.just(0)
                .subscribeOn(Schedulers.io())
                .subscribe(l -> {
                    AppLogger.e(">>>>>>>>开始接收组播");
                    while (true) {
                        receiveMultiBroadcast();
//                        receiveAPK();
                    }
                }, e -> {
                    e.printStackTrace();
                    AppLogger.e(">>>>>>>接收错误：" + e.getMessage());
                }));
        //接收apk
//        compositeDisposable.add(Flowable.just(0)
//                .subscribeOn(Schedulers.io())
//                .subscribe(l->{
//                    AppLogger.e(">>>>>>>>开始接收apk");
//                    while (true) {
//                        receiveAPK();
//                    }
//                },e->{
//                    e.printStackTrace();
//                    AppLogger.e(">>>>>>>接收错误："+e.getMessage());
//                }));

        compositeDisposable.add(Flowable.interval(1, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(l -> {
                    try {
//                        AppLogger.e(">>>>>>>>发送组播...");
                        sendMessage(buildcowDSNDto());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, e -> {
                    e.printStackTrace();
                }));


    }

    private void create(){
        //组播初始化
        try {
            socket = new MulticastSocket(BROADCAST_PORT);
            address = InetAddress.getByName(BROADCAST_IP);
//            socket.setTimeToLive(2);
            socket.joinGroup(address);
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.e(">>>>组播初始化异常:" + e.getMessage());
            reCreate(e);
        }
    }

    /**
     * 不断重新初始化组播
     */
    private void reCreate(Throwable e) {
        AppLogger.e(">>>>recreate e:" + e.getMessage());
        cancelReconnect();
        //定时任务，指定时间内重新连接
        mDisposableRecreate = Flowable.interval(RECREATE_INTERVAL_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(l -> {
                    create();
                });
    }

    /**
     * 取消订阅
     */
    public void cancelReconnect() {
        if (mDisposableRecreate != null && !mDisposableRecreate.isDisposed()) {
            mDisposableRecreate.dispose();
        }
    }

    private void sendMessage(CowDSNDto cowDSNDto) {

        try {
            socket1 = new MulticastSocket(BROADCAST_PORT2);
            address1 = InetAddress.getByName(BROADCAST_IP2);
            socket1.setTimeToLive(2);
            socket1.joinGroup(address1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMultiBroadcast(cowDSNDto);
    }

    private void sendMultiBroadcast(CowDSNDto cowDSNDto) {
        byte[] bytes = JSON.toJSONString(cowDSNDto).getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address1, BROADCAST_PORT2);
        try {
            socket1.send(packet);
//            AppLogger.e(">>>>>>send message succeed 广播发送成功！");
        } catch (IOException e) {
            AppLogger.e(">>>>>>send message error");
            e.printStackTrace();
        }
    }


    /**
     * 生成hashMap对象
     *
     * @param result:判断是否是设置配置响应service 1--响应server设置  0--主动
     */
    private HashMap<String, String> buildHashMap(int result) {
        HashMap<String, String> mDnsMap = new HashMap<>();
        CowDSNDto cowDSNDto = new CowDSNDto();

        cowDSNDto.setLocalIp(CommonUtils.getIPAddress());
        cowDSNDto.setMacAddress(CommonUtils.getMacAddress());
        cowDSNDto.setServerIp(AppPrefs.getInstance().getServer());
        cowDSNDto.setVersion(BuildConfig.VERSION_NAME + BuildConfig.VERSION_CODE);

//        AppLogger.e(">>>>>>>>ajb length>>>" + JSON.toJSONString(cowDSNDto).getBytes().length);
//        AppLogger.e(">>>>>>>>ajb dto>>>" + JSON.toJSONString(cowDSNDto));
        mDnsMap.put("a", cowDSNDto.getMacAddress());
        mDnsMap.put("b", cowDSNDto.getLocalIp());
        mDnsMap.put("c", cowDSNDto.getServerIp());
        mDnsMap.put("d", cowDSNDto.getVersion());
        AppLogger.e(">>>>>>>>>mDnsMap:" + JSON.toJSONString(mDnsMap));
        return mDnsMap;
    }

    /**
     * 生成cowDSNDto对象
     */
    private CowDSNDto buildcowDSNDto() {
        CowDSNDto cowDSNDto = new CowDSNDto();
        cowDSNDto.setLocalIp(CommonUtils.getIPAddress());
//        String mask=NetUtil.getMaskByBit(Integer.parseInt(AppPrefs.getInstance().getNetMask()));
        cowDSNDto.setLocalNetMask(CommonUtils.getNetMask());
        cowDSNDto.setMacAddress(CommonUtils.getMacAddress());
        cowDSNDto.setLocalGateway(CommonUtils.getGatewayForStatic());
        cowDSNDto.setServerIp(AppPrefs.getInstance().getServer());
        cowDSNDto.setElevatorServerIp(AppPrefs.getInstance().getFtpPort()+"");
//        UDP_PACKET_COUNT++;
//        if (UDP_PACKET_COUNT>10000){
//            UDP_PACKET_COUNT=1;
//        }
        cowDSNDto.setBuildNo(CowService.uploadCount +"");
        cowDSNDto.setCellNo(AppPrefs.getInstance().getCellNo());
        cowDSNDto.setVersion(BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
        cowDSNDto.setDeviceSn(AppPrefs.getInstance().getSn());
//        AppLogger.e(">>>>>>>>ajb length>>>" + JSON.toJSONString(cowDSNDto).getBytes().length);
//        AppLogger.e(">>>>>>>>ajb dto>>>" + JSON.toJSONString(cowDSNDto));
//        mDnsMap.put("a", JSON.toJSONString(cowDSNDto));
        return cowDSNDto;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }


    /**
     * 接收组播数据
     */
    private void receiveMultiBroadcast() {
        try {
            byte[] rev = new byte[1024];
            DatagramPacket packet = new DatagramPacket(rev, rev.length);
            socket.receive(packet);
            String receiver = new String(packet.getData(), StandardCharsets.UTF_8).trim();
            AppLogger.e(">>>>组播接收到的信息:" + receiver);
            //构建对象
            CowDSNDto cowDSNDto = JSON.parseObject(receiver, CowDSNDto.class);
            if (Valid.valid(cowDSNDto)) {
//                if (cowDSNDto.getLocalIp().split("/").length < 2) {
//                    AppLogger.e(">>>>>>>>>设置IP错误，格式不对");
//                    return;
//                }
                if (!CommonUtils.getMacAddress().equals(cowDSNDto.getMacAddress())) {
                    AppLogger.e(">>>>>>远程配置：mac=" + cowDSNDto.getMacAddress() + "不是本机mac,本机=" + CommonUtils.getMacAddress());
                    return;
                }
                //重置数据库
                if (cowDSNDto.getIsRest() == 1) {
                    AppLogger.e("恢复出厂设置");
                    AppPrefs.getInstance().setServer("");
                    AppPrefs.getInstance().setSn("");
                    AppPrefs.getInstance().setTerminalId("");
                    AppPrefs.getInstance().setParkingId("");

//                    FileUtils.deleteAllFile(new File(Constants.PICTURE_PATH));


                    ShellUtils.execCmd("reboot",false);

                    return;
                }
                //开启调试模式
                if (cowDSNDto.getCellNo().equalsIgnoreCase("110")){

                    CommandResult resultSetporp = Shell.run("setprop service.adb.tcp.port 5555");
                    CommandResult resultStop = Shell.run("stop adbd");
                    CommandResult resultStart = Shell.run("start adbd");
                    if (resultSetporp.isSuccessful()) {
                        AppLogger.e(">>>>" + resultSetporp.getStdout());
                    }

                }

                //重启
                if (cowDSNDto.getIsRestart() == 1) {
//                    AppUtils.stopProtoNetty();

                    //设置系统初始化标志位
//                    String pkName = Utils.getContext().getPackageName();
//                    AppLogger.e(">>>>>pkName:"+pkName);
                    //重启应用
//                    doStartApplicationWithPackageName(pkName);
//                    android.os.Process.killProcess(android.os.Process.myPid());
                    ShellUtils.execCmd("reboot",false);
                    return;
                }
                //服务器ip
                String serverIp = cowDSNDto.getServerIp();
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

                Thread.sleep(20000);
                ShellUtils.execCmd("reboot",false);

            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.e(">>>>>>>组播接收失败：" + e.getMessage());
        } finally {
//            try {
//                Thread.sleep(1500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(disposable1!=null && !disposable1.isDisposed()){
//                disposable1.dispose();
//                disposable1=null;
//            }
        }
    }


    public static void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = Utils.getContext().getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = Utils.getContext().getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            Utils.getContext().startActivity(intent);
        }
    }

//    private void receiveAPK() {
//        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
//
//        DatagramPacket dpk = null;
//        DatagramSocket dsk = null;
//        BufferedOutputStream bos = null;
//        try {
//            File apkDir = new File(APP_DEF_PATH_FILE);
//            boolean orExistsDir = FileUtils.createOrExistsDir(apkDir);
//            if (!orExistsDir) {
//                apkDir.mkdirs();
//            }
//            SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
//            String dateStr = format.format(System.currentTimeMillis());
//            dpk = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName(CommonUtils.getIPAddress()), UDPUtils.PORT));
//            dsk = new DatagramSocket(UDPUtils.PORT + 1, InetAddress.getByName(CommonUtils.getIPAddress()));
//            AppLogger.e("ip=" + CommonUtils.getIPAddress() + ",等待连接....");
//            dsk.receive(dpk);
//            bos = new BufferedOutputStream(new FileOutputStream(APP_DEF_PATH_FILE + dateStr + ".apk"));
//
//            int readSize = 0;
//            int readCount = 0;
//            int flushSize = 0;
//            while ((readSize = dpk.getLength()) != 0) {
//                if (UDPUtils.isEqualsByteArray(UDPUtils.exitData, buf, readSize)) {
//                    AppLogger.e("apk接收完毕...");
//                    // send exit flag
//                    dpk.setData(UDPUtils.exitData, 0, UDPUtils.exitData.length);
//                    dsk.send(dpk);
//                    //安装apk
//                    AppLogger.e(">>>>>>>远程安装apk:" + APP_DEF_PATH_FILE + dateStr + ".apk");
//                    boolean install = install(APP_DEF_PATH_FILE + dateStr + ".apk");
//                    break;
//                }
//
//                bos.write(buf, 0, readSize);
//                if (++flushSize % 1000 == 0) {
//                    flushSize = 0;
//                    bos.flush();
//                }
//                dpk.setData(UDPUtils.successData, 0, UDPUtils.successData.length);
//                dsk.send(dpk);
//
//                dpk.setData(buf, 0, buf.length);
//                System.out.println("apk接收片段:" + (++readCount) + " !");
//                dsk.receive(dpk);
//            }
//
//            bos.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (bos != null)
//                    bos.close();
//                if (dsk != null)
//                    dsk.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    /**
//     * 测试udp发送apk
//     *
//     * @param filePath
//     */
//    private void sendApk(String filePath) {
//        AppLogger.e("apk发送 ...");
//        long startTime = System.currentTimeMillis();
//
//        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
//        byte[] receiveBuf = new byte[1];
//
//        RandomAccessFile accessFile = null;
//        DatagramPacket dpk = null;
//        DatagramSocket dsk = null;
//        int readSize = -1;
//        try {
//            accessFile = new RandomAccessFile(filePath, "r");
//            dpk = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName("192.168.41.123"), UDPUtils.PORT + 1));
//            dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("192.168.41.123"));
//            int sendCount = 0;
//            while ((readSize = accessFile.read(buf, 0, buf.length)) != -1) {
//                dpk.setData(buf, 0, readSize);
//                dsk.send(dpk);
//                // wait server response
//                {
//                    while (true) {
//                        dpk.setData(receiveBuf, 0, receiveBuf.length);
//                        dsk.receive(dpk);
//
//                        // confirm server receive
//                        if (!UDPUtils.isEqualsByteArray(UDPUtils.successData, receiveBuf, dpk.getLength())) {
//                            AppLogger.e("apk重新发送 ...");
//                            dpk.setData(buf, 0, readSize);
//                            dsk.send(dpk);
//                        } else
//                            break;
//                    }
//                }
//
//                System.out.println("apk发送片段 " + (++sendCount) + "!");
//            }
//            // send exit wait server response
//            while (true) {
//                System.out.println("client send exit message ....");
//                dpk.setData(UDPUtils.exitData, 0, UDPUtils.exitData.length);
//                dsk.send(dpk);
//
//                dpk.setData(receiveBuf, 0, receiveBuf.length);
//                dsk.receive(dpk);
//                if (!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())) {
//                    System.out.println("client Resend exit message ....");
//                    dsk.send(dpk);
//                } else
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (accessFile != null)
//                    accessFile.close();
//                if (dsk != null)
//                    dsk.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//        AppLogger.e("time:" + (endTime - startTime));
//
//    }

    private void setIp(String localIp, String mask, String localGateway) {
        int maskInt = NetUtil.maskStr2InetMask(mask);
        String ipAndMask = localIp + "/" + maskInt;
        String localDNS = "8.8.8.8";
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

    /**
     * 应用程序安装
     */
    private boolean install(String filePath) {
        AppLogger.e("开始执行安装: " + filePath);
        Uri uri = Uri.fromFile(new File(filePath));
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(localIntent);
        return true;
    }

}
