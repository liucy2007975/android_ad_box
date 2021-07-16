package com.cow.liucy.libcommon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cow.liucy.libcommon.api.CommonConfig;

public class AppPrefs {

    /**
     * AesKey Aes加解密密钥
     */
    private static final String KEY_HDXM_AEK_KEY = "key_hdxm_aes_key";

    /**
     * 服务器地址
     */
    private static final String KEY_SERVER = "key_server";


    /**
     * 小区编号
     */
    private static final String KEY_CODE = "key_code";
    /**
     * 小区编号
     */
    private static final String DEVICE_SN = "device_sn";




    private static final String PREF_NAME = "app";


    private static AppPrefs instance = null;

    private static final String FTP_PORT = "ftp_sever_port";


    public static synchronized AppPrefs getInstance() {
        if (instance == null) {
            instance = new AppPrefs(Utils.getContext());
        }
        return instance;
    }

    private final SharedPreferences prefs;


    private AppPrefs(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, 0);
    }

    /**
     * 设置FtpPort
     * @param ftpPort
     */
    public void setFtpPort(int ftpPort) {
        prefs.edit().putInt(FTP_PORT, ftpPort).commit();
    }
    /**
     * 获取FtpPort 默认21
     * @param
     */
    public int getFtpPort( ) {
       return prefs.getInt(FTP_PORT, 9990);
    }

    /**
     * 保存AES密钥
     *
     * @param key
     */
    public void setAESkey(String key) {
        prefs.edit().putString(KEY_HDXM_AEK_KEY, key).commit();
    }

    /**
     * 获取AES密钥
     *
     * @return
     */
    public String getAESkey() {
        return prefs.getString(KEY_HDXM_AEK_KEY, "");
    }

    public String getCode() {
        return prefs.getString(KEY_CODE, "");
    }

    public void setCode(String code) {
        prefs.edit().putString(KEY_CODE, code).commit();
    }

    /**
     * 设置服务器地址
     *
     * @param ntpServer
     */
    public void setServer(String ntpServer) {
        prefs.edit().putString(KEY_SERVER, ntpServer).commit();
    }

    /**
     * 读取服务器地址
     *
     * @return
     */
    public String getServer() {
        return prefs.getString(KEY_SERVER, "192.168.8.3");
    }


    public String getSn() {
        return prefs.getString(DEVICE_SN, "");
    }

    public void setSn(String sn) {
        prefs.edit().putString(DEVICE_SN, sn).commit();
    }


}
