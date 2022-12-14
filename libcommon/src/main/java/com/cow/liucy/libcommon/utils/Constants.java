package com.cow.liucy.libcommon.utils;

import android.os.Environment;

import com.cow.liucy.hdxm.libcommon.BuildConfig;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cow on 2017-8-7.
 */

public class Constants {

    public static final String APP_DEF_PATH = Environment.getExternalStorageDirectory().getPath() + "/ad_box/";
    public static final String FTP_DEF_PATH = Environment.getExternalStorageDirectory().getPath() + "/ftp/";
    public static final String APP_DEF_PATH_IMG = Environment.getExternalStorageDirectory().getPath() + "/ad_box/images/";
    public static final String APP_DEF_PATH_FILE = Environment.getExternalStorageDirectory().getPath() + "/ad_box/files/";
//    public static final String PICTURE_PATH = APP_DEF_PATH + "picture/";
    public static final String VIDEO_PATH = APP_DEF_PATH + "video/";
    public static final String AUDIO_PATH = APP_DEF_PATH + "audio/";
    public static final String PICTURE_PATH = APP_DEF_PATH + "picture/";
    public static final String INSTALL_APK_PATH = APP_DEF_PATH + "app/";
    public static final String SYS_CONFIG_PATH = APP_DEF_PATH + "sysconfig/";
    public static final String USB_PATH = "/mnt/usb_storage/USB_DISK2/udisk0/ad_box/";

    /**
     * 是否启用AES加解密数据
     */
    public static boolean enableAES= BuildConfig.enableAES;

    public static final int PAY_CHANNEL_WECHAT = 1001;
    public static final int PAY_CHANNEL_ALIPAY = 2001;

    public static final int FREE_PARK_TIME=1*60;//15*60;//15分钟,

    public static final int FEE_TIME=3*60;//60*60;//每多长时间为一个计费周期;

    public static AtomicBoolean IS_REQUEST_START=new AtomicBoolean(false);//防止重复请求

    public static Integer CMD_SERIAL_NO=1;//APP命令序号从0开始

    public static AtomicInteger atomicInteger=new AtomicInteger(0);//原子自增计数

    public static AtomicInteger atomicIntegerSuccess=new AtomicInteger(0);

    public static AtomicInteger atomicIntegerFaild=new AtomicInteger(0);

    public static AtomicInteger biz10001Int = new AtomicInteger(0);
    public static AtomicInteger biz10001IntSuccess = new AtomicInteger(0);
    public static AtomicInteger biz10001IntFaild = new AtomicInteger(0);


    //远端UDP端口
    public static int localUdpRecPort=55640;
    public static int localUdpPort=55642;
    public static int remoteUdpPort=4000;
    public static String remoteIpAddress="";
    public static boolean isStartSend=false;
    public static String SDP_VIDEO_Y="0000001024";

}
