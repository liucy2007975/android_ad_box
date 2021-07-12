package com.cow.liucy.box.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.view.KeyEvent;
import android.view.WindowManager;

import com.blankj.utilcode.util.FileUtils;
import com.cow.liucy.box.service.UdiskEvent;
import com.cow.liucy.face.R;
import com.cow.liucy.libcommon.base.BaseActivity;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.usbmonitor.Constant;
import com.cow.liucy.libcommon.usbmonitor.Util;
import com.cow.liucy.libcommon.utils.Constants;
import com.cow.liucy.libcommon.utils.Utils;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;


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

    private PlayerView playerView=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏顶部，全屏显示
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);


        File videoPath=new File(Constants.VIDEO_PATH);
        for (File file : videoPath.listFiles()){
            Uri uri = null;
            if(file.exists()) {
                uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);
                player.addMediaItem(item);
            }
        }
        //  准备播放
        player.prepare();
        // 开始播放
        player.play();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }

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
        AppLogger.e(">>>>>>keyCode:"+keyCode +">>>KeyEvent:"+event.getCharacters());
        switch (keyCode) {
            case KEYCODE_LAST:   // 左键
                AppLogger.e(">>>>>:左键");
//                mControl.slowPlay();
                break;
            case KEYCODE_NEXT:   // 右键
                AppLogger.e(">>>>>:右键");
//                mControl.fastPlay();
                break;
            case KEYCODE_UP:   // 上键
                AppLogger.e(">>>>>:上键");
//                mControl.nextVideo();
                break;
            case KEYCODE_DOWN:   // 下
                AppLogger.e(">>>>>:下");
//                mControl.lastVideo();
                break;
            case KEYCODE_PAUSE:  // ok键
                AppLogger.e(">>>>>:ok键");
//                mControl.pauseVideo();
                break;
            case 126:
                AppLogger.e(">>>>>暂停");
                break;
            case 86:
                AppLogger.e(">>>>>停止");
                break;
            case 90:
                AppLogger.e(">>>>>快进");
                break;
            case 89:
                AppLogger.e(">>>>>快退");
                break;
            case 24:
                AppLogger.e(">>>>>音量+");

                break;
            case 25:
                AppLogger.e(">>>>>音量-");
                break;
            case 87:
                AppLogger.e(">>>>>下一曲");

                break;
            case 88:
                AppLogger.e(">>>>>上一曲");

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
        EventBus.getDefault().unregister(this);
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            AppLogger.d( "收到本地广播==>>" + data);
            if (data.equals("USB_MOUNT")) {
                String path = intent.getStringExtra("path");
                AppLogger.d(  "收到本地广播=path=>>" +  path);
                EventBus.getDefault().post(new UdiskEvent(path));
            }
            //mHandler.sendMessage(msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUdiskEvent(UdiskEvent udiskEvent) {
        AppLogger.e(">>>>>onUdiskEvent:"+udiskEvent.getPath());

        FileUtils.copyDir(udiskEvent.getPath()+"/sprogram",Constants.APP_DEF_PATH+"video");

    }

}
