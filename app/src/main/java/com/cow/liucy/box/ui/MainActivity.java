package com.cow.liucy.box.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.cow.liucy.face.R;
import com.cow.liucy.libcommon.base.BaseActivity;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.usbmonitor.Constant;

public class MainActivity extends BaseActivity {

    //==============声明变量=====================>>
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
//===============注册/反注册====================>>
    private static final int READ_EXTERNAL_STORAGE_CODE = 0;

    private static final int KEYCODE_UP = 19;
    private static final int KEYCODE_DOWN = 20;
    public static final int KEYCODE_LAST = 21;
    public static final int KEYCODE_NEXT = 22;
    public static final int KEYCODE_PAUSE = 23;
    private ProgressBar mProgressBar;
    private SeekBar mSeekBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏顶部，全屏显示
        getSupportActionBar().hide();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_USB_RECEIVER);
        localReceiver = new LocalReceiver();
        //注册本地接收器
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void checkAppPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_CODE);
        } else {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted

            } else {
                // Permission Denied
                System.exit(0);
            }
        }
        //退出应用

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_LAST:   // 左键
//                mControl.slowPlay();
                break;
            case KEYCODE_NEXT:   // 右键
//                mControl.fastPlay();
                break;
            case KEYCODE_UP:   // 上键
//                mControl.nextVideo();
                break;
            case KEYCODE_DOWN:   // 下
//                mControl.lastVideo();
                break;
            case KEYCODE_PAUSE:  // ok键
//                mControl.pauseVideo();
                break;
        }
//        showSeekBar(); // 有按键操作显示进度条
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            Message msg = Message.obtain();
            AppLogger.d( "收到本地广播==>>" + data);
            if (data.equals("USB_MOUNT")) {
                //msg.what = MyHandler.USB_MOUNT;
                msg.obj = intent.getStringExtra("path");
                AppLogger.d(  "收到本地广播=path=>>" +  msg.obj );
            }
            //mHandler.sendMessage(msg);
        }
    }
}
