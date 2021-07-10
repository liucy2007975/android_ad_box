package com.cow.liucy.huoyan.rxnetty;


import com.cow.liucy.huoyan.model.IvsResultResponse;

/**
 * Created by anjubao on 2018/6/8.
 */

public interface VzenithNettyEvent {
//    public void venithOnLine();
//    public void venithOffLine();
    public void venithOnReciveData(String deviceIp,IvsResultResponse ivsResultResponse,byte[] fullImage,byte[] clipImage);


}
