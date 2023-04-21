package com.freelycar.voice.recorderlib.recorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.freelycar.voice.recorderlib.recorder.listener.RecordDataListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordFftDataListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordResultListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordStateListener;
import com.freelycar.voice.recorderlib.utils.FileUtils;
import com.freelycar.voice.recorderlib.utils.Logger;

import java.util.Locale;

/**
 * 录音服务
 */
public class RecordService extends Service {
    private static final String TAG = RecordService.class.getSimpleName();

    /**
     * 录音配置
     */
    private static RecordConfig currentConfig = new RecordConfig();

    private final static String ACTION_NAME = "action_type";

    private final static int ACTION_INVALID = 0;

    private final static int ACTION_START_RECORD = 1;

    private final static int ACTION_STOP_RECORD = 2;

    private final static int ACTION_RESUME_RECORD = 3;

    private final static int ACTION_PAUSE_RECORD = 4;

    private final static String PARAM_PATH = "path";


    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.v(TAG,"onBind: ");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        Logger.v(TAG,"onUnbind: ");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.v(TAG, " onDestroy: ");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Logger.v(TAG,"onRebind: ");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.v(TAG, "MyApplication onLowMemory");//在最后一个后台进程被杀时调用
    }

    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                Logger.v(TAG ,"onTrimMemory() app的所有ui被隐藏");
                break;
            case TRIM_MEMORY_RUNNING_MODERATE:
            case TRIM_MEMORY_RUNNING_LOW:
            case TRIM_MEMORY_RUNNING_CRITICAL:
                Logger.v(TAG, "onTrimMemory() app正常运行，系统可能根据LRU缓存规则杀掉缓存的进程了。");
                break;
            case TRIM_MEMORY_BACKGROUND:
            case TRIM_MEMORY_MODERATE:
            case TRIM_MEMORY_COMPLETE:
                Logger.v(TAG, "onTrimMemory() 手机内存很低，系统开始杀app");
                break;
        }
        super.onTrimMemory(level);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_NAME)) {
            Logger.v(TAG,"onStartCommand bundle: "+bundle.toString());
            switch (bundle.getInt(ACTION_NAME, ACTION_INVALID)) {
                case ACTION_START_RECORD:
                    doStartRecording(bundle.getString(PARAM_PATH));
                    break;
                case ACTION_STOP_RECORD:
                    doStopRecording();
                    break;
                case ACTION_RESUME_RECORD:
                    doResumeRecording();
                    break;
                case ACTION_PAUSE_RECORD:
                    doPauseRecording();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }
        Logger.v(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    public static void startRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_START_RECORD);
        intent.putExtra(PARAM_PATH, getFilePath());
        context.startService(intent);
    }

    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_STOP_RECORD);
        context.startService(intent);
    }

    public static void resumeRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_RESUME_RECORD);
        context.startService(intent);
    }

    public static void pauseRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_PAUSE_RECORD);
        context.startService(intent);
    }

    /**
     * 改变录音格式
     */
    public static boolean changeFormat(RecordConfig.RecordFormat recordFormat) {
        if (getState() == RecordHelper.RecordState.IDLE) {
            currentConfig.setFormat(recordFormat);
            return true;
        }
        return false;
    }

    /**
     * 改变录音配置
     */
    public static boolean changeRecordConfig(RecordConfig recordConfig) {
        if (getState() == RecordHelper.RecordState.IDLE) {
            currentConfig = recordConfig;
            return true;
        }
        return false;
    }

    /**
     * 获取录音配置参数
     */
    public static RecordConfig getRecordConfig() {
        return currentConfig;
    }

    public static void changeRecordDir(String recordDir) {
        currentConfig.setRecordDir(recordDir);
    }

    /**
     * 获取当前的录音状态
     */
    public static RecordHelper.RecordState getState() {
        return RecordHelper.getInstance().getState();
    }

    public static void setRecordStateListener(RecordStateListener recordStateListener) {
        RecordHelper.getInstance().setRecordStateListener(recordStateListener);
    }

    public static void setRecordDataListener(RecordDataListener recordDataListener) {
        RecordHelper.getInstance().setRecordDataListener(recordDataListener);
    }

    public static void setRecordSoundSizeListener(RecordSoundSizeListener recordSoundSizeListener) {
        RecordHelper.getInstance().setRecordSoundSizeListener(recordSoundSizeListener);
    }

    public static void setRecordResultListener(RecordResultListener recordResultListener) {
        RecordHelper.getInstance().setRecordResultListener(recordResultListener);
    }

    public static void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        RecordHelper.getInstance().setRecordFftDataListener(recordFftDataListener);
    }

    private void doStartRecording(String path) {
        Logger.v(TAG, "doStartRecording path: %s", path);
        RecordHelper.getInstance().start(path, currentConfig);
    }

    private void doResumeRecording() {
        Logger.v(TAG, "doResumeRecording");
        RecordHelper.getInstance().resume();
    }

    private void doPauseRecording() {
        Logger.v(TAG, "doResumeRecording");
        RecordHelper.getInstance().pause();
    }

    private void doStopRecording() {
        Logger.v(TAG, "doStopRecording");
        RecordHelper.getInstance().stop();
        stopSelf();
    }

    public static RecordConfig getCurrentConfig() {
        return currentConfig;
    }

    public static void setCurrentConfig(RecordConfig currentConfig) {
        RecordService.currentConfig = currentConfig;
    }

    /**
     * 根据当前的时间生成相应的文件名
     * 实例 record_20160101_13_15_12
     */
    private static String getFilePath() {

        String fileDir =
                currentConfig.getRecordDir();
        if (!FileUtils.createOrExistsDir(fileDir)) {
            Logger.w(TAG, "文件夹创建失败：%s", fileDir);
            return null;
        }
        String fileName = "data";
        return String.format(Locale.getDefault(), "%s%s%s", fileDir, fileName, currentConfig.getFormat().getExtension());
    }


}
