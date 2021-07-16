package com.cow.liucy.box.ui;

import java.io.Serializable;

public class SysConfig implements Serializable {

    private String ip;
    private String submask;
    private String gateway;
    private Integer volume;
    private String tcpServerIP;
    private Integer tcpServerPort;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }



    public String getTcpServerIP() {
        return tcpServerIP;
    }

    public void setTcpServerIP(String tcpServerIP) {
        this.tcpServerIP = tcpServerIP;
    }



    public String getSubmask() {
        return submask;
    }

    public void setSubmask(String submask) {
        this.submask = submask;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getTcpServerPort() {
        return tcpServerPort;
    }

    public void setTcpServerPort(Integer tcpServerPort) {
        this.tcpServerPort = tcpServerPort;
    }
}
