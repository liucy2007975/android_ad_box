package com.cow.liucy.hdxm.libcommon.utils;

import android.text.TextUtils;

import java.util.Date;

import timber.log.Timber;

/**
 * Created by anjubao on 2017/8/16.
 */

public class TokenUtils {


    /**
     * 是否获取了Token
     * @return
     */
    public static boolean hasToken(){
        if(TextUtils.isEmpty(AppPrefs.getInstance().getAESkey())){
            return false;
        }
        return true;
    }
    /**
     * 是否需要更新Token
     * @param startDate
     * @return
     */
    public static boolean needUpdateToken(String startDate){
        Date date=DateTimeUtils.parse(startDate);
        long mins=(date.getTime()-System.currentTimeMillis())/(60*1000);
        Timber.e("Token还有"+mins+"分钟过期");
        if(mins<30)//小于30分钟，
            return true;
        return false;
    }

    /**
     * 发起HTTP请求获取Token
     */
    public static void requestForToken(){
//        //主动获取Token
//        AuthRequst authRequst=new AuthRequst();
//        if (TextUtils.isEmpty(AppPrefs.getInstance().getMacCode())){
//            Timber.e("还未设置机器ID");
//            Constants.IS_REQUEST_START.set(false);
//            return;//还未设置机器ID
//        }
//        authRequst.setUsername(AppPrefs.getInstance().getMacCode());
//        authRequst.setPassword(AppPrefs.getInstance().getMacCode());//ymhAvm111
//        DataLoader.getInstance().auth(authRequst).subscribeWith(new DisposableObserver() {
//            @Override
//            public void onNext(@NonNull Object o) {
//                AppPrefs.getInstance().setAuthorizationResult(true);//授权通过！
//                AppPrefs.getInstance().updateToken(o.toString());
//                AppPrefs.getInstance().updateTokenExpiredTime(DateTimeUtils.getDateByAddDays(1));
//                Constants.IS_REQUEST_START.set(false);
//                EventBus.getDefault().post(new SendWebScketMessage());
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                Timber.e(e.getMessage());
//                Constants.IS_REQUEST_START.set(false);
//            }
//
//            @Override
//            public void onComplete() {
//                Timber.e(">>>>>>Complete");
//            }
//        });
    }
}
