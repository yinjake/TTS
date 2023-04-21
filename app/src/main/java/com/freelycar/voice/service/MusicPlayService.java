package com.freelycar.voice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.freelycar.voice.MyApp;
import com.freelycar.voice.constants.AppConstants;
import com.freelycar.voice.entity.Music;
import com.freelycar.voice.util.MyLogUtils;
import com.freelycar.voice.util.ReduceThreadPool;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;


public class MusicPlayService extends Service implements AppConstants {
    private static final String TAG = MusicPlayService.class.getSimpleName();

    private MediaPlayer mp;
    private String musicName;
    private Thread thread;
    private boolean isRunning = true;
    private List<Music> musicList;
    private MyApp app;
    private MyMusicReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        app = (MyApp) getApplication();
        musicList = app.getMusicList();
        MyLogUtils.file(TAG, "onCreate: " + musicList);
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //完成监听器
                //下一首
                nextMusic();
            }
        });
        ReduceThreadPool threadPool = new ReduceThreadPool(10);
        IntStream.range(0, 10).forEach((i) -> {
            threadPool.execute(() -> {
                while (isRunning) {
                    try {
                        if (mp.isPlaying()) {
                            Intent intent = new Intent();
                            intent.setAction(INTENT_ACTION_UPDATE_PROGRESS);
                            app.setCurrentPosition(mp.getCurrentPosition());
                            intent.putExtra(DURATION, mp.getDuration());
                            sendBroadcast(intent);
                        }
                    } catch (IllegalStateException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + "--->>ReduceThreadPool");
            });
        });
//        thread = new Thread((Runnable) () -> {
//            while (isRunning) {
//                try {
//                    if (mp.isPlaying()) {
//                        Intent intent = new Intent();
//                        intent.setAction(INTENT_ACTION_UPDATE_PROGRESS);
//                        app.setCurrentPosition(mp.getCurrentPosition());
//                        intent.putExtra(DURATION, mp.getDuration());
//                        sendBroadcast(intent);
////                        try {
////                            Thread.sleep(500);
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                } catch (IllegalStateException|NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
        receiver = new MyMusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_PLAY_OR_PAUSE);
        filter.addAction(INTENT_ACTION_PLAY);
        filter.addAction(INTENT_ACTION_PREVIOUS);
        filter.addAction(INTENT_ACTION_NEXT);
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        musicName = musicList.get(app.getCurrentMusicIndex()).getMusicName();
        MyLogUtils.file(TAG, "onStartCommand: " + musicName);
        try {
            mp.setDataSource(musicName);
            mp.prepare();
            app.setCurrentPosition(1);
            Intent intent1 = new Intent();
            intent1.setAction(INTENT_ACTION_UPDATE_PROGRESS);
            intent1.putExtra(DURATION, mp.getDuration());
            sendBroadcast(intent1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }
        unregisterReceiver(receiver);
        //isRunning = false;
        //thread.interrupt();
        //thread = null;
    }

    private void play() {
        try {
            mp.reset();
            musicName = musicList.get(app.getCurrentMusicIndex()).getMusicName();
            mp.setDataSource(musicName);
            mp.prepare();
            mp.seekTo(app.getCurrentPosition());
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //上一首
    private void previousMusic() {
        if (app.getCurrentPosition() > 0) {
            app.setCurrentMusicIndex(app.getCurrentMusicIndex() - 1);
        } else {
            app.setCurrentMusicIndex(musicList.size() - 1);
        }
        app.setCurrentPosition(0);
        play();
    }

    //下一首
    private void nextMusic() {
        if (app.getCurrentMusicIndex() < musicList.size() - 1) {
            app.setCurrentMusicIndex(app.getCurrentMusicIndex() + 1);
        } else {
            app.setCurrentMusicIndex(0);
        }
        app.setCurrentPosition(0);
        play();
    }

    private void pause() {
        mp.pause();
        app.setCurrentPosition(mp.getCurrentPosition());
        Intent intent = new Intent();
        intent.setAction(INTENT_ACTION_UPDATE_PROGRESS);
        intent.putExtra(DURATION, mp.getDuration());
        sendBroadcast(intent);
    }

    private class MyMusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLogUtils.file(TAG, "MyMusicReceiver: " + action);
            if (action != null) {
                switch (action) {
                    case INTENT_ACTION_PLAY:
                        app.setCurrentPosition(1);
                        play();
                        break;
                    case INTENT_ACTION_PLAY_OR_PAUSE:
                        if (mp.isPlaying()) {
                            pause();
                        } else {
                            play();
                        }
                        break;
                    case INTENT_ACTION_PREVIOUS:
                        previousMusic();
                        break;
                    case INTENT_ACTION_NEXT:
                        nextMusic();
                        break;
                }
            }
        }
    }
}
