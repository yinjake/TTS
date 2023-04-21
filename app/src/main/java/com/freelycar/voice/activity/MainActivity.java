package com.freelycar.voice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.freelycar.voice.R;
import com.freelycar.voice.adapter.MusicAdapter;
import com.freelycar.voice.service.MusicPlayService;
import com.freelycar.voice.MyApp;
import com.freelycar.voice.asrt.BaseSpeechRecognizer;
import com.freelycar.voice.asrt.Sdk;
import com.freelycar.voice.asrt.models.AsrtApiResponse;
import com.freelycar.voice.constants.AppConstants;
import com.freelycar.voice.countdowntimer.startTimerUtils;
import com.freelycar.voice.entity.Music;
import com.freelycar.voice.service.FreeVoiceService;
import com.freelycar.voice.text2speech.FreeTts;
import com.freelycar.voice.constants.TestApi;
import com.freelycar.voice.util.KeepLiveManager;
import com.freelycar.voice.util.MyLogUtils;
import com.freelycar.voice.recorderlib.RecordManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    static Timer timer = new Timer(true);
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
    private static final String[] STYLE_DATA = new String[]{"STYLE_ALL", "STYLE_NOTHING", "STYLE_WAVE", "STYLE_HOLLOW_LUMP"};
    private startTimerUtils helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLogUtils.file(TAG, " onCreate ");
//        setContentView(R.layout.activity_main);
//        mHandler = new MyHandler(Looper.getMainLooper(), new WeakReference<MainActivity>(this));
        //开启严格模式
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        KeepLiveManager.getInstance().registerKeepLiveReceiver(this);
        //setContentView(R.layout.activity_main);
        //左上角显示
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        //设置为1像素大小
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);
        KeepLiveManager.getInstance().setKeepLiveActivity(this);
        KeepLiveManager.getInstance().finishKeepLiveActivity();
//        AndPermission.with(this)
//                .runtime()
//                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
//                        Permission.RECORD_AUDIO})
//                .start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyApp.getContext().startForegroundService(new Intent(MyApp.getContext(), FreeVoiceService.class));
        } else {
            MyApp.getContext().startService(new Intent(MyApp.getContext(), FreeVoiceService.class));
        }
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

    public void onTestReg(View view) {
        new Thread(() -> {
            BaseSpeechRecognizer sr = Sdk.GetSpeechRecognizer(host, port, protocol);
            AsrtApiResponse rsp = null;
            rsp = sr.RecogniteFile("/storage/emulated/0/Record/com.freelycar.voice/data.wav");
            MyLogUtils.file(TAG, "目录 : " + recordManager.getRecordConfig().getRecordDir());
            System.out.println(rsp.statusCode);
            System.out.println(rsp.statusMessage);
            System.out.println(rsp.result);
            MyLogUtils.file(TAG, " 识别结果: " + rsp.result);
            //Toast.makeText(this, "识别结果: " + rsp.result, Toast.LENGTH_SHORT).show();
            if (rsp.result != null) {
                startSpeaker((String) rsp.result);
                if (rsp.result.equals("开门")) {
                    sendRequestTestCon();
                }
                if (rsp.result.equals("音乐")) {
                    doPlayOrPause(null);
                }
                if (rsp.result.equals("停止播放")) {
                    doPlayOrPause(null);
                }
                if (rsp.result.equals("下一首")) {
                    doNext(null);
                    MyLogUtils.file(TAG, " 下一首 ");
                }
                if (rsp.result.equals("上一首")) {
                    doPrevious(null);
                    MyLogUtils.file(TAG, " 下一首 ");
                }
                if (rsp.result.equals("今天天气")) {
                    startSpeaker("多云转阴，东南方转东风 4-5级");
                }
            }
        }).start();
    }

    class MyHandler extends Handler {

        WeakReference<MainActivity> serviceWeakReference;

        public MyHandler(@NonNull Looper looper, WeakReference<MainActivity> serviceWeakReference) {
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
                    //onTestReg();
                    break;
                case 400:
                    //  startReg();
                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyLogUtils.file(TAG, " onResume ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLogUtils.file(TAG, " onStart ");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogUtils.file(TAG, " onDestroy ");
        stopService(new Intent(MainActivity.this, MusicPlayService.class));
        stopService(new Intent(MainActivity.this, FreeVoiceService.class));
        KeepLiveManager.getInstance().unregisterKeepLiveReceiver(this);
    }

    private void sendRequestTestCon() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TestApi.testQueryBoxX("FEDD7A6004888FT0#202", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doStop() {
        recordManager.stop();
        // btRecord.setText("开始");
        isPause = false;
        isStart = false;
    }

    private void doPlay() {
        if (isStart) {
            recordManager.pause();
            //   btRecord.setText("开始");
            isPause = true;
            isStart = false;
        } else {
            if (isPause) {
                recordManager.resume();
            } else {
                recordManager.start();
            }
            //  btRecord.setText("暂停");
            isStart = true;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spUpStyle:
                // audioView.setStyle(AudioView.ShowStyle.getStyle(STYLE_DATA[position]), audioView.getDownStyle());
                break;
            case R.id.spDownStyle:
                // audioView.setStyle(audioView.getUpStyle(), AudioView.ShowStyle.getStyle(STYLE_DATA[position]));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing
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
     *
     * @param view
     */
    public void doPrevious(View view) {
        //创建意图
        Intent intent = new Intent();
        //设置广播频道
        intent.setAction(AppConstants.INTENT_ACTION_PREVIOUS);
        //按意图发送广播
        sendBroadcast(intent);
    }

    /**
     * 下一首按钮单击事件处理方法
     *
     * @param view
     */
    public void doNext(View view) {
        //创建意图
        Intent intent = new Intent();
        //设置广播频道
        intent.setAction(AppConstants.INTENT_ACTION_NEXT);
        //按意图发送广播
        sendBroadcast(intent);
    }

    /**
     * 播放|暂停按钮单击事件处理方法
     *
     * @param view
     */
    public void doPlayOrPause(View view) {
        //创建意图
        Intent intent = new Intent();
        //设置广播频道
        intent.setAction(AppConstants.INTENT_ACTION_PLAY_OR_PAUSE);
        //按意图发送广播
        sendBroadcast(intent);
    }
}
