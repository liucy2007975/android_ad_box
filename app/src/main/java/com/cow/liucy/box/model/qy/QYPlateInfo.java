package com.cow.liucy.box.model.qy;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * {
 * 	"AlarmInfoPlate": {
 * 		"channel": 0,
 * 		"deviceName": "default",
 * 		"ipaddr": "192.168.1.10",
 * 		"result": {
 * 			"PlateResult": {
 * 				"bright": 0,
 * 				"carBright": 0,
 * 				"carColor": 0,
 * 				"colorType": 5,
 * 				"colorValue": 0,
 * 				"confidence": 95,
 * 				"direction": 1,
 * 				"imageFile": "",
 * 				"imageFragmentFile": "",
 * 				"license": "粤BF12345",
 * 				"location": {
 * 					"RECT": {
 * 						"top": 802,
 * 						"left": 648,
 * 						"right": 1004,
 * 						"bottom": 696
 *                                        }* 				},
 * 				"timeStamp": {
 * 					"Timeval": {
 * 						"sec": 1571932583,
 * 						"usec": 0
 *                                    }
 * 				},
 * 				"timeUsed": 0,
 * 				"triggerType": 2,
 * 			            pe": 11
 * 			}
 * 		},
 * 		"serialno": "e0fade    63f0fe93"
 * 	}
 * }
 *
 *
 */
public class QYPlateInfo{

	@JSONField(name="AlarmInfoPlate")
	private AlarmInfoPlate alarmInfoPlate;

	public void setAlarmInfoPlate(AlarmInfoPlate alarmInfoPlate){
		this.alarmInfoPlate = alarmInfoPlate;
	}

	public AlarmInfoPlate getAlarmInfoPlate(){
		return alarmInfoPlate;
	}
}