package com.cow.liucy.libcommon.api.http.model;

import java.io.Serializable;

public class EntryReq implements Serializable {
    private String carType;

    private String dataProviderId;
    private String[] entryPicBase64s;
    private String entryTime;
    private String entryWay = "车牌识别";
    private String lpn;

    private String parkingLotBoxId;
    private String parkingLotId;
    private String plateColor;
    private String plateConfidence;
    private String vehicleColor;

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(String dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public String[] getEntryPicBase64s() {
        return entryPicBase64s;
    }

    public void setEntryPicBase64s(String[] entryPicBase64s) {
        this.entryPicBase64s = entryPicBase64s;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getEntryWay() {
        return entryWay;
    }

    public void setEntryWay(String entryWay) {
        this.entryWay = entryWay;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getParkingLotBoxId() {
        return parkingLotBoxId;
    }

    public void setParkingLotBoxId(String parkingLotBoxId) {
        this.parkingLotBoxId = parkingLotBoxId;
    }

    public String getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(String parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public String getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(String plateColor) {
        this.plateColor = plateColor;
    }

    public String getPlateConfidence() {
        return plateConfidence;
    }

    public void setPlateConfidence(String plateConfidence) {
        this.plateConfidence = plateConfidence;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }
}
