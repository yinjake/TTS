package com.freelycar.voice.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.freelycar.voice.BuildConfig;
import com.freelycar.voice.R;
import com.freelycar.voice.adapter.MusicAdapter;
import com.freelycar.voice.service.MusicPlayService;
import com.freelycar.voice.MyApp;
import com.freelycar.voice.asrt.BaseSpeechRecognizer;
import com.freelycar.voice.asrt.Sdk;
import com.freelycar.voice.asrt.common.Common;
import com.freelycar.voice.asrt.models.AsrtApiResponse;
import com.freelycar.voice.asrt.models.Wave;
import com.freelycar.voice.constants.AppConstants;
import com.freelycar.voice.countdowntimer.startTimerUtils;
import com.freelycar.voice.entity.Music;
import com.freelycar.voice.service.freeVoiceService;
import com.freelycar.voice.text2speech.FreeTts;
import com.freelycar.voice.constants.TestApi;
import com.freelycar.voice.util.MyLogUtils;
import com.freelycar.voice.recorderlib.utils.Logger;
import com.freelycar.voice.recorderlib.RecordManager;
import com.freelycar.voice.recorderlib.recorder.RecordConfig;
import com.freelycar.voice.recorderlib.recorder.RecordHelper;
import com.freelycar.voice.recorderlib.recorder.listener.RecordFftDataListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordResultListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.freelycar.voice.recorderlib.recorder.listener.RecordStateListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

//    @BindView(R.id.btRecord)
//    Button btRecord;
//    @BindView(R.id.btStop)
//    Button btStop;
//    @BindView(R.id.btreg)
//    Button btReg;
//    @BindView(R.id.tvState)
//    TextView tvState;
//    @BindView(R.id.tvSoundSize)
//    TextView tvSoundSize;
//    @BindView(R.id.rgAudioFormat)
//    RadioGroup rgAudioFormat;
//    @BindView(R.id.rgSimpleRate)
//    RadioGroup rgSimpleRate;
//    @BindView(R.id.tbEncoding)
//    RadioGroup tbEncoding;
//    @BindView(R.id.audioView)
//    AudioView audioView;
//    @BindView(R.id.spUpStyle)
//    Spinner spUpStyle;
//    @BindView(R.id.spDownStyle)
//    Spinner spDownStyle;

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
//        whyTTS = MediaTTSManager.getInstance(this, mHandler);
//        helper = new startTimerUtils(this, mHandler);
//        ButterKnife.bind(this);
//        initAudioView();
//        initEvent();
//        initRecord();
        AndPermission.with(this)
                .runtime()
                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.RECORD_AUDIO})
                .start();
//        initAudoRe();
//        initMusic();
        Intent intent = new Intent(this, freeVoiceService.class);
        startService(intent);
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
        final int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[0]);
        // 如果没有授权，那么就请求读权限
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 0);
            return;
        }
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
                    onTestReg();
                    break;
                case 400:
                    //  startReg();
                    break;
            }
        }
    }

    public void startReg() {
        // runOnUiThread(new Runnable() {
        //  public void run() {
        //timer.cancel();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
        // doStop();
        //mHandler.sendMessage(mHandler.obtainMessage(100));
        //mySleep(100);
        //doPlay();
        mHandler.sendMessage(mHandler.obtainMessage(200));
        //doReg
        //  mHandler.sendMessage(mHandler.obtainMessage(300));
        MyLogUtils.file(TAG, "倒计时进行中。 ");
//            }
//        };
//        timer.schedule(task, 0, 6000);
//        MyLogUtils.file(TAG, "倒计时结束了... ");
        //    }
        // });
    }

    private synchronized void mySleep(long t) {
        try {
            wait(t);
        } catch (InterruptedException e) {
            MyLogUtils.file(TAG, "wait error " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLogUtils.file(TAG, " onResume ");
        //doStop();
        //initRecordEvent();
        //startTimerUtils.clickStart(5000);
        //mHandler.sendMessage(mHandler.obtainMessage(200));
        //  startReg();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLogUtils.file(TAG, " onStart ");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        MyLogUtils.file(TAG, " onStop ");
//        //     timer.cancel();
//        //doStop();
//        mHandler.removeCallbacksAndMessages(null);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogUtils.file(TAG, " onDestroy ");
        // timer.cancel();
        //  whyTTS.release();
        //  mHandler.removeCallbacksAndMessages(null);

        //停止音乐播放服务
        //  stopService(new Intent(MainActivity.this, MusicPlayService.class));
        //注销广播接收器
        // if (receiver != null) {
        //       unregisterReceiver(receiver);
        //    }
    }


    private void initAudioView() {
        // tvState.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, STYLE_DATA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spUpStyle.setAdapter(adapter);
        //  spDownStyle.setAdapter(adapter);
        //  spUpStyle.setOnItemSelectedListener(this);
        //  spDownStyle.setOnItemSelectedListener(this);
    }

//    private void initEvent() {
//        rgAudioFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rbPcm:
//                        recordManager.changeFormat(RecordConfig.RecordFormat.PCM);
//                        break;
//                    case R.id.rbMp3:
//                        recordManager.changeFormat(RecordConfig.RecordFormat.MP3);
//                        break;
//                    case R.id.rbWav:
//                        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//        rgSimpleRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rb8K:
//                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(8000));
//                        break;
//                    case R.id.rb16K:
//                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(16000));
//                        break;
//                    case R.id.rb44K:
//                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(44100));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//        tbEncoding.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rb8Bit:
//                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_8BIT));
//                        break;
//                    case R.id.rb16Bit:
//                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//    }

    private void initRecord() {
        recordManager.init(MyApp.getInstance(), BuildConfig.DEBUG);
        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        String recordDir = String.format(Locale.getDefault(), "%s/Record/com.freelycar.plugin/",
                Environment.getExternalStorageDirectory().getAbsolutePath());
        recordManager.changeRecordDir(recordDir);
        //initRecordEvent();
    }

    private void initRecordEvent() {
        recordManager.setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(RecordHelper.RecordState state) {
                Logger.i(TAG, "onStateChange %s", state.name());

                switch (state) {
                    case PAUSE:
                        //     tvState.setText("暂停中");
                        break;
                    case IDLE:
                        //     tvState.setText("空闲中");
                        break;
                    case RECORDING:
                        //       tvState.setText("录音中");
                        break;
                    case STOP:
                        //       tvState.setText("停止");
                        break;
                    case FINISH:
                        //      tvState.setText("录音结束");
                        //       tvSoundSize.setText("---");
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
                //    tvSoundSize.setText(String.format(Locale.getDefault(), "声音大小：%s db", soundSize));
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

    //@OnClick({R.id.btRecord, R.id.btStop, R.id.btreg, R.id.jumpTestActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btRecord:
                // doPlay();
                //doNext(null);//下一首
                break;
            case R.id.btStop:
                //doStop();
                //startPortData("01 05 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 73 b8");
                // printClass();
                doPrevious(null);
                break;
            case R.id.btreg:
                // Android 4.0 之后不能在主线程中请求 HTTP 请求
//                new Thread(() -> {
//                    onStartReg();
//                }).start();
                doPlayOrPause(null);//音乐播放或暂停
                break;
            case R.id.jumpTestActivity:
                startActivity(new Intent(this, TestHzActivity.class));
            default:
                break;
        }
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
                    Log.e("sendRequestTest error", String.valueOf(e));
                }
            }
        }).start();
    }


    public static void printClass() {
        try {
            Context c = MyApp.getInstance().createPackageContext("com.freelycar.plugin", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            //载入这个类
            Class clazz = c.getClassLoader().loadClass("com.freelycar.plugin.activity.MainActivity");
//            MyLogUtils.file(TAG, "method: " + clazz.getName());
//            Field[] ff = clazz.getDeclaredFields();
//            for (Field f : ff) {
//                MyLogUtils.file(TAG, "field: " + f.getName());
//            }

            Object object = clazz.newInstance();

            // Method method = clazz.getDeclaredMethod("pauseDisplayView", new Class[]{String.class, String.class});//数组传参
            // method.invoke(clazz, new String[]{"devusername", "666666"});

            Method[] methods = clazz.getDeclaredMethods();
            for (Method meth : methods) {
                MyLogUtils.file(TAG, "name:" + meth.getName());
                if (meth.getName().equals("pushApk")) {
                    meth.setAccessible(true);
                    //      meth.invoke(object,"");
                    meth.invoke(object);
                    MyLogUtils.file(TAG, "invoke ...");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

//    @SuppressLint("StaticFieldLeak")
//    private void accessWSAction() {
//        new AsyncTask<String, Void, Object>() {
//
//            //在doInBackground 执行完成后，onPostExecute 方法将被UI 线程调用，
//            // 后台的计算结果将通过该方法传递到UI 线程，并且在界面上展示给用户.
//            protected void onPostExecute(Object result) {
//                super.onPostExecute(result);
//            }
//
//            //该方法运行在后台线程中，因此不能在该线程中更新UI，UI线程为主线程
//            protected Object doInBackground(String... params) {
//                String result = sayHi("zxn");
////						activity_main_btn1.setText("请求结果为："+result);
//                return result;
//            }
//
//        }.execute();
//    }

    private void onStartReg() {
        String host = "123.60.169.72";
        String port = "20001";
        String protocol = "http";
        BaseSpeechRecognizer sr = Sdk.GetSpeechRecognizer(host, port, protocol);
        String filename = "/storage/emulated/0/Record/com.freelycar.voice/data.wav";

        AsrtApiResponse rsp = sr.RecogniteFile("/storage/emulated/0/Record/com.freelycar.voice/data.wav");
        Log.e("目录 : ", recordManager.getRecordConfig().getRecordDir());
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);
        Toast.makeText(this, "识别结果: " + rsp.result, Toast.LENGTH_SHORT).show();
        startSpeaker((String) rsp.result);
        // ============================================
        // 调用ASRT识别语音序列
        byte[] wavBytes = Common.readBinFile(filename);
        Wave wav = new Wave();
        wav.deserialize(wavBytes);
        byte[] sampleBytes = wav.getRawSamples();
        int sampleRate = wav.sampleRate;
        int channels = wav.channels;
        int byteWidth = wav.sampleWidth;
        rsp = sr.Recognite(sampleBytes, sampleRate, channels, byteWidth);
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);

        // ============================================
        // 调用ASRT声学模型识别语音序列
        wavBytes = Common.readBinFile(filename);
        wav = new Wave();
        wav.deserialize(wavBytes);
        sampleBytes = wav.getRawSamples();
        sampleRate = wav.sampleRate;
        channels = wav.channels;
        byteWidth = wav.sampleWidth;
        rsp = sr.RecogniteSpeech(sampleBytes, sampleRate, channels, byteWidth);
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);

        // ============================================
        // 调用ASRT语言模型识别拼音序列1
        String[] pinyins = ((String) rsp.result).split(", ");
        rsp = sr.RecogniteLanguage(pinyins);
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);

        // ============================================
        // 调用ASRT语言模型识别拼音序列2
//        pinyins = new String[]{"ni3", "hao3", "a1"};
//        rsp = sr.RecogniteLanguage(pinyins);
//        System.out.println(rsp.statusCode);
//        System.out.println(rsp.statusMessage);
//        System.out.println(rsp.result);
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
                    Intent intent = new Intent(MainActivity.this, MusicPlayService.class);
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
