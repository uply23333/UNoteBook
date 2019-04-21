package com.uply.notebook.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.uply.notebook.service.AlarmService;

/**
 * @Auther: Uply
 * @Date: 2019/4/21 22:14
 * @Description:
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context, AlarmService.class);
        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(service);
    }
}
