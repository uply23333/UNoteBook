package com.example.yangtianrui.notebook.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.yangtianrui.notebook.bean.Note;
import com.example.yangtianrui.notebook.config.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by yangtianrui on 16-6-14.
 * <p/>
 * 自动向Bmob后台更新数据
 */
public class AutoSyncService extends Service {

    private List<BmobObject> mNotes = new ArrayList<>();
    private Timer mTimer = new Timer();
    private Uri mUri = Uri.parse("content://com.terry.NoteBook");
    private ContentResolver mResolver;

    public static final String SEND_SYNC_STATE = "STATE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 执行定时任务
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mResolver = getContentResolver();
        TimerTask task = new SyncTask();
        mTimer.schedule(task, 5000, 5 * 60 * 1000); // 五分钟更新一次
//        mTimer.schedule(task, 30000);
    }


    class SyncTask extends TimerTask {

        @Override
        public void run() {
            mNotes.clear();
            Cursor cursor = mResolver.query(mUri, null
                    , "is_sync = ?", new String[]{"false"}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Note note = new Note();
                    int noteID = cursor.getInt(0);
                    note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    note.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    note.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                    note.setUserName(BmobUser.getCurrentUser(BmobUser.class).getUsername());
                    mNotes.add(note);
                    // 标记为已同步
                    ContentValues values = new ContentValues();
                    values.put("is_sync", "true");
                    mResolver.update(mUri, values, "_id=?", new String[]{noteID + ""});
                }
                cursor.close();
                // 向服务器发送数据
                new BmobBatch().insertBatch(mNotes).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                        if (e == null) {
                            int count = 0;
                            for (int i = 0; i < list.size(); i++) {
                                BatchResult result = list.get(i);
                                BmobException ex = result.getError();
                                if (ex != null) {
                                    count++;
                                }
                            }
                            Intent intent = new Intent();
                            intent.setAction(Constants.SYNC_BROADCAST_ACTION);
                            intent.putExtra(SEND_SYNC_STATE, "更新成功" + count + "条");
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(Constants.SYNC_BROADCAST_ACTION);
                            intent.putExtra(SEND_SYNC_STATE, "失败：" + e.getMessage() + "," + e.getErrorCode());
                            sendBroadcast(intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
