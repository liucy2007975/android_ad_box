package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

public class PlateResult{

	@JSONField(name="timeUsed")
	private int timeUsed;

	@JSONField(name="imageFile")
	private String imageFile;

	@JSONField(name="imageFragmentFile")
	private String imageFragmentFile;

	@JSONField(name="confidence")
	private int confidence;

	@JSONField(name="bright")
	private int bright;

	@JSONField(name="carBright")
	private int carBright;

	@JSONField(name="type")
	private int type;

	@JSONField(name="colorType")
	private int colorType;

	@JSONField(name="timeStamp")
	private TimeStamp timeStamp;

	@JSONField(name="license")
	private String license;

	@JSONField(name="carColor")
	private int carColor;

	@JSONField(name="colorValue")
	private int colorValue;

	@JSONField(name="location")
	private Location location;

	@JSONField(name="triggerType")
	private int triggerType;

	@JSONField(name="direction")
	private int direction;

	public void setTimeUsed(int timeUsed){
		this.timeUsed = timeUsed;
	}

	public int getTimeUsed(){
		return timeUsed;
	}



	public void setConfidence(int confidence){
		this.confidence = confidence;
	}

	public int getConfidence(){
		return confidence;
	}

	public void setBright(int bright){
		this.bright = bright;
	}

	public int getBright(){
		return bright;
	}

	public void setCarBright(int carBright){
		this.carBright = carBright;
	}

	public int getCarBright(){
		return carBright;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public void setColorType(int colorType){
		this.colorType = colorType;
	}

	public int getColorType(){
		return colorType;
	}

	public void setTimeStamp(TimeStamp timeStamp){
		this.timeStamp = timeStamp;
	}

	public TimeStamp getTimeStamp(){
		return timeStamp;
	}

	public void setLicense(String license){
		this.license = license;
	}

	public String getLicense(){
		return license;
	}

	public void setCarColor(int carColor){
		this.carColor = carColor;
	}

	public int getCarColor(){
		return carColor;
	}

	public void setColorValue(int colorValue){
		this.colorValue = colorValue;
	}

	public int getColorValue(){
		return colorValue;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public Location getLocation(){
		return location;
	}

	public void setTriggerType(int triggerType){
		this.triggerType = triggerType;
	}

	public int getTriggerType(){
		return triggerType;
	}

	public void setDirection(int direction){
		this.direction = direction;
	}

	public int getDirection(){
		return direction;
	}

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	public String getImageFragmentFile() {
		return imageFragmentFile;
	}

	public void setImageFragmentFile(String imageFragmentFile) {
		this.imageFragmentFile = imageFragmentFile;
	}
}