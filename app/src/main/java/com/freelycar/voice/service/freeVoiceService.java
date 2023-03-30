package com.freelycar.voice.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.BuildConfig;
import com.freelycar.voice.adapter.MusicAdapter;
import com.freelycar.voice.MyApp;
import com.freelycar.voice.asrt.BaseSpeechRecognizer;
import com.freelycar.voice.asrt.Sdk;
import com.freelycar.voice.asrt.models.AsrtApiResponse;
import com.freelycar.voice.constants.AppConstants;
import com.freelycar.voice.countdowntimer.startTimerUtils;
import com.freelycar.voice.entity.Music;
import com.freelycar.voice.text2speech.FreeTts;
import com.freelycar.voice.text2speech.MediaTTSManager;
import com.freelycar.voice.util.BroadcastUtils;
import com.freelycar.voice.constants.TestApi;
import com.freelycar.voice.util.MyLogUtils;
import com.freelycar.voice.recorderlib.RecordManager;
import com.freelycar.voice.recorderlib.recorder.RecordConfig;
import com.freelycar.voice.recorderlib.recorder.RecordHelper;
import com.freelycar.voice.recorderlib.recorder.listener.RecordFftDataListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordResultListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordStateListener;
import com.freelycar.voice.recorderlib.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class freeVoiceService extends Service {
    private static final String TAG = freeVoiceService.class.getSimpleName();

    static MyHandler mHandler;
    private FreeTts whyTTS;

    private static final String host = "123.60.169.72";
    private static final String port = "20001";
    private static final String protocol = "http";

    private String musicName; // 音乐文件名
    private List<Music> musicList; // 音乐列表（数据源）
    private MusicAdapter adapter; // 音乐适配器
    private MyApp app; // 音乐播放器应用程序对象
    private MusicReceiver receiver;//音乐广播接收器

    private boolean isStart = false;
    private boolean isPause = false;
    final RecordManager recordManager = RecordManager.getInstance();
    private startTimerUtils helper;

    @Override
    public IBinder onBind(Intent intent) {
        MyLogUtils.file(TAG, "onBind ");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        MyLogUtils.file(TAG, "onUnbind ");
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new freeVoiceService.MyHandler(Looper.getMainLooper(), new WeakReference<freeVoiceService>(this));
        //开启严格模式
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        whyTTS = MediaTTSManager.getInstance(this, mHandler);
        helper = new startTimerUtils(this, mHandler);
        initRecord();
//        AndPermission.with(this)
//                .runtime()
//                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
//                        Permission.RECORD_AUDIO})
//                .start();
        initAudoRe();
        initMusic();
        initRecordEvent();
        MyLogUtils.file(TAG, "onCreate ");
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        MyLogUtils.file(TAG, "onStartCommand ");
        startTimerUtils.clickStart(5000);
        mHandler.sendMessage(mHandler.obtainMessage(200));
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int i) {
        super.onStart(intent, i);
        MyLogUtils.file(TAG, "onStart ");
    }


    public void initMusic() {
        // 获取音乐播放器应用程序对象
        app = (MyApp) getApplication();
        // 定义存储读写权限数组
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // 检查是否有读权限
        //final int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[0]);

        //执行填充音乐列表的异步任务
        new FillMusicListTask().execute();

        //创建音乐广播接收器
        receiver = new MusicReceiver();
        //创建意图过滤器
        IntentFilter filter = new IntentFilter();
        //通过意图过滤器添加广播频道
        filter.addAction(AppConstants.INTENT_ACTION_UPDATE_PROGRESS);
        //注册音乐广播接收器
        registerReceiver(receiver, filter);
    }

    public void initAudoRe() {
        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(16000));
        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT));
    }

    public void startSpeaker(String text) {
        if (text.isEmpty()) {
            MyLogUtils.file(TAG, "textToSpeak text is null ");
            startTimerUtils.clickResetStart();
            mHandler.sendMessage(mHandler.obtainMessage(200));
        } else {
            //audioFocusController.requestFocus();
            whyTTS.speak(text);
        }
    }

    private void onTestReg() {
        new Thread(() -> {
            BaseSpeechRecognizer sr = Sdk.GetSpeechRecognizer(host, port, protocol);
            //String filename = "/storage/emulated/0/Record/com.freelycar.voice/data.wav";
            AsrtApiResponse rsp;
            rsp = sr.RecogniteFile("/storage/emulated/0/Record/com.freelycar.voice/data.wav");
            MyLogUtils.file(TAG, "目录 : " + recordManager.getRecordConfig().getRecordDir());
            System.out.println(rsp.statusCode);
            System.out.println(rsp.statusMessage);
            System.out.println(rsp.result);
            MyLogUtils.file(TAG, " 识别结果: " + rsp.result);
            //Toast.makeText(this, "识别结果: " + rsp.result, Toast.LENGTH_SHORT).show();
            if (rsp.result != null) {
                startSpeaker((String) rsp.result);
                if (rsp.result.equals("开门") || rsp.result.equals("打开柜门") || rsp.result.equals("开柜门")) {
                    sendRequestTestCon(0);
                } else if (rsp.result.equals("一号柜门") || rsp.result.equals("一号门")) {
                    sendRequestTestCon(1);
                } else if (rsp.result.equals("二号柜门") || rsp.result.equals("二号门")) {
                    sendRequestTestCon(2);
                } else if (rsp.result.equals("三号柜门") || rsp.result.equals("三号门")) {
                    sendRequestTestCon(3);
                } else if (rsp.result.equals("四号柜门") || rsp.result.equals("四号门")) {
                    sendRequestTestCon(4);
                } else if (rsp.result.equals("五号柜门") || rsp.result.equals("五号门")) {
                    sendRequestTestCon(5);
                } else if (rsp.result.equals("六号柜门") || rsp.result.equals("六号门")) {
                    sendRequestTestCon(6);
                }

                if (rsp.result.equals("音乐") || rsp.result.equals("播放音乐") || rsp.result.equals("music")) {
                    doPlayOrPause();
                }
                if (rsp.result.equals("停止播放") || rsp.result.equals("暂停")) {
                    doPlayOrPause();
                }
                if (rsp.result.equals("下一首")) {
                    doNext();
                    MyLogUtils.file(TAG, "下一首");
                }
                if (rsp.result.equals("上一首")) {
                    doPrevious();
                }
                if (rsp.result.equals("今天天气")) {
                    startSpeaker("多云转阴，东南方转东风 4-5级，晚上可能有雨！");
                }
            }
        }).start();
    }

    class MyHandler extends Handler {

        WeakReference<freeVoiceService> serviceWeakReference;

        public MyHandler(@NonNull Looper looper, WeakReference<freeVoiceService> serviceWeakReference) {
            super(looper);
            this.serviceWeakReference = serviceWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                //
            }
            switch (msg.what) {
                case 100:
                    doStop();
                    break;
                case 200:
                    doPlay();
                    break;
                case 300:
                    onTestReg();
                    break;
                case 400:
                    //  startReg();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogUtils.file(TAG, " onDestroy ");
        whyTTS.release();
        mHandler.removeCallbacksAndMessages(null);

        //停止音乐播放服务
        stopService(new Intent(this, MusicPlayService.class));
        //注销广播接收器
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void initRecord() {
        recordManager.init(MyApp.getInstance(), BuildConfig.DEBUG);
        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        String recordDir = String.format(Locale.getDefault(), "%s/Record/com.freelycar.voice/",
                Environment.getExternalStorageDirectory().getAbsolutePath());
        recordManager.changeRecordDir(recordDir);
        initRecordEvent();
    }

    private void initRecordEvent() {
        recordManager.setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(RecordHelper.RecordState state) {
                Logger.i(TAG, "onStateChange %s", state.name());

                switch (state) {
                    case PAUSE:

                        MyLogUtils.file(TAG, "暂停中： ");
                        break;
                    case IDLE:

                        MyLogUtils.file(TAG, "空闲中");
                        break;
                    case RECORDING:

                        MyLogUtils.file(TAG, "录音中");
                        break;
                    case STOP:

                        MyLogUtils.file(TAG, "停止");
                        break;
                    case FINISH:

                        MyLogUtils.file(TAG, "录音结束");
                        MyLogUtils.file(TAG, "---");
                        mHandler.sendMessage(mHandler.obtainMessage(300));
                        // timer.cancel();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String error) {
                Logger.i(TAG, "onError %s", error);
            }
        });
        //声音大小监听
        recordManager.setRecordSoundSizeListener(new RecordSoundSizeListener() {
            @Override
            public void onSoundSize(int soundSize) {
                // tvSoundSize.setText(String.format(Locale.getDefault(), "声音大小：%s db", soundSize));
                // MyLogUtils.file(TAG, "分贝大小： " + String.format(Locale.getDefault(), "声音大小：%s db", soundSize));
            }
        });
        recordManager.setRecordResultListener(new RecordResultListener() {
            @Override
            public void onResult(File result) {
                MyLogUtils.file(TAG, "录音文件： " + result.getAbsolutePath());
                //Toast.makeText(MainActivity.this, "录音文件： " + result.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
        //录音可视化数据回调，傅里叶转换后的频域数据
        recordManager.setRecordFftDataListener(new RecordFftDataListener() {
            @Override
            public void onFftData(byte[] data) {
                // audioView.setWaveData(data);
            }
        });
    }

    private void sendRequestTestCon(int boxId) {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TestApi.testQueryBoxX("FEDD7A6004888FT0#202", boxId);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("sendRequestTest error", String.valueOf(e));
                }
            }
        }).start();
    }

    private void doStop() {
        recordManager.stop();
        isPause = false;
        isStart = false;
    }

    private void doPlay() {
        if (isStart) {
            recordManager.pause();
            isPause = true;
            isStart = false;
        } else {
            if (isPause) {
                recordManager.resume();
            } else {
                recordManager.start();
            }
            isStart = true;
        }
    }

    /**
     * 填充音乐列表异步任务类
     */
    private class FillMusicListTask extends AsyncTask<Void, Integer, Void> {
        /**
         * 耗时工作执行前
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 显示扫描音乐进度条
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 获取音乐列表
            musicList = app.getMusicList();
            MyLogUtils.file(TAG, "doInBackground: " + musicList);
            // 故意耗时，要不然扫描太快结束
            for (long i = 0; i < 2000000000; i++) {
            }
            return null;
        }

        /**
         * 耗时工作执行后
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                // musicList = app.getMusicList();
                // MyLogUtils.file(TAG,"onPostExecute: "+musicList);
                // 判断音乐列表是否有元素
                if (musicList.size() > 0) {
                    // 创建音乐适配器
                    // adapter = new MusicAdapter(MainActivity.this, musicList);
                    // 获取当前要播放的音乐名（默认是音乐播放列表的第一首）
                    musicName = musicList.get(0).getMusicName();
                    //创建意图，用于启动音乐服务
                    Intent intent = new Intent(MyApp.getContext(), MusicPlayService.class);
                    //按意图启动服务
                    startService(intent);

                } else {
                    // 提示用户没有音乐文件
                    //  Toast.makeText(MainActivity.this, "外置存储卡上没有音乐文件！", Toast.LENGTH_SHORT);\
                    MyLogUtils.file(TAG, "外置存储卡上没有音乐文件！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 音乐广播接收器
     */
    private class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取广播频道
            String action = intent.getAction();
            //判断广播频道是否为空
            if (action != null) {
                //根据不同广播频道执行不同操作
                if (AppConstants.INTENT_ACTION_UPDATE_PROGRESS.equals(action)) {
                    //获取播放时长
                    int duration = intent.getIntExtra(AppConstants.DURATION, 0);
                    //计算进度值
                    int progress = app.getCurrentPosition() * 100 / duration;
                    //获取音乐名
                    musicName = app.getMusicList().get(app.getCurrentMusicIndex()).getMusicName();
                    MyLogUtils.file(TAG, "广播接收的当前音乐名: " + musicName);
                }
            }
        }
    }

    /**
     * 上一首按钮单击事件处理方法
     */
    public void doPrevious() {
        //创建意图
        // Intent intent = new Intent();
        //设置广播频道
        // intent.setAction(AppConstants.INTENT_ACTION_PREVIOUS);
        //按意图发送广播
        //sendBroadcast(intent);
        BroadcastUtils.sendBroadCast(AppConstants.INTENT_ACTION_PREVIOUS);
    }

    /**
     * 下一首按钮单击事件处理方法
     */
    public void doNext() {
        //创建意图
        // Intent intent = new Intent();
        //设置广播频道
        // intent.setAction(AppConstants.INTENT_ACTION_NEXT);
        //按意图发送广播
        //sendBroadcast(intent);
        BroadcastUtils.sendBroadCast(AppConstants.INTENT_ACTION_NEXT);
    }

    /**
     * 播放|暂停按钮单击事件处理方法
     */
    public void doPlayOrPause() {
        //创建意图
        //Intent intent = new Intent();
        //设置广播频道
        //intent.setAction(AppConstants.INTENT_ACTION_PLAY_OR_PAUSE);
        //按意图发送广播
        //sendBroadcast(intent);
        BroadcastUtils.sendBroadCast(AppConstants.INTENT_ACTION_PLAY_OR_PAUSE);
    }

}
