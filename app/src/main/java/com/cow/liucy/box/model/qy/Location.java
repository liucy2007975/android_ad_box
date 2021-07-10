package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class Location{

	@JSONField(name="RECT")
	private RECT rECT;

	public void setRECT(RECT rECT){
		this.rECT = rECT;
	}

	public RECT getRECT(){
		return rECT;
	}
}