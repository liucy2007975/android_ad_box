package com.cow.liucy.hdxm.libcommon.utils;

/**
 * 检测控件是否在0.5秒内被重复点击了
 *
 * @author suzheng
 */
public class ItemClickUtils {
    private static long lastClickTime;

    /**
     * 检测控件是否在0.5秒内被重复点击了
     *
     * @return boolean
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
