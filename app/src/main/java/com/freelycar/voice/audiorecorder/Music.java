package com.freelycar.voice.audiorecorder;

import java.security.PrivateKey;

public class Music {
    private String musicName;//音乐名
    private int duration;//音乐播放时长

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Music{" +
                "musicName='" + musicName + '\'' +
                '}';
    }
}
