package com.freelycar.voice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.freelycar.voice.entity.Music;
import com.freelycar.voice.recorderlib.recorder.wav.WavUtils;
import com.freelycar.voice.recorderlib.utils.ByteUtils;
import com.freelycar.voice.recorderlib.utils.Logger;
import com.freelycar.voice.util.MyLogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = MyApp.class.getSimpleName();
    private static MyApp instance;

    private SimpleDateFormat sdf; // 简单日期格式
    private int currentMusicIndex;//当前音乐索引
    private int currentPosition;//当前播放位置
    private int playMode;//播放模式
    private int progressChangedByUser;//用户修改的播放进度
    String suffix1 = ".flac";
    private static Context context;
    private int count = 0;

    public static Context getContext() {
        return context;
    }

    /**
     * 判断app是否在后台
     */
    public boolean isBackground() {
        if (count <= 0)
            MyLogUtils.file(TAG, "app不在后台");
        else
            MyLogUtils.file(TAG, "app在后台");
        return count <= 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                MyLogUtils.file(TAG, "onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                count++;
                MyLogUtils.file(TAG, "onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyLogUtils.file(TAG, "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                MyLogUtils.file(TAG, "onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (count > 0) {
                    count--;
                }
                MyLogUtils.file(TAG, "onActivityStopped:" + count);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                MyLogUtils.file(TAG, "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                MyLogUtils.file(TAG, "onActivityDestroyed");
            }
        });

        instance = this;
        context = getApplicationContext();
        Logger.w("freelyCarVoice", "TEST-----------------");
        byte[] header1 = WavUtils.generateWavFileHeader(1024, 16000, 1, 16);
        byte[] header2 = WavUtils.generateWavFileHeader(1024, 16000, 1, 16);

        Logger.d("freelyCarVoice", "Wav1: %s", WavUtils.headerToString(header1));
        Logger.d("freelyCarVoice", "Wav2: %s", WavUtils.headerToString(header2));

        Logger.w("freelyCarVoice", "TEST-2----------------");

        Logger.d("freelyCarVoice", "Wav1: %s", ByteUtils.toString(header1));
        Logger.d("freelyCarVoice", "Wav2: %s", ByteUtils.toString(header2));
    }

    public static MyApp getInstance() {
        return instance;
    }


    public int getCurrentMusicIndex() {
        return currentMusicIndex;
    }

    public void setCurrentMusicIndex(int currentMusicIndex) {
        this.currentMusicIndex = currentMusicIndex;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public int getProgressChangedByUser() {
        return progressChangedByUser;
    }

    public void setProgressChangedByUser(int progressChangedByUser) {
        this.progressChangedByUser = progressChangedByUser;
    }

    /**
     * 获取格式化时间
     *
     * @param time 单位是毫秒
     * @return mm:ss格式的时间
     */
    public String getFormatTime(int time) {
        return sdf.format(time);
    }

    /**
     * 生成指定目录下某种类型的文件列表
     *
     * @param dir
     * @param suffix
     * @param typeFileList
     */
    public void makeTypeFileList(File dir, String suffix, List<String> typeFileList) {
        // 获取指定目录下的File数组（File既可以指向目录，也可以指向文件）
        File[] files = dir.listFiles();
        // 遍历File数组
        for (File file : files) {
            // 判断file是否是文件
            if (file.isFile()) { // file是文件
                // 按照后缀来过滤文件
                if (file.getName().endsWith(suffix) || file.getName().endsWith(suffix1)) {
                    // 将满足条件的文件添加到文件列表
                    typeFileList.add(file.getAbsolutePath());
                }
            } else { // file是目录
                // 目录非空，递归调用
                if (file.list() != null) {
                    makeTypeFileList(file, suffix, typeFileList);
                }
            }
        }
    }

    /**
     * 获取音乐列表
     *
     * @return 音乐列表
     */
    public List<Music> getMusicList() {
        // 声明音乐列表
        List<Music> musicList = null;

        // 获取外置存储卡根目录
        File sdRootDir = new File(Environment.getExternalStorageDirectory() + "/Music");
        // 创建后缀字符串
        String suffix = ".mp3";
        // 创建音乐文件列表
        List<String> musicFileList = new ArrayList<>();
        // 调用方法，生成指定目录下某种类型文件列表
        makeTypeFileList(sdRootDir, suffix, musicFileList);
        // 判断音乐文件列表里是否有元素
        if (musicFileList.size() > 0) {
            // 实例化音乐列表
            musicList = new ArrayList<>();
            // 遍历音乐文件列表
            for (String musicFile : musicFileList) {
                // 创建音乐实体
                Music music = new Music();
                // 设置实体属性
                music.setMusicName(musicFile);
                // 将音乐实体添加到音乐列表
                musicList.add(music);
            }
        }

        // 返回音乐列表
        return musicList;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        MyLogUtils.file(TAG, "onActivityCreated");
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        MyLogUtils.file(TAG, "onActivityStarted");
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        MyLogUtils.file(TAG, "onActivityResumed");
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        MyLogUtils.file(TAG, "onActivityPaused");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        MyLogUtils.file(TAG, "onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        MyLogUtils.file(TAG, "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        MyLogUtils.file(TAG, "onActivityDestroyed");
    }
}
