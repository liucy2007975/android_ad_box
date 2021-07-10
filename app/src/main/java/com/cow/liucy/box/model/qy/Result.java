package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class Result{

	@JSONField(name="PlateResult")
	private PlateResult plateResult;

	public void setPlateResult(PlateResult plateResult){
		this.plateResult = plateResult;
	}

	public PlateResult getPlateResult(){
		return plateResult;
	}
}