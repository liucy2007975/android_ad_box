package com.cow.liucy.hdxm.libcommon.eventbus;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by Wu on 2018/6/28
 * 设备状态上报事件
 */

public class DeviceEvent implements Serializable {
    /**
     * 设备类型
     */
    @JSONField(name = "DeviceType")
    private int deviceType;
    /**
     * 设备Id
     */
    @JSONField(name = "deviceID")
    private String deviceId;
    /**
     * 地锁编号
     */
    @JSONField(name = "lockDeviceID")
    private int lockDeviceId;
    /**
     * 设备状态
     */
    @JSONField(name = "DeviceStatus")
    private int deviceStatus;

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getLockDeviceId() {
        return lockDeviceId;
    }

    public void setLockDeviceId(int lockDeviceId) {
        this.lockDeviceId = lockDeviceId;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
