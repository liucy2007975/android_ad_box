package com.cow.liucy.box.ui;

import java.io.Serializable;

public class SysConfig implements Serializable {

    private String ip;
    private String submask;
    private String gateway;
    private String volume;
    private String tcpServerIP;
    private String tcpServerPort;

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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTcpServerIP() {
        return tcpServerIP;
    }

    public void setTcpServerIP(String tcpServerIP) {
        this.tcpServerIP = tcpServerIP;
    }

    public String getTcpServerPort() {
        return tcpServerPort;
    }

    public void setTcpServerPort(String tcpServerPort) {
        this.tcpServerPort = tcpServerPort;
    }

    public String getSubmask() {
        return submask;
    }

    public void setSubmask(String submask) {
        this.submask = submask;
    }
}
