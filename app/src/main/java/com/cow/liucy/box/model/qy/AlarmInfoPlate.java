package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class AlarmInfoPlate{

	@JSONField(name="result")
	private Result result;

	@JSONField(name="channel")
	private int channel;

	@JSONField(name="ipaddr")
	private String ipaddr;

	@JSONField(name="deviceName")
	private String deviceName;

	@JSONField(name="serialno")
	private String serialno;

	public void setResult(Result result){
		this.result = result;
	}

	public Result getResult(){
		return result;
	}

	public void setChannel(int channel){
		this.channel = channel;
	}

	public int getChannel(){
		return channel;
	}

	public void setIpaddr(String ipaddr){
		this.ipaddr = ipaddr;
	}

	public String getIpaddr(){
		return ipaddr;
	}

	public void setDeviceName(String deviceName){
		this.deviceName = deviceName;
	}

	public String getDeviceName(){
		return deviceName;
	}

	public void setSerialno(String serialno){
		this.serialno = serialno;
	}

	public String getSerialno(){
		return serialno;
	}
}