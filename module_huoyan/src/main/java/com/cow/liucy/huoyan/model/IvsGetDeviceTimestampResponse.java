package com.cow.liucy.huoyan.model;

public class IvsGetDeviceTimestampResponse {

//    "cmd": "get_device_timestamp"
    private String cmd="get_device_timestamp";
    private long timestamp;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
