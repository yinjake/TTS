package com.freelycar.demo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.freelycar.demo.constants.AppConstants;
import com.freelycar.demo.countdowntimer.CountDownTimerSupport;
import com.freelycar.demo.countdowntimer.OnCountDownTimerListener;
import com.freelycar.demo.util.MyLogUtils;
import com.freelycar.demo.view.dialog.myview.RxDialogmMon;

public class DialogActivity extends Activity {
    private static final String TAG = DialogActivity.class.getSimpleName();
    private static CountDownTimerSupport mTimer;
    long countDownInterval = 1000L;
    static long millisInFuture = 60000L;
    RxDialogmMon mRxDialogMonLoading;
    private static Handler DialogHandler;
    private static int countInt = 8;
    MyReciver receiver;

    public static class MyReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLogUtils.file(TAG, "MyReciver: " + intent.getAction());
            //abortBroadcast();//截断广播
            Bundle bundle = intent.getExtras();
            String str = bundle.getString("key"); //字符串类型的数据
            //int i = bundle.getInt("key");//整形数据
            MyLogUtils.file(TAG, "MyReciver111: " + str);
        }
    }

    //AppcompaActivity相对于Activity的区别
    //主界面带有toolbar的标题栏；
    //theme主题只能用android:theme=”@style/AppTheme （appTheme主题或者其子类），而不能用android:style。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new MyReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DIALOGTEST); //要与之前的action保持一致，最好以当前包名自己定义的Service的名称
        registerReceiver(receiver, filter);
        //setContentView(R.layout.dynamic_activity);
        testDialog();
//        initUtils();
        DialogHandler = new Handler(Looper.getMainLooper());
        DialogHandler.post(new Runnable() {
            @Override
            public void run() {
                initUtils();
            }
        });
        playNotificationSound();
    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/daka1");
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
            MyLogUtils.file(TAG, "playNotificationSound");
        } catch (Exception e) {
            MyLogUtils.file(TAG, "playNotificationSound: fail");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // MyLogUtils.file(TAG, " onDestroy: " + MyApp.isBackground());
        unregisterReceiver(receiver);
        mRxDialogMonLoading.dismiss();
        mRxDialogMonLoading = null;
        mTimer.stop();
        mTimer = null;
        //Runtime.getRuntime().gc();
    }


    private void initUtils() {
//        Looper.prepare();
//        ToastUtils.show("这是一条Toast");
//        Looper.loop();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mTimer = new CountDownTimerSupport(millisInFuture, countDownInterval);
                mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        MyLogUtils.file(TAG, millisUntilFinished / 1000 + "s");
                        //MyLogUtils.file(TAG, "CountDownTimerSupport:   " + "onTick : " + millisUntilFinished + "ms");
                    }

                    @Override
                    public void onFinish() {
                        MyLogUtils.file(TAG, "倒计时已结束");
                        MyLogUtils.file(TAG, "CountDownTimerSupport:   " + "onFinish");
                        mRxDialogMonLoading.cancel();
                        mRxDialogMonLoading.dismiss();
                        DialogHandler = new Handler(Looper.getMainLooper());
                        DialogHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                clickResetStart();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        MyLogUtils.file(TAG, "倒计时已手动停止");
                        MyLogUtils.file(TAG, "CountDownTimerSupport:  " + "onCancel");
                    }
                });
                mTimer.start();
                Looper.loop();
            }
        }).start();
    }

    public synchronized void mySleep(long t) {
        try {
            wait(t);
        } catch (InterruptedException e) {
            MyLogUtils.file(TAG, "wait error " + e);
        }
    }

    private static Handler getHandler() {
        if (null == DialogHandler) {
            DialogHandler = new Handler(Looper.getMainLooper());
        }
        return DialogHandler;
    }


    public void clickResetStart() {
        MyLogUtils.file(TAG, "clickResetStart " + countInt);
        try {
            if (countInt >= 0) {
                testDialog();
                if (mTimer != null) {
                    MyLogUtils.file(TAG, "clickResetStart start:");
                    mTimer.reset();
                    mTimer.start();
                }
                countInt--;
            } else {
                mRxDialogMonLoading.dismiss();
                countInt = 3;
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testDialog() {
        mRxDialogMonLoading = new RxDialogmMon(this);
        mRxDialogMonLoading.show();
    }


//    private void justShowDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setTitle("service中弹出Dialog了")
//                .setMessage("是否关闭dialog？")
//                .setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int whichButton) {
//                            }
//                        })
//                .setNegativeButton("取消",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int whichButton) {
//                            }
//                        });
//        //下面这行代码放到子线程中会 Can't create handler inside thread that has not called Looper.prepare()
//        AlertDialog dialog = builder.create();
//        //设置点击其他地方不可取消此 Dialog
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        //8.0系统加强后台管理，禁止在其他应用和窗口弹提醒弹窗，如果要弹，必须使用TYPE_APPLICATION_OVERLAY，否则弹不出
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
//        } else {
//            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
//        }
//        dialog.show();
//    }

}
