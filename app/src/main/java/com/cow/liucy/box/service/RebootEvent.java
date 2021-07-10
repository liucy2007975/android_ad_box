package com.cow.liucy.box.service;

public class RebootEvent {
    private boolean reboot;
    public RebootEvent(boolean reboot){
        this.setReboot(reboot);
    }

    public boolean isReboot() {
        return reboot;
    }

    public void setReboot(boolean reboot) {
        this.reboot = reboot;
    }
}
