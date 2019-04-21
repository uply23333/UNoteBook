package com.uply.notebook.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @Auther: Uply
 * @Date: 2019/4/22 00:42
 * @Description:
 */
public class RemoteService extends Service {
    public RemoteService() {
    }

    public IBinder onBind(Intent intent) {
        return stub;// 在客户端连接服务端时，Stub通过ServiceConnection传递到客户端
    }

// 实现接口中暴露给客户端的Stub--Stub继承自Binder，它实现了IBinder接口

    private IMyAidlInterface.Stub stub = new IMyAidlInterface.Stub() {
        @Override
        public void sendNotification() throws RemoteException {

        }
    };
}
