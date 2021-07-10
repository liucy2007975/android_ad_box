package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class Timeval{

	@JSONField(name="sec")
	private int sec;

	@JSONField(name="usec")
	private int usec;

	public void setSec(int sec){
		this.sec = sec;
	}

	public int getSec(){
		return sec;
	}

	public void setUsec(int usec){
		this.usec = usec;
	}

	public int getUsec(){
		return usec;
	}
}