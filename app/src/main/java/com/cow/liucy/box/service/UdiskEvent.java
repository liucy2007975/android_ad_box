package com.cow.liucy.box.service;

import java.io.Serializable;

public class UdiskEvent  implements Serializable {
    private String path;

    public UdiskEvent(String path){
        this.path=path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
