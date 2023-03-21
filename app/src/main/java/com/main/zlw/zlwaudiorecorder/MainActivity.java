package com.main.zlw.zlwaudiorecorder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.main.zlw.zlwaudiorecorder.asrt.BaseSpeechRecognizer;
import com.main.zlw.zlwaudiorecorder.asrt.Sdk;
import com.main.zlw.zlwaudiorecorder.asrt.common.Common;
import com.main.zlw.zlwaudiorecorder.asrt.models.AsrtApiResponse;
import com.main.zlw.zlwaudiorecorder.asrt.models.Wave;
import com.main.zlw.zlwaudiorecorder.base.MyApp;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.zlw.loggerlib.Logger;
import com.zlw.main.recorderlib.RecordManager;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.RecordHelper;
import com.zlw.main.recorderlib.recorder.listener.RecordFftDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.recorder.listener.RecordStateListener;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btRecord)
    Button btRecord;
    @BindView(R.id.btStop)
    Button btStop;
    @BindView(R.id.btreg)
    Button btReg;
    @BindView(R.id.tvState)
    TextView tvState;
    @BindView(R.id.tvSoundSize)
    TextView tvSoundSize;
    @BindView(R.id.rgAudioFormat)
    RadioGroup rgAudioFormat;
    @BindView(R.id.rgSimpleRate)
    RadioGroup rgSimpleRate;
    @BindView(R.id.tbEncoding)
    RadioGroup tbEncoding;
    @BindView(R.id.audioView)
    AudioView audioView;
    @BindView(R.id.spUpStyle)
    Spinner spUpStyle;
    @BindView(R.id.spDownStyle)
    Spinner spDownStyle;

    private boolean isStart = false;
    private boolean isPause = false;
    final RecordManager recordManager = RecordManager.getInstance();
    private static final String[] STYLE_DATA = new String[]{"STYLE_ALL", "STYLE_NOTHING", "STYLE_WAVE", "STYLE_HOLLOW_LUMP"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启严格模式
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        ButterKnife.bind(this);
        initAudioView();
        initEvent();
        initRecord();
        AndPermission.with(this)
                .runtime()
                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.RECORD_AUDIO})
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doStop();
        initRecordEvent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doStop();
    }

    private void initAudioView() {
        tvState.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, STYLE_DATA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUpStyle.setAdapter(adapter);
        spDownStyle.setAdapter(adapter);
        spUpStyle.setOnItemSelectedListener(this);
        spDownStyle.setOnItemSelectedListener(this);
    }

    private void initEvent() {
        rgAudioFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbPcm:
                        recordManager.changeFormat(RecordConfig.RecordFormat.PCM);
                        break;
                    case R.id.rbMp3:
                        recordManager.changeFormat(RecordConfig.RecordFormat.MP3);
                        break;
                    case R.id.rbWav:
                        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
                        break;
                    default:
                        break;
                }
            }
        });

        rgSimpleRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb8K:
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(8000));
                        break;
                    case R.id.rb16K:
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(16000));
                        break;
                    case R.id.rb44K:
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(44100));
                        break;
                    default:
                        break;
                }
            }
        });

        tbEncoding.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb8Bit:
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_8BIT));
                        break;
                    case R.id.rb16Bit:
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initRecord() {
        recordManager.init(MyApp.getInstance(), BuildConfig.DEBUG);
        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        String recordDir = String.format(Locale.getDefault(), "%s/Record/com.zlw.main/",
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
                        tvState.setText("暂停中");
                        break;
                    case IDLE:
                        tvState.setText("空闲中");
                        break;
                    case RECORDING:
                        tvState.setText("录音中");
                        break;
                    case STOP:
                        tvState.setText("停止");
                        break;
                    case FINISH:
                        tvState.setText("录音结束");
                        tvSoundSize.setText("---");
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
        recordManager.setRecordSoundSizeListener(new RecordSoundSizeListener() {
            @Override
            public void onSoundSize(int soundSize) {
                tvSoundSize.setText(String.format(Locale.getDefault(), "声音大小：%s db", soundSize));
            }
        });
        recordManager.setRecordResultListener(new RecordResultListener() {
            @Override
            public void onResult(File result) {
                Toast.makeText(MainActivity.this, "录音文件： " + result.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
        recordManager.setRecordFftDataListener(new RecordFftDataListener() {
            @Override
            public void onFftData(byte[] data) {
                audioView.setWaveData(data);
            }
        });
    }

    @OnClick({R.id.btRecord, R.id.btStop,R.id.btreg, R.id.jumpTestActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btRecord:
                doPlay();
                break;
            case R.id.btStop:
                doStop();
                break;
            case R.id.btreg:
                // Android 4.0 之后不能在主线程中请求 HTTP 请求
                new Thread(() -> {
                    onStartReg();
                }).start();
                break;
            case R.id.jumpTestActivity:
                startActivity(new Intent(this, TestHzActivity.class));
            default:
                break;
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

    private void onStartReg(){
        String host = "123.60.169.72";
        String port = "20001";
        String protocol = "http";
        BaseSpeechRecognizer sr = Sdk.GetSpeechRecognizer(host, port, protocol);
        String filename = "/storage/emulated/0/Record/com.zlw.main/data.wav";

        AsrtApiResponse rsp = sr.RecogniteFile("/storage/emulated/0/Record/com.zlw.main/data.wav");
        Log.e("目录 : ", recordManager.getRecordConfig().getRecordDir());
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);

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
        String[] pinyins = ((String)rsp.result).split(", ");
        rsp = sr.RecogniteLanguage(pinyins);
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);

        // ============================================
        // 调用ASRT语言模型识别拼音序列2
        pinyins = new String[]{"ni3", "hao3", "a1"};
        rsp = sr.RecogniteLanguage(pinyins);
        System.out.println(rsp.statusCode);
        System.out.println(rsp.statusMessage);
        System.out.println(rsp.result);
    }

    private void doStop() {
        recordManager.stop();
        btRecord.setText("开始");
        isPause = false;
        isStart = false;
    }

    private void doPlay() {
        if (isStart) {
            recordManager.pause();
            btRecord.setText("开始");
            isPause = true;
            isStart = false;
        } else {
            if (isPause) {
                recordManager.resume();
            } else {
                recordManager.start();
            }
            btRecord.setText("暂停");
            isStart = true;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spUpStyle:
                audioView.setStyle(AudioView.ShowStyle.getStyle(STYLE_DATA[position]), audioView.getDownStyle());
                break;
            case R.id.spDownStyle:
                audioView.setStyle(audioView.getUpStyle(), AudioView.ShowStyle.getStyle(STYLE_DATA[position]));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing
    }
}
