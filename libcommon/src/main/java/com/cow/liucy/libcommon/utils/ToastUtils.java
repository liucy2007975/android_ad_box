package com.cow.liucy.libcommon.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by fanyufeng on 2017-9-25.
 */

public class ToastUtils {

    private static Context context = null;
    private static Toast toast = null;


    public static void getShortToast(Context context, int retId){
        if (toast == null) {
            toast = Toast.makeText(context, retId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    public static void getShortToastByString(Context context,String hint){
        if (toast == null) {
            toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    public static void getLongToast(Context context,int retId){
        if (toast == null) {
            toast = Toast.makeText(context, retId, Toast.LENGTH_LONG);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    public static void getLongToastByString(Context context,String hint){
        if (toast == null) {
            toast = Toast.makeText(context, hint, Toast.LENGTH_LONG);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}