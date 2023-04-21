package com.freelycar.voice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.freelycar.voice.service.FreeVoiceService;
import com.freelycar.voice.util.MyLogUtils;

public class StartBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_BOOT.equals(intent.getAction())) {
            return;
        }
        MyLogUtils.file(TAG, "start broadcast received");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, FreeVoiceService.class));
        } else {
            context.startService(new Intent(context, FreeVoiceService.class));
        }
        MyLogUtils.file(TAG, "start MainService");
    }
}
