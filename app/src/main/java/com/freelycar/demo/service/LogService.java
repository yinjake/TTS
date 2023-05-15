package com.freelycar.demo.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.freelycar.demo.activity.DialogActivity;
import com.freelycar.demo.activity.DialogActivityPm;
import com.freelycar.demo.activity.DialogActivityThu;
import com.freelycar.demo.constants.AppConstants;
import com.freelycar.demo.util.MyLogUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.hutool.core.util.URLUtil;

public class LogService extends Service {
    private static final String TAG = "LogService";

    private static final long SAVE_LOG_FILE_ONE_DAY = 60000L;
    //private static final long SAVE_LOG_FILE_ONE_DAY = 60000 * 60L;

    private OffOnlineReceiver offOnlineReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private LogTaskReceiver logTaskReceiver;
    private SDStateMonitorReceiver sdStateReceiver;
    private MyReciver receiver;
    private PowerManager.WakeLock wakeLock;

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String MONITOR_LOG_SIZE_ACTION = "MONITOR_LOG_SIZE";
    private static final String SWITCH_LOG_FILE_ACTION = "SWITCH_LOG_FILE_ACTION";

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @SuppressLint("SimpleDateFormat")
//    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH时mm分");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static class MyReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLogUtils.file(TAG, "MyReciver: " + intent.getAction());
            //abortBroadcast();//截断广播
            Bundle bundle = intent.getExtras();
            String str = bundle.getString("key"); //字符串类型的数据
            MyLogUtils.file(TAG, "MyReciver111: " + str);
        }
    }


    public static class OffOnlineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLogUtils.file(TAG, "OffOnlineReceiver：" + intent.getAction());
            if (intent.getAction().equals(AppConstants.ACTION_OFFONLINETEST)) {

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        MyLogUtils.file(TAG, "LogService onUnbind");
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        MyLogUtils.file(TAG, "LogService onStartCommand");
        offOnlineReceiver = new OffOnlineReceiver();
        IntentFilter intentFilter = new IntentFilter();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter.addAction(AppConstants.ACTION_OFFONLINETEST);
        localBroadcastManager.registerReceiver(offOnlineReceiver, intentFilter);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogUtils.file(TAG, "LogService onDestroy");
        SDStateMonitorReceiver sDStateMonitorReceiver = sdStateReceiver;
        if (sDStateMonitorReceiver != null) {
            unregisterReceiver(sDStateMonitorReceiver);
        }
        LogTaskReceiver logTaskReceiver = this.logTaskReceiver;
        if (logTaskReceiver != null) {
            unregisterReceiver(logTaskReceiver);
        }
        //取消注册 注销\
        localBroadcastManager.unregisterReceiver(offOnlineReceiver);
        sendRestartBroadcast();
        unregisterReceiver(receiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        register();
        deploySwitchLogFileTask();
        new LogCollectorThread().start();
        MyLogUtils.file(TAG, "LogService onCreate");
    }

    @SuppressLint("InvalidWakeLockTag")
    private void init() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(1, TAG);
        }
        MyLogUtils.file(TAG, "LogService init: ");
    }

    private void sendRestartBroadcast() {
        Intent intent = new Intent();
        intent.setAction(AppConstants.RESTART_BROADCAST_ACTION);
        sendBroadcast(intent);
    }


    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addDataScheme(URLUtil.URL_PROTOCOL_FILE);
        SDStateMonitorReceiver sDStateMonitorReceiver = new SDStateMonitorReceiver();
        registerReceiver(sDStateMonitorReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(MONITOR_LOG_SIZE_ACTION);
        intentFilter2.addAction(SWITCH_LOG_FILE_ACTION);
        LogTaskReceiver logTaskReceiver = new LogTaskReceiver();
        registerReceiver(logTaskReceiver, intentFilter2);

        receiver = new MyReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DIALOGTEST);
        registerReceiver(receiver, filter);
    }

    private void deploySwitchLogFileTask() {
        MyLogUtils.file(TAG, "deploySwitchLogFileTask start: ");
        try {
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, new Intent(SWITCH_LOG_FILE_ACTION), 0);
            Calendar calendar = Calendar.getInstance();
            //第一个参数:如果是1是对年份操作，2是对月份操作，3是对星期操作，5是对日期操作，11是对小时操作，12是对分钟操作，13是对秒操作，14是对毫秒操作。
            //第二个参数:则是加或者减指定的 年/月/周/日/时/分/秒/毫秒
            calendar.add(12, 1);
            //参数：该方法有两个参数：
            //calndr_field：这是“日历”类型，是指要更改的日历字段。
            //new_val：这是指要替换的新值。
//            calendar.set(11, 0);
//            calendar.set(12, 0);
//            calendar.set(13, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), SAVE_LOG_FILE_ONE_DAY, broadcast);
            }
            MyLogUtils.file(TAG, "deploySwitchLogFileTask success,next task time is:" + myLogSdf.format(calendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void canDeleteSDLog() {
        MyLogUtils.file(TAG, "canDeleteSDLog: ");
        Calendar calendar = Calendar.getInstance();
//        MyLogUtils.file(TAG, "111: " + calendar.get(Calendar.DAY_OF_WEEK));
//        MyLogUtils.file(TAG, "222: " + calendar.getTime().getDate());
//        MyLogUtils.file(TAG, "333: " + calendar.getTime().getDay());
//        MyLogUtils.file(TAG, "666: " + calendar.get(Calendar.MINUTE));
//        MyLogUtils.file(TAG, "777: " + calendar.get(Calendar.HOUR_OF_DAY));
//        MyLogUtils.file(TAG, "888: " + calendar.get(Calendar.HOUR));
        if (calendar.get(Calendar.MINUTE) >= 47 && calendar.get(Calendar.MINUTE) <= 57 && calendar.get(Calendar.HOUR_OF_DAY) == 8) {
            MyLogUtils.file(TAG, "111: true");
            Intent intent = new Intent(this, DialogActivityPm.class);
            // 此处必须设置flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.HOUR_OF_DAY) == 18) {
            MyLogUtils.file(TAG, "222: true");
            Intent intent = new Intent(this, DialogActivity.class);
            // 此处必须设置flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
          //  playNotificationSound("daka1");
        }

        if (calendar.get(Calendar.MINUTE) == 1 && calendar.get(Calendar.HOUR_OF_DAY) == 18 && calendar.getTime().getDay() == 5) {
            MyLogUtils.file(TAG, "333: true");
            Intent intent = new Intent(this, DialogActivityThu.class);
            // 此处必须设置flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
//        calendar.add(5, 3);
//        try {
//            return sdf.parse(str).before(calendar.getTime());
//        } catch (ParseException e) {
//            MyLogUtils.file(TAG, e.getMessage());
//            MyLogUtils.file(TAG, "canDeleteSDLog fail:");
//            return false;
//        }
    }

    public class LogCollectorThread extends Thread {
        LogCollectorThread() {
            super("LogCollectorThread");
        }

        @Override
        public void run() {
            try {
                MyLogUtils.file(TAG, "LogCollectorThread is running:");
                wakeLock.acquire(1000L);
                wakeLock.setReferenceCounted(false);
                //  LogService.this.killLogcatPro(getProcessInfoList(getAllProcess()));
                //  Thread.sleep(1000L);
                //  LogService.this.handleLog();
                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
                MyLogUtils.file(TAG, Log.getStackTraceString(e));
            }
        }
    }

    public class SDStateMonitorReceiver extends BroadcastReceiver {
        private SDStateMonitorReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                MyLogUtils.file(TAG, "SDCard is UNMOUNTED");
            } else {
                MyLogUtils.file(TAG, "SDCard is MOUNTED");
            }
            new LogCollectorThread().start();
        }
    }

    public class LogTaskReceiver extends BroadcastReceiver {
        LogTaskReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLogUtils.file(TAG, "LogTaskReceiver : " + action);
            if (LogService.SWITCH_LOG_FILE_ACTION.equals(action)) {
                canDeleteSDLog();
            } else if (LogService.MONITOR_LOG_SIZE_ACTION.equals(action)) {
                return;
            }
            new LogCollectorThread().start();
        }
    }
}