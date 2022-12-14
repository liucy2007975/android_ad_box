package com.cow.liucy.box.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ShellUtils;
import com.bumptech.glide.Glide;
import com.cow.liucy.box.service.RebootEvent;
import com.cow.liucy.box.service.UdiskEvent;
import com.cow.liucy.face.R;
import com.cow.liucy.libcommon.base.BaseActivity;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.rxnetty.CommandVo;
import com.cow.liucy.libcommon.usbmonitor.Constant;
import com.cow.liucy.libcommon.utils.AppPrefs;
import com.cow.liucy.libcommon.utils.Constants;
import com.cow.liucy.libcommon.utils.FileUtils;
import com.cow.liucy.libcommon.utils.NetUtil;
import com.cow.liucy.libcommon.utils.ToastUtils;
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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.cow.liucy.libcommon.utils.Constants.PICTURE_PATH;
import static com.google.android.exoplayer2.Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED;


public class MainActivity extends BaseActivity {

    //==============????????????=====================>>
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;

//===============??????/?????????====================>>
    private static final int READ_EXTERNAL_STORAGE_CODE = 0;

    private static final int KEYCODE_UP = 19;
    private static final int KEYCODE_DOWN = 20;
    public static final int KEYCODE_LAST = 21;
    public static final int KEYCODE_NEXT = 22;
    public static final int KEYCODE_PAUSE = 23;

    private PlayerView videoPlayerView =null;
    private SimpleExoPlayer videoPlayer=null;
    private SimpleExoPlayer audioPlayer=null;
    AudioManager audiomanager;//???????????????
    private CommandVo commandVo=null;//??????????????????
    private Map<String,Integer> videoIndexNameMap=new HashMap<String,Integer>();
    private ImageView ad_pic;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //???????????????????????????
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //???????????????????????????
        audiomanager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);

        videoPlayerView = findViewById(R.id.player_view);
        ad_pic=findViewById(R.id.ad_pic);

        videoPlayer = new SimpleExoPlayer.Builder(this)
                .build();
        audioPlayer = new SimpleExoPlayer.Builder(this)
                .build();
        AppLogger.e(">>>>???????????????"+audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));//???????????? 100

//        audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_SHOW_UI);

        videoPlayerView.setPlayer(videoPlayer);
        initVideo();
        initAudio();

        //????????????
        File file = new File(PICTURE_PATH+"logo.jpg");
        //????????????
        Glide.with(this).load(file).into(ad_pic);
        ad_pic.setVisibility(View.VISIBLE);
        videoPlayerView.setVisibility(View.GONE);


        /**
         * 3s???????????????????????????????????????????????????????????????????????????????????????
         */
        Flowable.interval(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l-> {

                    if (videoPlayer.isPlaying()){
//                        AppLogger.e(">>>>>>isPlaying>>>");
                        ad_pic.setVisibility(View.GONE);
                        videoPlayerView.setVisibility(View.VISIBLE);
                    }else{
//                        AppLogger.e(">>>>>>not Playing>>>");

                        ad_pic.setVisibility(View.VISIBLE);
                        videoPlayerView.setVisibility(View.GONE);
                    }
                },e->{
                    e.printStackTrace();
                });
    }

    public void initVideo(){
        AppLogger.e(">>>>>>initVideo");
        videoPlayer.clearMediaItems();
//        File videoPath=new File(Constants.VIDEO_PATH);
//        for (File file : videoPath.listFiles()){
//            if (file.getName().endsWith(".mp4")){
//                Uri uri = null;
//                if(file.exists()) {
//                    uri = Uri.fromFile(file);
//                    MediaItem item = MediaItem.fromUri(uri);
//                    videoPlayer.addMediaItem(item);
//                }
//            }
//        }
//        videoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
//
//        //  ????????????
//        videoPlayer.prepare();
//        videoPlayer.setPlayWhenReady(true);
    }

    public void initAudio(){
        AppLogger.e(">>>>>>initAudio");
        audioPlayer.clearMediaItems();
//        File audioPath=new File(Constants.AUDIO_PATH);
//        for (File file : audioPath.listFiles()){
//            if (file.getName().endsWith(".mp3")){
//                Uri uri = null;
//                if(file.exists()) {
//                    uri = Uri.fromFile(file);
//                    MediaItem item = MediaItem.fromUri(uri);
//                    audioPlayer.addMediaItem(item);
//                }
//            }
//        }
////        audioPlayer.setVolume(1f);
//        audioPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
//        //  ????????????
//        audioPlayer.prepare();
//        // ????????????
//        audioPlayer.setPlayWhenReady(true);
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
        //?????????????????????
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void checkAppPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //??????READ_EXTERNAL_STORAGE
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
        //????????????

    }


    @Override
    protected void onPause() {
        super.onPause();
        AppLogger.e(">>>>>>onPause");
        videoPlayerView.onPause();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    /**
     * ??????finish()??????
     */
    @Override
    public void finish() {
        //super.finish(); //????????????????????????
        AppLogger.e(">>>>>>finish");
        moveTaskToBack(true); //?????????activity???????????????????????????onDestroy()
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
            AppLogger.d( "??????????????????==>>" + data);
            if (data.equals("USB_MOUNT")) {
                String path = intent.getStringExtra("path");
                AppLogger.d(  "??????????????????=path=>>" +  path);
                EventBus.getDefault().post(new UdiskEvent(path));
            }
            //mHandler.sendMessage(msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUdiskEvent(UdiskEvent udiskEvent) {
        AppLogger.e(">>>>>onUdiskEvent:"+udiskEvent.getPath());
        try{
            boolean result= FileUtils.copyDir(udiskEvent.getPath() + "/ad_box", Environment.getExternalStorageDirectory().getPath() + "/ad_box");

            AppLogger.e(">>>>>result:"+result);
            if (result){
                //???????????????????????????????????????
                //????????????????????????????????????????????????
                //?????????????????????????????????
                SysConfig sysConfig= (SysConfig) JSON.parseObject(FileUtils.readFile(Constants.SYS_CONFIG_PATH+"config.json"),SysConfig.class);
                setConfig(sysConfig);

            }

        }catch (Exception ee){
            ee.printStackTrace();
        }

    }

    private void setConfig(SysConfig sysConfig) {
        int maskInt = NetUtil.maskStr2InetMask(sysConfig.getSubmask());
        String ipAndMask = sysConfig.getIp() + "/" + maskInt;
        String localDNS = "114.114.114.114";
        String localGateway=sysConfig.getGateway();
//        String mask=sysConfig.getSubmask();
//        ContentResolver contentResolver = getContentResolver();
//        Settings.System.putInt(contentResolver, "ethernet_use_static_ip", 1);
//
//        Settings.System.putString(contentResolver, "ethernet_static_ip", ipAndMask);
//        Settings.System.putString(contentResolver, "ethernet_static_gateway", localGateway);
//        Settings.System.putString(contentResolver, "ethernet_static_netmask", mask);
//        Settings.System.putString(contentResolver, "ethernet_static_dns1", localDNS);
        Flowable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .onBackpressureLatest()
                .subscribe(integer -> {
                    AppPrefs.getInstance().setServer(sysConfig.getTcpServerIP());
                    AppPrefs.getInstance().setFtpPort(sysConfig.getTcpServerPort());
                    NetUtil.setIp(ipAndMask, localDNS, localGateway);//??????IP
                    audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, sysConfig.getVolume(), AudioManager.FLAG_SHOW_UI);//????????????

                    ToastUtils.getShortToastByString(Utils.getContext(), "??????????????????????????????U???,10s???????????????");
//                    initVideo();
//                    initAudio();
                    //??????service
                    EventBus.getDefault().post(new RebootEvent(true));

                }, e -> {
                    e.printStackTrace();
                });
    }

    /**
     * ???????????????????????????
     * @param commandVo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(CommandVo commandVo) {
        AppLogger.e(">>>>>onCommandEvent:"+ JSON.toJSONString(commandVo));
        this.commandVo=commandVo;

        if (!commandVo.getVideoNo().equals("000")){
            //????????????????????????
            File file=new File(Constants.VIDEO_PATH+commandVo.getVideoNo()+".mp4");
            if (file.exists()){
                ad_pic.setVisibility(View.GONE);
                videoPlayerView.setVisibility(View.VISIBLE);
                videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                int videoNo=Integer.parseInt(commandVo.getVideoNo());

                if (videoNo<=100) {
                    videoPlayer.setVolume(0f);//???????????????100???????????????????????????
                }else{
                    videoPlayer.setVolume(1f);//??????????????????
                }
                AppLogger.e(">>>>>??????????????????:"+ file.getName());

                Uri uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);
//                // ????????????
                pauseVideoPlayer();
                //??????MediaItems
                videoPlayer.clearMediaItems();
                //????????????????????????
                if (commandVo.getVideoMod().equals("03")){
                    //????????????????????? ????????????
                    int count= Integer.parseInt(commandVo.getVideoCirc());//????????????
                    videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    LoopingMediaSource loopingMediaSource = new LoopingMediaSource(new DefaultMediaSourceFactory(Utils.getContext()).createMediaSource(item),count);
                    videoPlayer.setMediaSource(loopingMediaSource,true);

                }else if (commandVo.getVideoMod().equals("01")){
                    videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    //??????????????? ????????????
                    videoPlayer.setMediaItem(item,true);

                }else if (commandVo.getVideoMod().equals("02")){
                    //???????????? ????????????
                    //??????????????? ????????????
                    videoPlayer.setMediaItem(item,true);
                    videoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
                startVideoPlayer();
            }else{
                AppLogger.e(">>>>???????????????????????????????????????");
            }

        }else{//????????????
            if (videoPlayer!=null){
                pauseVideoPlayer();
            }
        }

        if(!commandVo.getAudioNo().equals("000")){//??????????????????

            audioPlayer.setVolume(1f);//????????????

            File file=new File(Constants.AUDIO_PATH+commandVo.getAudioNo()+".mp3");
            if (file.exists()){

                videoPlayer.setVolume(0f);//????????????

                Uri uri = Uri.fromFile(file);
                MediaItem item = MediaItem.fromUri(uri);

                AppLogger.e(">>>>>??????????????????:"+ file.getName());

                //??????MediaItems
                audioPlayer.clearMediaItems();

                //????????????????????????
                if (commandVo.getAudioMod().equals("03")){
                    //????????????????????? ????????????
                    int count= Integer.parseInt(commandVo.getAudioCirc());//????????????
                    audioPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    LoopingMediaSource loopingMediaSource = new LoopingMediaSource(new DefaultMediaSourceFactory(Utils.getContext()).createMediaSource(item),count);
                    audioPlayer.setMediaSource(loopingMediaSource,true);

                }else if (commandVo.getAudioMod().equals("01")){
                    audioPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    //??????????????? ????????????
                    audioPlayer.setMediaItem(item,true);

                }else if (commandVo.getAudioMod().equals("02")){
                    //???????????? ????????????
                    //??????????????? ????????????
                    audioPlayer.setMediaItem(item,true);
                    audioPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
                //  ????????????
                audioPlayer.prepare();
                // ????????????
                audioPlayer.play();

            }else{
                AppLogger.e(">>>>???????????????????????????????????????");
            }

        }else{//????????????

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
            case KEYCODE_LAST:   // ??????
                AppLogger.e(">>>>>:??????");
//                mControl.slowPlay();
                break;
            case KEYCODE_NEXT:   // ??????
                AppLogger.e(">>>>>:??????");
//                mControl.fastPlay();
                break;
            case KEYCODE_UP:   // ??????
                AppLogger.e(">>>>>:??????");
//                mControl.nextVideo();
                break;
            case KEYCODE_DOWN:   // ???
                AppLogger.e(">>>>>:???");
//                mControl.lastVideo();
                break;
            case KEYCODE_PAUSE:  // ok???
                AppLogger.e(">>>>>:ok???");
//                mControl.pauseVideo();
                break;
            case 126:
                AppLogger.e(">>>>>??????");
                break;
            case 86:
                AppLogger.e(">>>>>??????");
                break;
            case 90:
                AppLogger.e(">>>>>??????");
                break;
            case 89:
                AppLogger.e(">>>>>??????");
                break;
            case 24:
                AppLogger.e(">>>>>??????+");

                break;
            case 25:
                AppLogger.e(">>>>>??????-");
                break;
            case 87:
                AppLogger.e(">>>>>?????????");

                break;
            case 88:
                AppLogger.e(">>>>>?????????");

                break;
        }
//        showSeekBar(); // ??????????????????????????????
        return super.onKeyDown(keyCode, event);

    }

}
