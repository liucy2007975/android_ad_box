package com.cow.liucy.box.model;

import java.io.Serializable;

public class ConfigHttpResult implements Serializable {

//    String   vehicleLaneKey= jsonObject.get("vehicleLaneKey").toString();
//    String   ipaddr=  jsonObject.get("ipaddr").toString();
//    String   license=jsonObject.get("license").toString();
//    String   colorType=jsonObject.get("colorType").toString();
//    String   triggerType=jsonObject.get("triggerType").toString();
//    String   confidence=jsonObject.get("confidence").toString();
//    String   scanTime=jsonObject.get("scanTime").toString();
//    String   imageFile=jsonObject.get("imageFile").toString();
//    String   imageFragmentFile=jsonObject.get("imageFragmentFile").toString();

    private String deviceIP="";//摄像枪IP
    private String license="";//车牌
    private String vehicleColorType="";//车辆颜色
    private String plateColorType="";//车牌颜色
    private String confidence="";//车牌识别结果分值
    private String imageFile="";//图片
    private String imageFragmentFile="";//车牌特写图片
}
