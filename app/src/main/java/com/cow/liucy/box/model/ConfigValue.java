package com.cow.liucy.box.model;

import java.util.HashMap;

/**
 * HTTP车牌识别结果上报配置参数
 */
public class ConfigValue {
    public static String DEVICE_IP="ipaddr";//摄像枪IP
    public static String LICENSE="license";//车牌
    public static String VEHICLE_COLOR_TYPE="color";//车辆颜色
    public static String PLATE_COLOR_TYPE="colorType";//车牌颜色
    public static String PLATE_TYPE="type";//车牌类型
    public static String CONFIDENCE="confidence";//车牌识别结果分值
    public static String SCAN_TIME="scanTime";//触发时间
    public static String IMAGE_FILE="imageFile";//图片
    public static String IMAGE_FRAGMENT_FILE="imageFragmentFile";//车牌特写图片

    public static String POST_URL="/ParkAPI/sendScanCar";
    /**
     * 车牌颜色
     */
    public static HashMap<Integer,String> plantColorMap=new HashMap<Integer,String>(){
        {
            put(0,"未知");
            put(1,"蓝色");
            put(2,"黄色");
            put(3,"白色");
            put(4,"黑色");
            put(5,"绿色");
        }
    };
    /**
     * 车牌类型
     */
    public static HashMap<Integer,String> plantTypeMap=new HashMap<Integer,String>(){
        {
            put(0,"未知车牌");
            put(1,"蓝牌小汽车");
            put(2,"黑牌小汽车");
            put(3,"单排黄牌");
            put(4,"双排黄牌");
            put(5,"警车车牌");
            put(6,"武警车牌");
            put(7,"个性化车牌");
            put(8,"单排军车牌");
            put(9,"双排军车牌");
            put(10,"使馆车牌");
            put(11,"香港进出中国大陆车牌");
            put(12,"农用车牌");
            put(13,"教练车牌");
            put(14,"澳门进出中国大陆车牌");
            put(15,"双层武警车牌");
            put(16,"武警总队车牌");
            put(17,"双层武警总队车牌");

        }
    };
    public static String RESPOSE_STR="{\"info\": \"接收成功\", \"resultCode\": 100, \"type\":0,\"data\":\"\"}";


}
