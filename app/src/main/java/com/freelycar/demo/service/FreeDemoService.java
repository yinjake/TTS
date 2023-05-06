package com.freelycar.demo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.freelycar.demo.R;
import com.freelycar.demo.activity.DialogActivity;
import com.freelycar.demo.activity.DialogActivityPm;
import com.freelycar.demo.activity.DialogActivityThu;
import com.freelycar.demo.activity.DynamicActivity;
import com.freelycar.demo.activity.IssueReproActivity;
import com.freelycar.demo.activity.MainActivity;
import com.freelycar.demo.MyApp;
import com.freelycar.demo.activity.RippleActivity;
import com.freelycar.demo.activity.TrainActivity;
import com.freelycar.demo.constants.AppConstants;
import com.freelycar.demo.view.dialog.myview.RxDialogAcfunVideoLoading;
import com.freelycar.demo.view.dialog.RxDialogScaleView;
import com.freelycar.plugin.service.Person;
import com.freelycar.demo.text2speech.FreeTts;
import com.freelycar.demo.util.MyLogUtils;
import com.freelycar.plugin.service.IAidlInterface;

import java.lang.ref.WeakReference;
import java.util.Random;


public class FreeDemoService extends Service {
    private static final String TAG = FreeDemoService.class.getSimpleName();

    // static MyHandler mHandler;
    private FreeTts whyTTS;
    // private startTimerUtils helper;
    //在 Service 中创建全局变量 mHandler
    private static Handler myFreeHandler;

    IntentFilter intentFilter;
    ApplicationReceiver mbReceiver;

    private final MyBinder mBinder = new MyBinder();

    private IAidlInterface mAidlInterfaceImpl;
    private final InterapplicationServiceConnection mServiceConnection = new InterapplicationServiceConnection();

    //    对于不同注册方式的广播接收器回调OnReceive(Context context，Intent intent)中的context返回值是不一样的：
//    对于静态注册（全局+应用内广播），回调onReceive(context, intent)中的context返回值是：ReceiverRestrictedContext；
//    对于全局广播的动态注册，回调onReceive(context, intent)中的context返回值是：Activity Context；
//    对于应用内广播的动态注册（LocalBroadcastManager方式），回调onReceive(context, intent)中的context返回值是：Application Context。
//    对于应用内广播的动态注册（非LocalBroadcastManager方式），回调onReceive(context, intent)中的context返回值是：Activity Context；
    public class ApplicationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLogUtils.file(TAG, "ApplicationReceiver: " + intent.getAction());
            // abortBroadcast();//截断广播
            intent = new Intent(context, RippleActivity.class);
            // 此处必须设置flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //startDynamic();
            //startIssueRe();
        }
    }

    private void startDynamic() {
        Intent intent = new Intent(this, DynamicActivity.class);
        // 此处必须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startIssueRe() {
        Intent intent = new Intent(this, IssueReproActivity.class);
        // 此处必须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendBro() {
        //发送全局标准广播
        Intent intent = new Intent(AppConstants.APPLICATION_BROADCAST_ACTION);
        sendBroadcast(intent);
    }

    private void sendOrBro() {
        //发送全局有序广播
        Intent intent = new Intent(AppConstants.APPLICATION_BROADCAST_ACTION);
        sendOrderedBroadcast(intent, null);
    }

    private class InterapplicationServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAidlInterfaceImpl = IAidlInterface.Stub.asInterface(service);
            MyLogUtils.file(TAG, "成功建立连接: " + name);
//            testValue();
            initInterApp();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLogUtils.file(TAG, "失去连接: " + name);
            mAidlInterfaceImpl = null;
        }

    }

    private void initInterApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mAidlInterfaceImpl != null) {
                    try {
                        MyLogUtils.file(TAG, "获取列表：" + mAidlInterfaceImpl.getPersonList());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    MyLogUtils.file(TAG, "mAidlInterfaceImpl is null ");
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyLogUtils.file(TAG, "onBind ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MyLogUtils.file(TAG, "onUnbind ");
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public void doSomething() {
            MyLogUtils.file(TAG, "MyBinder : doSomething()");
        }
    }


//    fun createNotifyChannel(context: Context): String? {
//        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/raw/" + R.raw.sound)
//        // NotificationChannels are required for Notifications on O (API 26) and above.
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            val audioAttributes = AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .build()
//
//            // Initializes NotificationChannel.
//            val notificationChannel = NotificationChannel("huawei", "Sample Social", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationChannel.enableVibration(true) //是否有震动
//            notificationChannel.lockscreenVisibility = notifPublicVisible()
//
//            notificationChannel.setSound(sound, audioAttributes)
//            // Adds NotificationChannel to system. Attempting to create an existing notification
//            // channel with its original values performs no operation, so it's safe to perform the
//            // below sequence.
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(notificationChannel)
//            "huawei"
//        } else {
//           val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/" + R.raw.sound)
//           NotificationCompat.Builder(context, channelId)
//           .setSound(sound)
//           .build()
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        String ID = "com.freelycar.plugin";    //这里的id里面输入自己的项目的包的路径
        String NAME = "download_name";
        int NOTIFICATION_ID = 1;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification; //创建服务对象
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(this, "chat").setChannelId(ID);
        } else {
            notification = new NotificationCompat.Builder(this, "chat");
        }
        notification
                .setContentTitle("demo")
                //  .setContentText("freelyCarPlugin")
                .setTicker("test")
                .setSmallIcon(R.drawable.ic_notification)
                //.setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS)
                //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getContext().getPackageName() + "/" + R.raw.train))
                // .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .build();
        playNotificationSound();
        Notification notification1 = notification.build();
        startForeground(NOTIFICATION_ID, notification1);
        manager.notify(1, notification1);
//        mHandler = new MyHandler(Looper.getMainLooper(), new WeakReference<FreeDemoService>(this));
        mbReceiver = new ApplicationReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.APPLICATION_BROADCAST_ACTION);
        //      优先级设置
        //intentFilter.setPriority(1000);
        registerReceiver(mbReceiver, intentFilter);

        //在 Service 生命周期方法 onCreate() 中初始化 mHandler
        myFreeHandler = new Handler(Looper.getMainLooper());
        //在子线程中想要 Toast 的地方添加如下
//        myFreeHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                //justShowDialog();
//                showDialog();
//                //testDialog();
//            }
//        });
        //开启严格模式
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        whyTTS = MediaTTSManager.getInstance(this, mHandler);
//        helper = new startTimerUtils(this, mHandler);
//        mHandler.sendMessage(mHandler.obtainMessage(100));
        MyLogUtils.file(TAG, "onCreate ");
    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/train");
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            MyLogUtils.file(TAG,"playNotificationSound: fail");
            e.printStackTrace();
        }
    }

    private void showDialog() {
        Intent intent = new Intent(this, DialogActivity.class);
        // 此处必须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void testDialog() {
        new RxDialogAcfunVideoLoading(this).show();
    }

    private void showImage() {
        RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(this);
        rxDialogScaleView.setImage("squirrel.jpg", true);
        rxDialogScaleView.show();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        MyLogUtils.file(TAG, "onStartCommand ");
        //跨应用通信
        //final Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.freelycar.plugin", "com.freelycar.plugin.service.InterapplicationService"));
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int i) {
        super.onStart(intent, i);
        MyLogUtils.file(TAG, "onStart ");
    }

    private synchronized void mySleep(long t) {
        try {
            wait(t);
        } catch (InterruptedException e) {
            MyLogUtils.file(TAG, "wait error " + e);
        }
    }

    class MyHandler extends Handler {

        WeakReference<FreeDemoService> serviceWeakReference;

        public MyHandler(@NonNull Looper looper, WeakReference<FreeDemoService> serviceWeakReference) {
            super(looper);
            this.serviceWeakReference = serviceWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                //
            }
            MyLogUtils.file(TAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                case 100:
                    //mySleep(3000);
                    break;
                case 200:

                    break;
                case 300:

                    break;
                case 400:

                    break;
            }
        }
    }

    private void testValue() {
        if (mAidlInterfaceImpl != null) {
            if (mAidlInterfaceImpl != null) {
                final Person person = new Person("用户" + new Random().nextInt(10000), new Random().nextInt(100));
                MyLogUtils.file(TAG, "开始添加：" + person);
                try {
                    mAidlInterfaceImpl.addPerson(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }


            if (mAidlInterfaceImpl != null) {
                try {
                    MyLogUtils.file(TAG, "获取列表：" + mAidlInterfaceImpl.getPersonList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (mAidlInterfaceImpl != null) {
                final Person person = new Person("用户" + new Random().nextInt(10000), new Random().nextInt(100));
                MyLogUtils.file(TAG, "客户端发送：" + person);
                try {
                    mAidlInterfaceImpl.setInPerson(person);
                    MyLogUtils.file(TAG, "客户端查看：" + person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (mAidlInterfaceImpl != null) {
                final Person person = new Person("用户" + new Random().nextInt(10000), new Random().nextInt(100));
                MyLogUtils.file(TAG, "客户端发送：" + person);
                try {
                    mAidlInterfaceImpl.setOutPerson(person);
                    MyLogUtils.file(TAG, "客户端查看：" + person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (mAidlInterfaceImpl != null) {
                final Person person = new Person("用户" + new Random().nextInt(10000), new Random().nextInt(100));
                MyLogUtils.file(TAG, "客户端发送：" + person);
                try {
                    mAidlInterfaceImpl.setInOutPerson(person);
                    MyLogUtils.file(TAG, "客户端查看：" + person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            MyLogUtils.file(TAG, "mAidlInterfaceImpl is null：");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogUtils.file(TAG, " onDestroy ");
        //whyTTS.release();
        //mHandler.removeCallbacksAndMessages(null);

        //停止音乐播放服务
        //stopService(new Intent(this, MusicPlayService.class));
        //注销广播接收器
        unbindService(mServiceConnection);
        sendRestartBroadcast();
        //取消注册 注销
        unregisterReceiver(mbReceiver);
    }

    private void sendRestartBroadcast() {
        //创建意图
        Intent intent = new Intent();
        //设置广播频道
        intent.setAction(AppConstants.RESTART_BROADCAST_ACTION);
        //按意图发送广播
        sendBroadcast(intent);
    }
}
