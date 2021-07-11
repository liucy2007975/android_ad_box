package com.cow.liucy.box.service.dns;

import com.cow.liucy.libcommon.db.objectbox.CameraInfoEntity;

import java.io.Serializable;
import java.util.List;

public class DeviceInfo implements Serializable {

    private String deviceSn;
    private String time;
    private List<CameraInfoEntity> cameraInfoEntityList;
    private String url;
    private String version;
    private String parkingId;
    private String deviceId;

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<CameraInfoEntity> getCameraInfoEntityList() {
        return cameraInfoEntityList;
    }

    public void setCameraInfoEntityList(List<CameraInfoEntity> cameraInfoEntityList) {
        this.cameraInfoEntityList = cameraInfoEntityList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
