package com.uply.notebook.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.uply.notebook.R;
import com.uply.notebook.activity.MainActivity;
import com.uply.notebook.bean.Calendar;

public class AlarmService extends Service {

    private static final int NOTIFICATION_ID = 1000;
    private static final String TAG = "AlarmService";

    public AlarmService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Uply Note Calendar Service")
                .setContentText("日程表后台程序")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent != null && "NOTIFICATION".equals(intent.getAction())) {
            final Calendar calendar = (Calendar)intent.getSerializableExtra("calendar");
            Log.d(TAG, "onStartCommand: title" + calendar.getTitle());
            Log.d(TAG, "onStartCommand: notifytime" + calendar.getNotifyTime());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent intent2 = new Intent(AlarmService.this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(AlarmService.this, 0, intent2, 0);

                    Notification.Builder builder3 = new Notification.Builder(AlarmService.this);
                    builder3.setContentIntent(pendingIntent);
                    builder3.setSmallIcon(R.mipmap.ic_launcher);
                    builder3.setLargeIcon(BitmapFactory.decodeResource(AlarmService.this.getResources(), R.mipmap.ic_launcher));
                    builder3.setContentTitle(calendar.getTitle());

                    Intent XuanIntent = new Intent();
                    XuanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    XuanIntent.setClass(AlarmService.this, MainActivity.class);

                    PendingIntent xuanpengdIntent = PendingIntent.getActivity(AlarmService.this, 0, XuanIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder3.setFullScreenIntent(xuanpengdIntent, true);
                    manager.notify(2, builder3.build());
                }
            }).start();
        }
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        Intent localIntent = new Intent();
        localIntent.setClass(this, AlarmService.class); //销毁时重新启动Service
        this.startService(localIntent);
    }
}
