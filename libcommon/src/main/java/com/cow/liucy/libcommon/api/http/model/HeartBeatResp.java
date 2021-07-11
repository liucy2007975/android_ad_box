package com.cow.liucy.libcommon.api.http.model;

import java.io.Serializable;

public class HeartBeatResp implements Serializable {
	private long type;
    private String downloadUrl;
    private long time;



    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }
}
