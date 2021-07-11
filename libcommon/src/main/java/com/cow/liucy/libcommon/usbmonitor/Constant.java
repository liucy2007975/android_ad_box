package com.cow.liucy.libcommon.usbmonitor;

/**
 * 常量
 */
public class Constant {
    // request参数
    public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
    public static final int REQ_PERM_CAMERA = 11003; // 打开摄像头
    public static final int REQ_PERM_EXTERNAL_STORAGE = 11004; // 读写文件

    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";

    public static final String filename = "/ad_res/config";
    public static final String ACTION_USB_RECEIVER = "com.cow.liucy.monitor_usb";
    public static final String FROM_PATH = "/storage/udisk/ad_res";
    public static final String TARGET_PATH = "/sdcard/ad_res";
    public static final String TARGET_PATH_VIDEO = "/sdcard/ad_res/myvideos";
    public static final String TARGET_PATH_UPDATE = "/sdcard/ad_res/myupdates";

    public static String from_path_root = null;
}
