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
import com.cow.liucy.libcommon.utils.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.ui.PlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.google.android.exoplayer2.Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED;


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
    private CommandVo commandVo=null;//当前播放指令
    private Map<String,Integer> videoIndexNameMap=new HashMap<String,Integer>();



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
                .build();
        AppLogger.e(">>>>最大音量："+audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));//最大值： 100

        audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_SHOW_UI);

        videoPlayerView.setPlayer(videoPlayer);
        initVideo();
        initAudio();
    }

    public void initVideo(){

        videoPlayer.clearMediaItems();
        File videoPath=new File(Constants.VIDEO_PATH);
        for (File file : videoPath.listFiles()){
            if (file.getName().endsWith(".mp4")){
                Uri uri = null;
                if(file.exists()) {
                    uri = Uri.fromFile(file);
                    MediaItem item = MediaItem.fromUri(uri);
                    videoPlayer.addMediaItem(item);
                }
            }
        }
        videoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

        //  准备播放
        videoPlayer.prepare();
        videoPlayer.setPlayWhenReady(true);
    }

    public void initAudio(){
        audioPlayer.clearMediaItems();
        File audioPath=new File(Constants.AUDIO_PATH);
        for (File file : audioPath.listFiles()){
            if (file.getName().endsWith(".mp3")){
                Uri uri = null;
                if(file.exists()) {
                    uri = Uri.fromFile(file);
                    MediaItem item = MediaItem.fromUri(uri);
                    audioPlayer.addMediaItem(item);
                }
            }
        }
//        audioPlayer.setVolume(1f);
        audioPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        //  准备播放
        audioPlayer.prepare();
        // 开始播放
        audioPlayer.setPlayWhenReady(true);
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
        if (videoPlayer!=null) {
            videoPlayer.release();
            videoPlayer=null;
        }
        if (audioPlayer!=null) {
            audioPlayer.release();
            audioPlayer=null;
        }
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
        try{
            FileUtils.moveDir(udiskEvent.getPath()+"/ad_box", Environment.getExternalStorageDirectory().getPath() + "/ad_box");
            //读取系统配置文件进行配置；
            //音频文件、视频文件拷贝至相应目录
            Flowable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(l->{
                initVideo();
                initAudio();
            },e->{
                e.printStackTrace();
            });
        }catch (Exception ee){
            ee.printStackTrace();
        }

    }

    /**
     * 服务器播放控制指令
     * @param commandVo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(CommandVo commandVo) {
        AppLogger.e(">>>>>onCommandEvent:"+ JSON.toJSONString(commandVo));
        this.commandVo=commandVo;

        if (!commandVo.getVideoNo().equals("000")){
            //播放指定视频逻辑
            File file=new File(Constants.VIDEO_PATH+commandVo.getVideoNo()+".mp4");
            if (file.exists()){

                videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                int videoNo=Integer.parseInt(commandVo.getVideoNo());

                if (videoNo<=100) {
                    videoPlayer.setVolume(0f);//视频静音，100序号以下，视频静音
                }else{
                    videoPlayer.setVolume(1f);//视频取消静音
                }
                AppLogger.e(">>>>>开始播放视频:"+ file.getName());

                Uri uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);
//                // 暂停播放
                pauseVideoPlayer();
                //清除MediaItems
                videoPlayer.clearMediaItems();
                //播放指定音频逻辑
                if (commandVo.getVideoMod().equals("03")){
                    //按循环次数播放 指定音频
                    int count= Integer.parseInt(commandVo.getVideoCirc());//循环次数
                    videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    LoopingMediaSource loopingMediaSource = new LoopingMediaSource(new DefaultMediaSourceFactory(Utils.getContext()).createMediaSource(item),count);
                    videoPlayer.setMediaSource(loopingMediaSource,true);

                }else if (commandVo.getVideoMod().equals("01")){
                    videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    //只播放一次 指定音频
                    videoPlayer.setMediaItem(item,true);

                }else if (commandVo.getVideoMod().equals("02")){
                    //循环播放 指定音频
                    //只播放一次 指定音频
                    videoPlayer.setMediaItem(item,true);
                    videoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
                startVideoPlayer();
            }else{
                AppLogger.e(">>>>需要播放的视频文件不存在！");
            }

        }else{//停止视频
            if (videoPlayer!=null){
                pauseVideoPlayer();
            }
        }

        if(!commandVo.getAudioNo().equals("000")){//需要播放音频

            audioPlayer.setVolume(1f);//音频开放

            File file=new File(Constants.AUDIO_PATH+commandVo.getAudioNo()+".mp3");
            if (file.exists()){

                videoPlayer.setVolume(0f);//视频静音

                Uri uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);

                AppLogger.e(">>>>>开始播放音频:"+ file.getName());

                //清除MediaItems
                audioPlayer.clearMediaItems();

                //播放指定音频逻辑
                if (commandVo.getAudioMod().equals("03")){
                    //按循环次数播放 指定音频
                    int count= Integer.parseInt(commandVo.getAudioCirc());//循环次数
                    audioPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    LoopingMediaSource loopingMediaSource = new LoopingMediaSource(new DefaultMediaSourceFactory(Utils.getContext()).createMediaSource(item),count);
                    audioPlayer.setMediaSource(loopingMediaSource,true);

                }else if (commandVo.getAudioMod().equals("01")){
                    audioPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    //只播放一次 指定音频
                    audioPlayer.setMediaItem(item,true);

                }else if (commandVo.getAudioMod().equals("02")){
                    //循环播放 指定音频
                    //只播放一次 指定音频
                    audioPlayer.setMediaItem(item,true);
                    audioPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
                //  准备播放
                audioPlayer.prepare();
                // 开始播放
                audioPlayer.play();

            }else{
                AppLogger.e(">>>>需要播放的音频文件不存在！");
            }

        }else{//停止音频

            if (audioPlayer!=null){
                audioPlayer.setPlayWhenReady(false);
            }
        }
    }

    private void pauseVideoPlayer(){
        videoPlayer.setPlayWhenReady(false);
        videoPlayer.getPlaybackState();
    }

    private void startVideoPlayer(){
        videoPlayer.prepare();
        videoPlayer.setPlayWhenReady(true);
        videoPlayer.getPlaybackState();
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
