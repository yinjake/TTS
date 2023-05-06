package com.freelycar.demo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import com.freelycar.demo.R;
import com.freelycar.demo.MyApp;
import com.freelycar.demo.service.FreeDemoService;
import com.freelycar.demo.service.LogService;
import com.freelycar.demo.util.KeepLiveManager;
import com.freelycar.demo.util.MyLogUtils;
import java.lang.ref.WeakReference;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

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
            MyApp.getContext().startForegroundService(new Intent(MyApp.getContext(), FreeDemoService.class));
        } else {
            MyApp.getContext().startService(new Intent(MyApp.getContext(), FreeDemoService.class));
        }
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyLogUtils.file(TAG, "onServiceConnected()");
            if (iBinder == null) return;
            FreeDemoService.MyBinder mBinder = (FreeDemoService.MyBinder) iBinder;
            mBinder.doSomething();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            MyLogUtils.file(TAG, "onServiceDisconnected()");
        }
    };



    static class MyHandler extends Handler {

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

                    break;
                case 200:

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
        //stopService(new Intent(MainActivity.this, MusicPlayService.class));
       //stopService(new Intent(MainActivity.this, FreeDemoService.class));
        KeepLiveManager.getInstance().unregisterKeepLiveReceiver(this);
        //unbindService(connection);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spUpStyle:

                break;
            case R.id.spDownStyle:

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
