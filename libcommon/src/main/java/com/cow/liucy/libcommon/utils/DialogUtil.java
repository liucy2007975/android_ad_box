package com.cow.liucy.libcommon.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;


import com.cow.liucy.hdxm.libcommon.R;

import java.lang.reflect.Field;

/**
 * @author: dong
 * @Data: 2018/7/9 11:50
 * @Description:
 */
public class DialogUtil {

    /**
     * 只保留确定dialog
     *
     * @param context
     * @param title
     * @param msg
     * @param positiveButton
     * @param positiveListener
     */
    public static void showAlertDialog(Context context, String title, String msg,
                                       String positiveButton,
                                       DialogInterface.OnClickListener positiveListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, positiveListener)
                .setCancelable(true).create();
        alertDialog.show();

        //修改“确认”、“取消”按钮的字体大小
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            if (!TextUtils.isEmpty(title)) {
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(26);
            }
            //通过反射修改message字体大小和颜色
            if (!TextUtils.isEmpty(msg)) {
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextSize(26);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     * 只保留确定dialog
     *
     * @param context
     * @param title
     * @param msg
     * @param positiveButton
     * @param positiveListener
     */
    public static AlertDialog showAlertDialog(Context context, String title, String msg,
                                              String positiveButton,
                                              DialogInterface.OnClickListener positiveListener, boolean cancelable) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, positiveListener)
                .setCancelable(cancelable).create();
        alertDialog.show();

        //修改“确认”、“取消”按钮的字体大小
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            if (!TextUtils.isEmpty(title)) {
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(26);
            }
            //通过反射修改message字体大小和颜色
            if (!TextUtils.isEmpty(msg)) {
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextSize(26);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return alertDialog;
    }

    /**
     * 确定与取消dialog
     *
     * @param context
     * @param title
     * @param msg
     * @param positiveButton
     * @param positiveListener
     * @param negativeButton
     * @param negativeListener
     */
    public static void showAlertDialog(Context context, String title, String msg,
                                       String positiveButton,
                                       DialogInterface.OnClickListener positiveListener,
                                       String negativeButton,
                                       DialogInterface.OnClickListener negativeListener, boolean cancelable) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, positiveListener)
                .setNegativeButton(negativeButton, negativeListener)
                .setCancelable(cancelable).create();
        alertDialog.show();

        //修改“确认”、“取消”按钮的字体大小
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(26);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            if (!TextUtils.isEmpty(title)) {
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(26);
            }
            //通过反射修改message字体大小和颜色
            if (!TextUtils.isEmpty(msg)) {
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextSize(26);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void showAlertDialog(Context context, String title, int iconId, String msg,
                                       String positiveButton,
                                       DialogInterface.OnClickListener positiveListener,
                                       String negativeButton,
                                       DialogInterface.OnClickListener negativeListener, boolean cancelable) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setIcon(iconId)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, positiveListener)
                .setNegativeButton(negativeButton, negativeListener)
                .setCancelable(cancelable).create();
        alertDialog.show();

        //修改“确认”、“取消”按钮的字体大小
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(26);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            if (!TextUtils.isEmpty(title)) {
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(26);
            }
            //通过反射修改message字体大小和颜色
            if (!TextUtils.isEmpty(msg)) {
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextSize(26);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void showAlertDialog(Context context, String title, String msg,
                                       String positiveButton,
                                       DialogInterface.OnClickListener positiveListener,
                                       String negativeButton,
                                       DialogInterface.OnClickListener negativeListener) {
        showAlertDialog(context, title, msg, positiveButton, positiveListener, negativeButton, negativeListener, true);
    }

    public static ProgressDialog createProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.progressStyle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(msg);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context, String msg, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.progressStyle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(msg);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context, String msg, DialogInterface.OnClickListener onClickListener, int max) {

        ProgressDialog dialog = new ProgressDialog(context, R.style.progressStyle);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        //dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
        dialog.setProgressNumberFormat(null);
        dialog.setProgress(0);
        dialog.setTitle(R.string.tips);
        dialog.setMax(max);
        /*dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });*/
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.btn_can),
                onClickListener);
        /*dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "中立",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });*/
        dialog.setMessage(msg);
        //dialog.show();

        /*dialog.incrementProgressBy(1);
        // dialog.incrementSecondaryProgressBy(10)//二级进度条更新方式*/

        return dialog;
    }

}
