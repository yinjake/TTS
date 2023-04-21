package com.freelycar.voice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.freelycar.voice.constants.AppConstants;
import com.freelycar.voice.service.FreeVoiceService;
import com.freelycar.voice.util.MyLogUtils;


public class ServiceDieListenerBroadcastReceiver extends BroadcastReceiver {
    //public static final String RESTART_BROADCAST_ACTION = "com.freelycar.voice.ServiceDieListenerBroadcastReceiver";
    public static final String TAG = "ServiceDieListenerBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !AppConstants.RESTART_BROADCAST_ACTION.equals(intent.getAction())) {
            return;
        }
        String str = TAG;
        MyLogUtils.file(str, "restart broadcast received");
        //Android 8.0 不再允许后台进程直接通过startService方式去启动服务，改为startForegroundService方式启动
        //context.startForegroundService(new Intent(context, TrackingService.class));
        MyLogUtils.file(str, "start MainService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, FreeVoiceService.class));
        } else {
            context.startService(new Intent(context, FreeVoiceService.class));
        }
    }
}
