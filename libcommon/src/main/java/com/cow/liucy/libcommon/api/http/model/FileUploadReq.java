package com.cow.liucy.libcommon.api.http.model;

import java.io.Serializable;

/**
 * 文件上传对象
 * Created by anjubao on 2018/6/25.
 */

public class FileUploadReq implements Serializable{

    private String pic;
    private int type;
    private String fileName;
    private String dataTime;
    private String filePath;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
