package com.cow.liucy.libcommon.usbmonitor;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cow.liucy.libcommon.logger.AppLogger;

/**
 * 用于监听android 5.1 的u盘插拔
 */
public class USBdiskReceiver51 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        String path = intent.getData().getPath();
        AppLogger.e( action);
        if (action.equals(VolumeInfo.ACTION_USB_DEVICE_ATTACHED)) {
            // AppLogger.e("action===", "装载");
        } else if (action.equals(VolumeInfo.ACTION_USB_DEVICE_DETACHED)) {
            //AppLogger.d("action===", "卸载USB");
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
            //AppLogger.d(MainActivity.TAG, "ACTION_MEDIA_UNMOUNTED");
            // processUnmountedMessage(context, bundle);
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            //AppLogger.d(MainActivity.TAG, "ACTION_MEDIA_MOUNTED");
            processMountedMessage(context, path);
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            //AppLogger.d(MainActivity.TAG, "ACTION_MEDIA_REMOVED");
            // processUnmountedMessage(context, bundle);
        }
    }

    private void processUnmountedMessage(Context context, Bundle bundle) {

        Intent mIntent = new Intent(Constant.ACTION_USB_RECEIVER);
        mIntent.putExtra("message", "false");
        context.sendBroadcast(mIntent);

    }

    private void processMountedMessage(Context context, String path) {
        AppLogger.e( "path ==>>" + path);
        if (Util.isPathExist(path)) {
            AppLogger.e( "into processMountedMessage isPathExist true");
            Intent intent = new Intent(Constant.ACTION_USB_RECEIVER);
            intent.putExtra("data", "USB_MOUNT");
            intent.putExtra("path", path);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            //发送本地广播
            localBroadcastManager.sendBroadcast(intent);
        }
    }
}
