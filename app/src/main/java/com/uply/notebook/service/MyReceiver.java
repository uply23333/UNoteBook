package com.uply.notebook.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.uply.notebook.R;
import com.uply.notebook.activity.MainActivity;

/**
 * @Auther: Uply
 * @Date: 2019/4/22 01:15
 * @Description:
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

        Notification.Builder builder3 = new Notification.Builder(context);
        builder3.setContentIntent(pendingIntent);
        builder3.setSmallIcon(R.mipmap.ic_launcher);
        builder3.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder3.setContentTitle("悬挂通知");

        Intent XuanIntent = new Intent();
        XuanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        XuanIntent.setClass(context, MainActivity.class);

        PendingIntent xuanpengdIntent = PendingIntent.getActivity(context, 0, XuanIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder3.setFullScreenIntent(xuanpengdIntent, true);
        manager.notify(2, builder3.build());
    }
}
