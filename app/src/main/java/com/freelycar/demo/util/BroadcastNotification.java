package com.freelycar.demo.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.freelycar.demo.MyApp;
import com.freelycar.demo.R;
import com.freelycar.demo.activity.MainActivity;

public class BroadcastNotification extends BroadcastReceiver {
    private void showNotification() {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager) MyApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.ic_notification, "新消息", System.currentTimeMillis());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApp.getContext()," ");
        builder.setSmallIcon(R.drawable.next_icon);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // 设置通知的事件消息
        CharSequence contentTitle = "Title"; // 通知栏标题
        CharSequence contentText = "Text"; // 通知栏内容
        //Intent clickIntent = new Intent(this, NotificationClickReceiver.class); //点击通知之后要发送的广播
        int id = (int) (System.currentTimeMillis() / 1000);
        //PendingIntent contentIntent = PendingIntent.getBroadcast(MyApp.getContext(), id, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       // notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
        notificationManager.notify(id, notification);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //todo 跳转之前要处理的逻辑
        Intent newIntent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

}
