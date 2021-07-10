package com.cow.liucy.huoyan.model;


/**
 * Created by anjubao on 2017/12/25.
 * 车牌识别 配置推送数据方式
 */

public class IvsResultRequest {

    /**
     * cmd : ivsresult
     * enable : true
     * format : json
     * image : true
     * image_type : 0
     */

    private String cmd="ivsresult";
    private boolean enable=true;
    private String format="json";
    private boolean image=true;
    private int image_type=0;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public int getImage_type() {
        return image_type;
    }

    public void setImage_type(int image_type) {
        this.image_type = image_type;
    }


}
