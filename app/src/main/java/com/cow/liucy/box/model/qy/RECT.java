package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class RECT{

	@JSONField(name="top")
	private int top;

	@JSONField(name="left")
	private int left;

	@JSONField(name="bottom")
	private int bottom;

	@JSONField(name="right")
	private int right;

	public void setTop(int top){
		this.top = top;
	}

	public int getTop(){
		return top;
	}

	public void setLeft(int left){
		this.left = left;
	}

	public int getLeft(){
		return left;
	}

	public void setBottom(int bottom){
		this.bottom = bottom;
	}

	public int getBottom(){
		return bottom;
	}

	public void setRight(int right){
		this.right = right;
	}

	public int getRight(){
		return right;
	}
}