package com.cow.liucy.libcommon.usbmonitor;

public class VolumeInfo {
    /**
     * U盘挂载类型  插入U盘 STATE_UNMOUNTED->STATE_MOUNTED;拔出U盘  STATE_EJECTING->STATE_UNMOUNTED->STATE_BAD_REMOVAL
     */
    //卸载
    public static final int STATE_UNMOUNTED = 0;
    //状态监测
    public static final int STATE_CHECKING = 1;
    //挂载完成
    public static final int STATE_MOUNTED = 2;
    //只读
    public static final int STATE_MOUNTED_READ_ONLY = 3;
    //格式化
    public static final int STATE_FORMATTING = 4;
    //开始移除
    public static final int STATE_EJECTING = 5;
    //无法挂载
    public static final int STATE_UNMOUNTABLE = 6;
    //删除
    public static final int STATE_REMOVED = 7;
    //移除完成
    public static final int STATE_BAD_REMOVAL = 8;
    /**
     * 接受广播key
     */
    public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";
    public static final String EXTRA_VOLUME_ID = "android.os.storage.extra.VOLUME_ID";
    public static final String EXTRA_VOLUME_STATE = "android.os.storage.extra.VOLUME_STATE";
    /**
     * 刚挂载和关闭
     */
    public static final String ACTION_USB_DEVICE_ATTACHED= "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DEVICE_DETACHED= "android.hardware.usb.action.USB_DEVICE_DETACHED";
}