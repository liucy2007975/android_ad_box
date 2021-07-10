package com.cow.liucy.box.service;

public class FileUploadEvent {

    private String filePath;
    public FileUploadEvent(String filePath){
        this.setFilePath(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
