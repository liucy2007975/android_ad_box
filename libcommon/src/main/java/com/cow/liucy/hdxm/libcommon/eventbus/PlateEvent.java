package com.cow.liucy.hdxm.libcommon.eventbus;

import java.io.Serializable;

public  class PlateEvent implements Serializable{

    private String deviceIp;

    private String number;


    /**
     * 车辆颜色
     */
    private String vehicleColor="0";
    /**
     * 车牌颜色
     */
    private String plateColor="0";
    private byte[] picdata;
    /**
     * 车牌识别结果打分
     */
    private float plateConfidence;

    /**
     * 进出类型
     * 0:入场
     * 1：出场
     */
    private int type=0;


    //车牌时间
    private long createTime= System.nanoTime();


    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public byte[] getPicdata() {
        return picdata;
    }

    public void setPicdata(byte[] picdata) {
        this.picdata = picdata;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPlateConfidence() {
        return plateConfidence;
    }

    public void setPlateConfidence(float plateConfidence) {
        this.plateConfidence = plateConfidence;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * 车身颜色
     */
    public String getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(String plateColor) {
        this.plateColor = plateColor;
    }
}