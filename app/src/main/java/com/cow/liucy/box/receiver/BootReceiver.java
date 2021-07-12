package com.cow.liucy.box.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cow.liucy.box.ui.MainActivity;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.utils.Utils;


/**
 */
public class BootReceiver extends BroadcastReceiver {
    static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        AppLogger.e(" BootReceiver 接收到开机启动的广播 ------------------>>>.");
        if (intent.getAction().equals(BOOT_COMPLETED)) {
//            Intent fileIntent = new Intent(Utils.getContext(), MainActivity.class);
//            fileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Utils.getContext().startActivity(fileIntent);
            AppLogger.e(" BootReceiver  打开MainActivity--------------->>>.");
        }
    }
}
