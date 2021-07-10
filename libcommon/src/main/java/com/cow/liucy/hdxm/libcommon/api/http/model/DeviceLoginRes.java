package com.cow.liucy.hdxm.libcommon.api.http.model;

import java.io.Serializable;
import java.math.BigInteger;

public class DeviceLoginRes implements Serializable {
    	private BigInteger deviceId;
    	private BigInteger parkingLotId;
    	private long time;

    public BigInteger getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(BigInteger deviceId) {
        this.deviceId = deviceId;
    }

    public BigInteger getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(BigInteger parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
