package com.cow.liucy.hdxm.libcommon.db.objectbox;



import com.cow.liucy.hdxm.libcommon.utils.DateTimeUtils;
import com.alibaba.fastjson.JSON;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by anjubao on 2018/1/4.
 * 出入口相机配置实体类
 */

@Entity
public class CameraInfoEntity {

    @Id
    private long id;

    /**
     * 出入口名称
     */
    private String portName="";

    private String cameraIp="";

    /**
     * 类型 0-入口 1-出口
     */
    private int portDirect = 0;

    /**
     * 车场编号
     */
    private String parkCode="";

    /**
     * 进出口编号
     */
    private String portId="";

    /**
     * 盒子设备编号
     */
    private String terminalId ="";

    /**
     * 附加字段
     */
    private String comment="";

    private Date createData= DateTimeUtils.getFormatedData();

    /**
     * 相册厂家类型1：华夏，2:火眼
     */
    private int deviceType=1;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPortDirect() {
        return portDirect;
    }

    public void setPortDirect(int portDirect) {
        this.portDirect = portDirect;
    }

    /**
     * 出入口名称
     */
    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    /**
     * 相机IP
     */
    public String getCameraIp() {
        return cameraIp;
    }

    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp;
    }



    /**
     * 记录创建时间
     */
    public Date getCreateData() {
        return createData;
    }

    public void setCreateData(Date createData) {
        this.createData = createData;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    @Override
    public String toString() {
        return  JSON.toJSONString(this);
    }

    /**
     * 终端编号
     */
    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }



    /**
     * 出入口说明
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }


}
