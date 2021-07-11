package com.cow.liucy.libcommon.api.http.model;

import java.io.Serializable;

/**
 *文件上传返回对象
 * Created by anjubao on 2018/6/25.
 */

public class FileUploadResp implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
