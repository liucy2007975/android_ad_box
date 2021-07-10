package com.cow.liucy.hdxm.libcommon.utils;

import com.alibaba.fastjson.serializer.PropertyFilter;

/**
 * Created by anjubao on 2019-03-27.
 * fastjson过滤器, 排除int型值为0的字段
 */

public class FastJsonFilter {
   public static PropertyFilter profilter = new PropertyFilter(){

        @Override
        public boolean apply(Object object, String name, Object value) {
            if(name.equalsIgnoreCase("DeviceEntryType") && (int)value==0){
                //false表示DeviceEntryType字段将被排除在外
                return false;
            }
            if(name.equalsIgnoreCase("CredenceType") && (int)value==0){
                //false表示CredenceType字段将被排除在外
                return false;
            }
            if(name.equalsIgnoreCase("RecordType") && (int)value==0){
                //false表示RecordType字段将被排除在外
                return false;
            }
            if(name.equalsIgnoreCase("UserType") && (int)value==0){
                //false表示UserType字段将被排除在外
                return false;
            }
            if(name.equalsIgnoreCase("EventType") && (int)value==0){
                //false表示EventType字段将被排除在外
                return false;
            }
            if(name.equalsIgnoreCase("EventCode") && (int)value==0){
                //false表示EventType字段将被排除在外
                return false;
            }
            if(name.equalsIgnoreCase("gateOpenMode") && (int)value==0){
                //false表示EventType字段将被排除在外
                return false;
            }
            return true;
        }

    };

}
