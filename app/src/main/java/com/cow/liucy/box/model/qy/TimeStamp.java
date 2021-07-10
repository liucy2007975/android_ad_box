package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class TimeStamp{

	@JSONField(name="Timeval")
	private Timeval timeval;

	public void setTimeval(Timeval timeval){
		this.timeval = timeval;
	}

	public Timeval getTimeval(){
		return timeval;
	}
}