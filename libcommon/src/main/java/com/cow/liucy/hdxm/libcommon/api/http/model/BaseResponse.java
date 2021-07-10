package com.cow.liucy.hdxm.libcommon.api.http.model;

import java.io.Serializable;

/**
 * 网络请求结果 基类
 */

public class BaseResponse<T> implements Serializable{
    public int code;

    public String message;

    public String msg="";

    public int total;

    public T data;

    public String datetime="";
}
