package com.freelycar.voice.audiorecorder.countdowntimer;

import com.freelycar.voice.audiorecorder.MyLogUtils;

public class startTimerUtils {
    private static final String TAG = "startTimerUtils";
    private static CountDownTimerSupport mTimer;
    static long millisInFuture = 60000L;

    public static void clickStart(long mills) {
        if (mTimer != null) {
            mTimer.stop();
            mTimer = null;
        }
//        long millisInFuture = 60000L;
        if(mills != 0)
            millisInFuture = mills;
        long countDownInterval = 1000L;
        mTimer = new CountDownTimerSupport(millisInFuture, countDownInterval);
        mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                MyLogUtils.file(TAG,millisUntilFinished + "ms\n" + millisUntilFinished / 1000 + "s");
                MyLogUtils.file(TAG,"CountDownTimerSupport:   "+"onTick : " + millisUntilFinished + "ms");
            }

            @Override
            public void onFinish() {
                MyLogUtils.file(TAG,"倒计时已结束");
                MyLogUtils.file(TAG,"CountDownTimerSupport:   " +"onFinish");
            }

            @Override
            public void onCancel() {
                MyLogUtils.file(TAG,"倒计时已手动停止");
                MyLogUtils.file(TAG,"CountDownTimerSupport:  "+"onCancel");
            }
        });
        mTimer.start();
    }

    public void clickPause() {
        if (mTimer != null) {
            MyLogUtils.file(TAG,"clickPause...");
            mTimer.pause();
        }
    }

    public void clickResume() {
        if (mTimer != null) {
            MyLogUtils.file(TAG,"clickResume...");
            mTimer.resume();
        }
    }

    public void clickCancel() {
        if (mTimer != null) {
            MyLogUtils.file(TAG,"clickCancel...");
            mTimer.stop();
        }
    }

    public void clickResetStart() {
        if (mTimer != null) {
            MyLogUtils.file(TAG,"clickResetStart.");
            mTimer.reset();
            mTimer.start();
        }
    }

}
