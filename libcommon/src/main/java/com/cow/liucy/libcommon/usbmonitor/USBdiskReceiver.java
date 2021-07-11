package com.cow.liucy.libcommon.usbmonitor;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class USBdiskReceiver extends BroadcastReceiver {
    private static final String TAG = "USBdiskReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.e("action===", action);
        if (action.equals(VolumeInfo.ACTION_USB_DEVICE_ATTACHED)) {
            // Log.e("action===", "装载");
        } else if (action.equals(VolumeInfo.ACTION_USB_DEVICE_DETACHED)) {
            //Log.d("action===", "卸载USB");
        } else if (action.equals(VolumeInfo.ACTION_VOLUME_STATE_CHANGED)) {
            int intExtra = intent.getIntExtra(VolumeInfo.EXTRA_VOLUME_STATE, VolumeInfo.STATE_UNMOUNTED);
            Log.e("action===", "ACTION_VOLUME_STATE_CHANGED=intExtra=>>" + intExtra);
            if (intExtra == VolumeInfo.STATE_MOUNTED) {
                processMountedMessage(context);
            }
        }
    }

    private void processUnmountedMessage(Context context, Bundle bundle) {

        Intent mIntent = new Intent(Constant.ACTION_USB_RECEIVER);
        mIntent.putExtra("message", "false");
        context.sendBroadcast(mIntent);

    }

    private void processMountedMessage(Context context) {
        String path = Util.getStoragePath(context);
        if (Util.isPathExist(path)) {
            Log.e(TAG, "into processMountedMessage isPathExist true");
            Intent intent = new Intent(Constant.ACTION_USB_RECEIVER);
            intent.putExtra("data", "USB_MOUNT");
            intent.putExtra("path", path);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            //发送本地广播
            localBroadcastManager.sendBroadcast(intent);
        }
    }

}
