package com.cow.liucy.libcommon.db.objectbox;


import com.cow.liucy.libcommon.utils.DateTimeUtils;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Created by zhengtao on 2018-01-08.
 * 车辆进出场表, 出场时，将数据插入到临时表
 * 等待同步，同时删除记录
 */

@Entity
public class CarEnterExitEntity {

    @Id
    private long id;

    /**
     * 所在车场编号
     */
    private String parkId;

    /**
     * 车牌号
     */
    @Index
    @JSONField(name = "carNo")
    private String carNo;

    /**
     * 车牌号码，去掉省份后剩余号码
     */
    @JSONField(name = "carNoSub")
    private String carNoPlateNum="";

    /**
     * 车牌号附加
     */
    private String carNoExt;

    /**
     * 出入口名称
     */
    @JSONField(name = "portName")
    private String portName;


    /**
     * 进场图片，路径为本地缓存的路径
     */
    @JSONField(name = "imageUrl")
    private String imageUrl;

    /**
     * 出入口终端ID
     */
    private String terminalId;

    @JSONField(name="createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime= DateTimeUtils.getFormatedData();

    /**
     * 车辆颜色
     */
    private String vehicleColor;
    /**
     * 车牌颜色
     */
    private String plateColor;

    /**
     * 车牌可信度
     */
    private String plateConfidence;

    /**
     * 车辆类型：大货车，小客车，大客车
     */
    private String carType;

    private int type=0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 所在车场编号
     */
    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    /**
     * 车牌号附加
     */
    public String getCarNoExt() {
        return carNoExt;
    }

    public void setCarNoExt(String carNoExt) {
        this.carNoExt = carNoExt;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCarNoPlateNum() {
        return carNoPlateNum;
    }

    public void setCarNoPlateNum(String carNoPlateNum) {
        this.carNoPlateNum = carNoPlateNum;
    }


    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }


    public String getPlateConfidence() {
        return plateConfidence;
    }

    public void setPlateConfidence(String plateConfidence) {
        this.plateConfidence = plateConfidence;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }


    public String getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(String plateColor) {
        this.plateColor = plateColor;
    }

    /**
     * 进出类型：
     * 0：入场
     * 1：出场
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
