package com.cow.liucy.box.service.dns;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by anjubao on 2019-04-01.
 * mDNS配置信息对象
 */

public class CowDSNDto implements Serializable {

    @JSONField(name = "a")
    private String macAddress;
    @JSONField(name = "b")
    private String localIp;
    @JSONField(name="c")
    private String version;
    @JSONField(name = "d")
    private String serverIp;
    @JSONField(name = "e")
    private String localGateway;
    @JSONField(name="f")//恢复出厂设置:1-恢复 0-不恢复
    private int isRest;
    @JSONField(name="g")//重启：1-重启 0-不重启
    private int isRestart;
    @JSONField(name = "k")
    private String localNetMask;//子网掩码
    @JSONField(name = "l")
    private String elevatorServerIp;//梯控服务器
    @JSONField(name = "m")
    private String buildNo;//楼栋号
    @JSONField(name = "n")
    private String cellNo;//单元号
    @JSONField(name = "o")
    private String deviceSn;//设备名


    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }
    public String getElevatorServerIp() {
        return elevatorServerIp;
    }

    public void setElevatorServerIp(String elevatorServerIp) {
        this.elevatorServerIp = elevatorServerIp;
    }

    public String getBuildNo() {
        return buildNo;
    }

    public void setBuildNo(String buildNo) {
        this.buildNo = buildNo;
    }

    public String getCellNo() {
        return cellNo;
    }

    public void setCellNo(String cellNo) {
        this.cellNo = cellNo;
    }

    public String getLocalNetMask() {
        return localNetMask;
    }

    public void setLocalNetMask(String localNetMask) {
        this.localNetMask = localNetMask;
    }

    public String getLocalGateway() {
        return localGateway;
    }

    public void setLocalGateway(String localGateway) {
        this.localGateway = localGateway;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getIsRest() {
        return isRest;
    }

    public void setIsRest(int isRest) {
        this.isRest = isRest;
    }

    public int getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(int isRestart) {
        this.isRestart = isRestart;
    }
}
