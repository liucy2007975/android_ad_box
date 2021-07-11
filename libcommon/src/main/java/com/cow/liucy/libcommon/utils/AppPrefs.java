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
     * 当前设备编码
     */
    private static final String KEY_HDXM_DEVICE_NO = "key_hdxm_device_no";
    /**
     * 车位总数
     */
    private static final String KEY_PARKING_PRESENT_TOTAL = "key_parking_present_total";
    /**
     * Http接口请求头
     */
    private static final String KEY_HEADER_PARKING_ID = "key_header_parking_id";
    /**
     * ParkName
     */
    private static final String KEY_PARKING_NAME = "key_parking_name";
    /**
     * 固件版本号
     */
    private static final String KEY_FIRMWARE_VERSION = "key_firmware_version";
    /**
     * 文件HTTP服务器地址
     */
    private static final String KEY_FILE_SERVER_URL = "key_file_server_url";
    /**
     * 图片HTTP服务器地址
     */
    private static final String KEY_IMAGE_URL = "key_image_url";
    /**
     * 服务器地址
     */
    private static final String KEY_SERVER = "key_server";

    /**
     * 服务器地址
     */
    private static final String KEY_HTTP_SERVER = "key_http_server";
    /**
     * NTP服务器地址
     */
    private static final String KEY_NTP_SERVER = "key_ntp_server";
    /**
     * 梯控服务器地址
     */
    private static final String KEY_ELEVATOR_SERVER = "key_elevator_server";

    /**
     * 门禁 楼栋号
     */
    private static final String KEY_BUILD_NO = "key_build_no";
    /**
     * 门禁 单元号
     */
    private static final String KEY_CELL_NO = "key_cell_no";

    /**
     * 文件在文件服务器上的ID
     */
    private static final String KEY_FILE_ID = "key_file_id";
    /**
     * 批量操作固定凭证信息操作时间
     */
    private static final String KEY_OPTIME = "key_optime";
    /**
     * 保存网关下发的包序号
     */
    private static final String KEY_PACKAGE_NUM = "key_package_num";


    //  初始化 设置车场控制器
    /**
     * 车场控制器 网关ip
     */
    private static final String KEY_PARK_CONTROL_SERVER_IP = "key_park_control_server_ip";
    /**
     * 车场控制器 名称
     */
    private static final String KEY_PARK_CONTROL_NAME = "key_park_control_name";

    //初始化 设置车场相机
    /**
     * 相机IP
     */
    private static final String KEY_PARK_CAMERA_IP = "key_park_camera_ip";
    /**
     * 相机名称
     */
    private static final String KEY_PARK_CAMERA_NAME = "key_park_camera_name";
    /**
     * 轻量级网关ip
     */
    private static final String KEY_LIGHT_IP = "key_light_ip";
    /**
     * 轻量级网关port
     */
    private static final String KEY_LIGHT_PORT = "key_light_port";
    /**
     * 国标网关ip
     */
    private static final String KEY_SIP_SERVER_IP = "key_sip_server_ip";
    /**
     * 国标网关port
     */
    private static final String KEY_SIP_SERVER_PORT = "key_sip_server_port";
    /**
     * SIP服务器ID
     */
    private static final String KEY_SIP_SERVER_ID = "key_sip_server_id";
    /**
     * SIP服务器密码
     */
    private static final String KEY_SIP_SERVER_PASSWORD = "key_sip_server_password";
    /**
     * SIP服务器Realm
     */
    private static final String KEY_SIP_SERVER_REALM = "key_sip_server_realm";
    /**
     * 是否处于激活状态（用于恢复出厂设置）
     */
    private static final String KEY_DEVICE_RESET = "key_device_reset";

    /**
     * 保存广告
     */
    private static final String KEY_LOAD_LED = "key_load_led";

    /**
     * 保存广告
     */
    private static final String KEY_DEVICE_DIRECT = "key_device_direct";

    /**
     * 小区编号
     */
    private static final String KEY_CODE = "key_code";
    /**
     * 小区编号
     */
    private static final String DEVICE_SN = "device_sn";

    /**
     * 是否探测手温度模块
     */
    private static final String KEY_OPEN_TEMP_MODULE = "open_temp_module";

    /**
     * localNetMask子网掩码
     */
    private static final String KEY_LOCAL_NETMASK = "key_local_netmask";

    private static final String KEY__ADD_TEMP = "key_add_temp";

    private static final String PREF_NAME = "app";

    private static final String RECOGNITION_ENABLE = "recognition_enable";

    private static AppPrefs instance = null;

    private static final String FTP_PORT = "ftp_sever_port";

    /**
     * 终端ID
     */
    private static final String KEY_PARKING_TERMINAL_ID = "key_parking_terminal_id";

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
       return prefs.getInt(FTP_PORT, 2121);
    }

    /**
     * 设置TerminalId
     * @param terminalId
     */
    public void setTerminalId(String terminalId) {
        prefs.edit().putString(KEY_PARKING_TERMINAL_ID, terminalId).commit();
    }

    /**
     * 获取TerminalId
     * @return
     */
    public String getTerminalId() {
        return prefs.getString(KEY_PARKING_TERMINAL_ID, "");
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

    public float getTemp() {
        return prefs.getFloat(KEY__ADD_TEMP, 0);
    }

    public void setTemp(float temp) {
        prefs.edit().putFloat(KEY__ADD_TEMP, temp).commit();
    }

    public String getBuildNo() {
        return prefs.getString(KEY_BUILD_NO, "0");
    }

    public void setBuildNo(String buildNo) {
        prefs.edit().putString(KEY_BUILD_NO, buildNo).commit();
    }

    public String getCellNo() {
        return prefs.getString(KEY_CELL_NO, "0");
    }

    public void setCellNo(String cellNo) {
        prefs.edit().putString(KEY_CELL_NO, cellNo).commit();
    }


    /**
     * 设置系统是否需要初始化
     *
     * @param result
     */
    public void setSystemNeedInit(boolean result) {
        prefs.edit().putBoolean(KEY_HDXM_DEVICE_NO, result).commit();
    }

    /**
     * 获取系统是否需要初始化
     *
     * @return
     */
    public boolean getSystemNeedInit() {
        return prefs.getBoolean(KEY_HDXM_DEVICE_NO, true);
    }

    /**
     * 设置ParkingId
     *
     * @param parkingId
     */
    public void setParkingId(String parkingId) {
        prefs.edit().putString(KEY_HEADER_PARKING_ID, parkingId).commit();
    }

    /**
     * 获取ParkingId
     *
     * @return
     */
    public String getParkingId() {
        return prefs.getString(KEY_HEADER_PARKING_ID, "");
    }

    /**
     * 设置parkingName
     *
     * @param parkingName
     */
    public void setParkingName(String parkingName) {
        prefs.edit().putString(KEY_PARKING_NAME, parkingName).commit();
    }

    /**
     * 获取ParkingPresentTotal
     *
     * @return
     */
    public String getParkingName() {
        return prefs.getString(KEY_PARKING_NAME, "");
    }

    /**
     * 设置ParkingPresentTotal
     *
     * @param parkingPresentTotal
     */

    public void setParkingPresentTotal(long parkingPresentTotal) {
        prefs.edit().putLong(KEY_PARKING_PRESENT_TOTAL, parkingPresentTotal).commit();
    }

    /**
     * 获取ParkingPresentTotal
     *
     * @return
     */
    public long getParkingPresentTotal() {
        return prefs.getLong(KEY_PARKING_PRESENT_TOTAL, 0);
    }

    /**
     * 设置固件版本号
     */
    public void setFirmwareVersion(String firmwareVersion) {
        prefs.edit().putString(KEY_FIRMWARE_VERSION, firmwareVersion).commit();
    }

    /**
     * 获取固件版本号
     *
     * @return
     */
    public String getFirmwareVersion() {
        return prefs.getString(KEY_FIRMWARE_VERSION, "");
    }

    /**
     * 设置文件HTTP服务器地址
     */
    public void setFileServerUrl(String fileServerUrl) {
        prefs.edit().putString(KEY_FILE_SERVER_URL, fileServerUrl).commit();
    }

    /**
     * 读取文件HTTP服务器地址
     *
     * @return
     */
    public String getFileServerUrl() {
        return prefs.getString(KEY_FILE_SERVER_URL, "");
    }

    /**
     * 设置图片HTTP服务器地址
     */
    public void setImageServerUrl(String fileImageUrl) {
        prefs.edit().putString(KEY_IMAGE_URL, fileImageUrl).commit();
    }

    /**
     * 读取图片HTTP服务器地址
     *
     * @return
     */
    public String getImageServerUrl() {
        return prefs.getString(KEY_IMAGE_URL, "");
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
        return prefs.getString(KEY_SERVER, "");
    }

    /**
     * 设置http服务器地址
     *
     * @param httpServer
     */
    public void setHttpServer(String httpServer) {
        prefs.edit().putString(KEY_HTTP_SERVER, httpServer).commit();
    }

    /**
     * 读取http服务器地址
     *
     * @return
     */
    public String getHttpServer() {
        if (Valid.valid(prefs.getString(KEY_HTTP_SERVER, ""))) {
            return prefs.getString(KEY_HTTP_SERVER, "");
        }
        return CommonConfig.BASE_URL;
    }

    /**
     * 设置NetMask
     */
    public void setNetMask(String netMask) {
        prefs.edit().putString(KEY_LOCAL_NETMASK, netMask).commit();
    }

    /**
     * 获取NetMask
     */
    public String getNetMask() {
        return prefs.getString(KEY_LOCAL_NETMASK, "24");
    }

    /**
     * 设置NTP服务器地址
     *
     * @param ntpServer
     */
    public void setNtpServer(String ntpServer) {
        prefs.edit().putString(KEY_NTP_SERVER, ntpServer).commit();
    }

    /**
     * 读取NTP服务器地址
     *
     * @return
     */
    public String getNtpServer() {
        return prefs.getString(KEY_NTP_SERVER, "");
    }

    /**
     * 设置梯控服务器地址
     *
     * @param ntpServer
     */
    public void setElevatorServer(String ntpServer) {
        prefs.edit().putString(KEY_ELEVATOR_SERVER, ntpServer).commit();
    }

    /**
     * 读取梯控服务器地址
     *
     * @return
     */
    public String getElevatorServer() {
        return prefs.getString(KEY_ELEVATOR_SERVER, "");
    }

    /**
     * 设置文件在文件服务器上的ID
     *
     * @param fileId
     */
    public void setFileId(String fileId) {
        prefs.edit().putString(KEY_FILE_ID, fileId).commit();
    }

    /**
     * 读取文件在文件服务器上的ID
     *
     * @return
     */
    public String getFileId() {
        return prefs.getString(KEY_FILE_ID, "");
    }

    /**
     * 设置批量操作固定凭证信息操作时间
     *
     * @param optime
     */
    public void setOptime(String optime) {
        prefs.edit().putString(KEY_OPTIME, optime).commit();
    }

    /**
     * 读取批量操作固定凭证信息操作时间
     *
     * @return
     */
    public String getOptime() {
        return prefs.getString(KEY_OPTIME, "");
    }

    /**
     * 读取保存的网关下发的包号
     *
     * @return
     */
    public int getPackageNum() {
        return prefs.getInt(KEY_PACKAGE_NUM, 0);
    }

    /**
     * 设置网关下发的包号
     *
     * @param packageNum
     */
    public void setPackageNum(int packageNum) {
        prefs.edit().putInt(KEY_PACKAGE_NUM, packageNum).commit();
    }

    /**
     * 读取初始化的车场控制器 服务器ip
     *
     * @return
     */
    public String getInitParkControlServerIp() {
        return prefs.getString(KEY_PARK_CONTROL_SERVER_IP, CommonUtils.getIPAddress());
    }

    /**
     * 设置初始化的车场控制器 服务器ip
     *
     * @param parkControlServerIp
     */
    public void setInitParkControlServerIp(String parkControlServerIp) {
        prefs.edit().putString(KEY_PARK_CONTROL_SERVER_IP, parkControlServerIp).commit();
    }

    /**
     * 读取初始化的车场控制器名称
     *
     * @return
     */
    public String getInitParkControlName() {
        return prefs.getString(KEY_PARK_CONTROL_NAME, "安居宝车场控制器");
    }

    /**
     * 设置初始化的车场控制器名称
     *
     * @param parkControlName
     */
    public void setInitParkControlName(String parkControlName) {
        prefs.edit().putString(KEY_PARK_CONTROL_NAME, parkControlName).commit();
    }


    /**
     * 读取初始化的车场相机名称
     *
     * @return
     */
    public String getInitParkCameraName() {
        return prefs.getString(KEY_PARK_CAMERA_NAME, "安居宝车场车牌识别器");
    }

    /**
     * 设置初始化的车场相机名称
     *
     * @param parkCameraName
     */
    public void setInitParkCameraName(String parkCameraName) {
        prefs.edit().putString(KEY_PARK_CAMERA_NAME, parkCameraName).commit();
    }

    /**
     * 读取初始化的车场相机ip
     *
     * @return
     */
    public String getInitParkCameraIp() {
        return prefs.getString(KEY_PARK_CAMERA_IP, CommonUtils.getIPAddress());
    }

    /**
     * 设置初始化的车场相机ip
     *
     * @param parkCameraIp
     */
    public void setInitParkCameraIp(String parkCameraIp) {
        prefs.edit().putString(KEY_PARK_CAMERA_IP, parkCameraIp).commit();
    }

    /**
     * 读取轻量级网关ip
     *
     * @return
     */
    public String getLightIp() {
        return prefs.getString(KEY_LIGHT_IP, "");
    }

    /**
     * 设置轻量级网关ip
     */
    public void setLightIp(String lightIp) {
        prefs.edit().putString(KEY_LIGHT_IP, lightIp).commit();
    }

    /**
     * 读取轻量级网关port
     *
     * @return
     */
    public String getLightPort() {
        return prefs.getString(KEY_LIGHT_PORT, "");
    }

    /**
     * 设置轻量级网关port
     */
    public void setLightPort(String lightPort) {
        prefs.edit().putString(KEY_LIGHT_PORT, lightPort).commit();
    }

    /**
     * 读取国标网关ip
     *
     * @return
     */
    public String getSipServerIp() {
        return prefs.getString(KEY_SIP_SERVER_IP, "192.168.8.3");
    }

    /**
     * 设置国标网关ip
     */
    public void setSipServerIp(String nationalIp) {
        prefs.edit().putString(KEY_SIP_SERVER_IP, nationalIp).commit();
    }

    /**
     * 读取国标网关port
     *
     * @return
     */
    public int getSipServerPort() {
        return prefs.getInt(KEY_SIP_SERVER_PORT, 5060);
    }

    /**
     * 设置国标网关port
     */
    public void setSipServerPort(int nationalPort) {
        prefs.edit().putInt(KEY_SIP_SERVER_PORT, nationalPort).commit();
    }

    /**
     * 设置SIP服务器Id
     *
     * @param sipServerId
     */
    public void SetSipServerId(String sipServerId) {
        prefs.edit().putString(KEY_SIP_SERVER_ID, sipServerId).commit();

    }

    /**
     * 获取SIP服务器Id
     *
     * @return
     */
    public String getSipServerId() {
        return prefs.getString(KEY_SIP_SERVER_ID, "34020000002000000001");
    }

    /**
     * 设置SIP服务器密码
     *
     * @param sipServerPassword
     */
    public void setSipServerPassword(String sipServerPassword) {
        prefs.edit().putString(KEY_SIP_SERVER_PASSWORD, sipServerPassword).commit();
    }

    /**
     * 获取SIP服务器密码
     *
     * @return
     */
    public String getSipServerPassword() {
        return prefs.getString(KEY_SIP_SERVER_PASSWORD, "12345678");
    }

    /**
     * 设置SIP服务器Realm
     *
     * @param sipServerRealm
     */
    public void setSipServerRealm(String sipServerRealm) {
        prefs.edit().putString(KEY_SIP_SERVER_REALM, sipServerRealm).commit();
    }

    /**
     * 获取SIP服务器Realm
     *
     * @return
     */
    public String getSipServerRealm() {
        return prefs.getString(KEY_SIP_SERVER_REALM, "34020000");
    }

    /**
     * 设置是否恢复出厂设置
     */
    public void setDeviceResrt(boolean flag) {
        prefs.edit().putBoolean(KEY_DEVICE_RESET, flag).commit();
    }

    /**
     * 获取是否恢复出厂设置
     */
    public Boolean getDeviceResrt() {
        return prefs.getBoolean(KEY_DEVICE_RESET, false);
    }

    /**
     * 设置广告语
     *
     * @param
     */
    public void setAD(String ad) {
        prefs.edit().putString(KEY_LOAD_LED, ad).commit();
    }

    /**
     * 获取广告语
     *
     * @return
     */
    public String getAD() {
        return prefs.getString(KEY_LOAD_LED, "欢迎光临");
    }

    /**
     * 设置控制器方向
     *
     * @param
     */
    public void setDirect(int direct) {
        prefs.edit().putInt(KEY_DEVICE_DIRECT, direct).commit();
    }

    /**
     * 获取控制器方向
     *
     * @return
     */
    public int getDirect() {
        return prefs.getInt(KEY_DEVICE_DIRECT, 0);
    }

    public String getSn() {
        return prefs.getString(DEVICE_SN, "");
    }

    public void setSn(String sn) {
        prefs.edit().putString(DEVICE_SN, sn).commit();
    }

    public boolean isOpenTempModule() {
        return prefs.getBoolean(KEY_OPEN_TEMP_MODULE, false);
    }

    public void setOpenTempModule(boolean open) {
        prefs.edit().putBoolean(KEY_OPEN_TEMP_MODULE, open).commit();
    }


    public boolean getRecognitionEnable() {
        return prefs.getBoolean(RECOGNITION_ENABLE, false);
    }

    public void setRecognitionEnable(boolean enable) {
        prefs.edit().putBoolean(RECOGNITION_ENABLE, enable).commit();
    }

}
