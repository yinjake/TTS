package com.freelycar.voice.text2speech;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.RequiresApi;

import com.freelycar.voice.util.MyLogUtils;
import com.freelycar.voice.countdowntimer.startTimerUtils;

import java.util.HashMap;
import java.util.Locale;

public class MediaTTSManager implements FreeTts {

    private static final String TAG = "MediaTTSManager";
    private static volatile FreeTts manager = null;
    private static TextToSpeech mSpeech = null;
    private Context mContext;
    private String wavPath;
    //private MediaPlayer player;
    private FocusTts mFocusTts;
    private HashMap<String, String> myHashRender = new HashMap();
    private Handler mHandler;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public MediaTTSManager(Context context,Handler handler) {
        this.mContext = context;
        //this.mFocusTts = listener;
        mHandler = handler;
        wavPath = Environment.getExternalStorageDirectory() + "/temp.wav";
      //  player = new MediaPlayer();
        initSpeech();
    }

//    public MediaTTSManager(Handler handler) {
//        mHandler = handler;
//    }

    /**
     * Init TTS and set params
     */
    private void initSpeech() {
        mSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mSpeech.setLanguage(Locale.getDefault());
                } else {
                    MyLogUtils.file(TAG, "TTS Initialization failed");
                }

                // mSpeech.setLanguage(Locale.ENGLISH);

                //判断是否转化成功
//                if (status == TextToSpeech.SUCCESS) {
//                    //设置语言为中文
//                    int languageCode = mSpeech.setLanguage(Locale.CHINESE);
//                    //判断是否支持这种语言，Android原生不支持中文，使用科大讯飞的tts引擎就可以了
//                    if (languageCode == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Log.d("TAG", "onInit: 不支持这种语言");
//                    } else {
//                        //不支持就改成英文
//                        mSpeech.setLanguage(Locale.US);
//                    }
//                    //设置音调,值越大声音越尖（女生），值越小则变成男声,1.0是常规
//                    mSpeech.setPitch(1.0f);
//                    //设置语速
//                    mSpeech.setSpeechRate(1.0f);
//                    //在onInIt方法里直接调用tts的播报功能
//                    mSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
//                }
            }
        });

        mSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener(){
            @Override
            public void onStart(String utteranceId) {
                //播报开始
                MyLogUtils.file(TAG, "speak onStart...");
                //BroadcastUtils.sendBroadCast("TURN_VOICE_ZREO");
            }

            @Override
            public void onDone(String utteranceId) {
                //播报结束
                MyLogUtils.file(TAG, "speak onDone...");
                //mFocusTts.onDone(utteranceId);
                //BroadcastUtils.sendBroadCast("TURN_VOICE_RESUME");
                //mHandler.sendMessage(mHandler.obtainMessage(400));
                //startReg();
                startTimerUtils.clickResetStart();
                mHandler.sendMessage(mHandler.obtainMessage(200));
            }

            @Override
            public void onError(String utteranceId) {
                //播报出错
                MyLogUtils.file(TAG, "speak onError...");
               // mHandler.sendMessage(mHandler.obtainMessage(200));
            }
        });

    }

//    class TTSListener implements TextToSpeech.OnInitListener {
//        @Override
//        public void onInit(int status) {
//            if (mSpeech1 != null) {
//                int isSupportChinese = mSpeech1.isLanguageAvailable(Locale.CHINESE);//是否支持中文
//                mSpeech1.getMaxSpeechInputLength();//最大播报文本长度
//
//                if (isSupportChinese == TextToSpeech.LANG_AVAILABLE) {
//                    int setLanRet = mSpeech1.setLanguage(Locale.CHINESE);//设置语言
//                    int setSpeechRateRet = mSpeech1.setSpeechRate(1.0f);//设置语
//                    int setPitchRet = mSpeech1.setPitch(1.0f);//设置音量
//                    String defaultEngine = mSpeech1.getDefaultEngine();//默认引擎
//                    if (status == TextToSpeech.SUCCESS) {
//                        //初始化TextToSpeech引擎成功，初始化成功后才可以play等
//                    }
//                }
//            } else {
//                //初始化TextToSpeech引擎失败
//            }
//        }
//    }
//    TextToSpeech mSpeech1 = new TextToSpeech(MyApplication.getContext(), new TTSListener());


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static FreeTts getInstance(Context context,Handler mHandler) {
        if (manager == null) {
            synchronized (MediaTTSManager.class) {
                if (manager == null) {
                    manager = new MediaTTSManager(context,mHandler);
                }
            }
        }
        return manager;
    }

    @Override
    public void speak(String content) {
        MyLogUtils.file(TAG, "speak content: " + content);
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, content);
        //int r = mSpeech.synthesizeToFile(content, myHashRender, wavPath);
        int r = mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null,"");
        if (r == TextToSpeech.SUCCESS) {
            MyLogUtils.file(TAG, "speak success");
        } else {
            MyLogUtils.file(TAG, "speak fail");
            mHandler.sendMessage(mHandler.obtainMessage(200));
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        try {
//            player.reset();
//            player.setDataSource(wavPath);
//            player.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        player.start();
    }


    /**
     * pause the TTS
     */
    @Override
    public void pause() {
//        if (player.isPlaying()) {
//            player.pause();
//        }
    }

    /**
     * reset the TTS
     */
    @Override
    public void resume() {
      //  player.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setSpeechRate(float newRate) {
        //6.0+可以设置
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            player.setPlaybackParams(player.getPlaybackParams().setSpeed(newRate));
//        } else {
//            MyLogUtils.file(TAG,"setSpeechRate: 版本过低，接口不可用");
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setSpeechPitch(float newPitch) {
        //6.0+可以设置
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            player.setPlaybackParams(player.getPlaybackParams().setPitch(newPitch));
//        } else {
//            MyLogUtils.file(TAG, "setSpeechPitch: 版本过低，接口不可用");
//        }

    }

    /**
     * stop the TTS
     */
    @Override
    public void release() {
//        player.stop();
//        player.release();
        mSpeech.shutdown();
        mSpeech.stop();
    }
}
