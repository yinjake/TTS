package com.freelycar.voice.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.freelycar.voice.activity.MainActivity;
import com.freelycar.voice.receiver.KeepLiveReceiver;

import java.lang.ref.WeakReference;

public class KeepLiveManager {
    private static final KeepLiveManager ourInstance = new KeepLiveManager();
    public static KeepLiveManager getInstance() {
        return ourInstance;
    }
    private KeepLiveManager() {
    }
    //弱引用，防止内存泄漏
    private WeakReference<MainActivity> reference;
    private KeepLiveReceiver receiver;
    public void setKeepLiveActivity(MainActivity activity) {
        reference = new WeakReference<>(activity);
    }
    //开启透明Activity
    public void startKeepLiveActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    //关闭透明Activity
    public void finishKeepLiveActivity() {
        if (reference != null && reference.get() != null) {
            reference.get().finish();
        }
    }
    //注册广播
    public void registerKeepLiveReceiver(Context context) {
        receiver = new KeepLiveReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(receiver, filter);
    }
    //反注册
    public void unregisterKeepLiveReceiver(Context context){
        if(receiver != null){
            context.unregisterReceiver(receiver);
        }
    }
}
