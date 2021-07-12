package com.cow.liucy.libcommon.rxnetty;

import java.io.Serializable;

public class CommandVo implements Serializable {
    private String videoNo;//视频编号 3个字节；数值=000表示不播放，数值001-999需要播放的编号
//    视频编号<100时只播放视频不输出视频的音频，
//    视频编号>=100时播放视频同时输出视频的音频，如同时有音频播放任务时，音频播放任务的音频输出优先
    private String videoMod;//视频播放模式 2个字节；01-只播放1次，02-重复循环播放，03-按循环次数播放
    private String videoCirc; // 2个字节；播放模式03时有效
    private String audioNo;//3个字节；数值=000表示不播放，数值001-999需要播放的编号
    private String audioMod;//2个字节；01-只播放1次，02-重复循环播放，03-按循环次数播放
    private String audioCirc;//2个字节；播放模式03时有效，

    public String getVideoNo() {
        return videoNo;
    }

    public void setVideoNo(String videoNo) {
        this.videoNo = videoNo;
    }

    public String getVideoMod() {
        return videoMod;
    }

    public void setVideoMod(String videoMod) {
        this.videoMod = videoMod;
    }

    public String getVideoCirc() {
        return videoCirc;
    }

    public void setVideoCirc(String videoCirc) {
        this.videoCirc = videoCirc;
    }

    public String getAudioNo() {
        return audioNo;
    }

    public void setAudioNo(String audioNo) {
        this.audioNo = audioNo;
    }

    public String getAudioMod() {
        return audioMod;
    }

    public void setAudioMod(String audioMod) {
        this.audioMod = audioMod;
    }

    public String getAudioCirc() {
        return audioCirc;
    }

    public void setAudioCirc(String audioCirc) {
        this.audioCirc = audioCirc;
    }
}
