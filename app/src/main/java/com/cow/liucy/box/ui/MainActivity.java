package com.cow.liucy.box.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.os.Environment;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileUtils;
import com.cow.liucy.box.service.UdiskEvent;
import com.cow.liucy.face.R;
import com.cow.liucy.libcommon.base.BaseActivity;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.rxnetty.CommandVo;
import com.cow.liucy.libcommon.usbmonitor.Constant;
import com.cow.liucy.libcommon.utils.Constants;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

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

    private PlayerView videoPlayerView =null;
    private SimpleExoPlayer videoPlayer=null;
    private SimpleExoPlayer audioPlayer=null;
    AudioManager audiomanager;//音频管理器



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏顶部，全屏显示
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //获取音频管理器服务
        audiomanager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);

        videoPlayerView = findViewById(R.id.player_view);

        videoPlayer = new SimpleExoPlayer.Builder(this)
                .build();
        audioPlayer = new SimpleExoPlayer.Builder(this)
                .build();;

        videoPlayerView.setPlayer(videoPlayer);

        File audioPath=new File(Constants.AUDIO_PATH);
        for (File file : audioPath.listFiles()){
            Uri uri = null;
            if(file.exists()) {
                uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);
                audioPlayer.addMediaItem(item);
            }
        }

        File videoPath=new File(Constants.VIDEO_PATH);
        for (File file : videoPath.listFiles()){
            Uri uri = null;
            if(file.exists()) {
                uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);
                videoPlayer.addMediaItem(item);
            }
        }



        videoPlayer.setVolume(0f);//静音
        //  准备播放
        videoPlayer.prepare();
        // 开始播放
        videoPlayer.play();


        AppLogger.e(">>>>最大音量："+audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));//最大值： 100

        audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 17, AudioManager.FLAG_SHOW_UI);

        //  准备播放
        audioPlayer.prepare();
        // 开始播放
        audioPlayer.play();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLogger.e(">>>>>>onStart");
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLogger.e(">>>>>>onResume");
        videoPlayerView.getPlayer().prepare();
        videoPlayerView.getPlayer().play();
        videoPlayerView.onResume();
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
    protected void onPause() {
        super.onPause();
        AppLogger.e(">>>>>>onPause");
        videoPlayerView.getPlayer().pause();
        videoPlayerView.onPause();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    /**
     * 重写finish()方法
     */
    @Override
    public void finish() {
        //super.finish(); //记住不要执行此句
        AppLogger.e(">>>>>>finish");
        moveTaskToBack(true); //设置该activity永不过期，即不执行onDestroy()
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLogger.e(">>>>>>onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLogger.e(">>>>>>onDestroy");
        videoPlayerView.getPlayer().release();
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

        FileUtils.copyDir(udiskEvent.getPath()+"/ad_box", Environment.getExternalStorageDirectory().getPath() + "/ad_box");
        //读取系统配置文件进行配置；
        //音频文件、视频文件拷贝至相应目录

    }

    /**
     * 服务器播放控制指令
     * @param commandVo
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCommandEvent(CommandVo commandVo) {
        AppLogger.e(">>>>>onCommandEvent:"+ JSON.toJSONString(commandVo));

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

}
