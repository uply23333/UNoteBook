package com.uply.notebook.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.uply.notebook.R;
import com.uply.notebook.bean.Calendar;
import com.uply.notebook.db.CalendarDao;
import com.uply.notebook.service.AlarmService;
import com.uply.notebook.service.MyReceiver;
import com.uply.notebook.util.TextFormatUtil;
import com.uply.notebook.widget.LineEditText;

import java.text.ParseException;
import java.util.Date;


public class  CalendarDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CALENDAR_ID = "calendar_id";
    public static final String SPEECH_CONTENT = "SPEECH_CONTENT";
    public static final String TIME_CONTENT = "TIME_CONTENT";
    private EditText mEtTitle;
    private TextView mEtime;
    private LineEditText mEtContent;
    private Button mBtnModify;
    private Toolbar mToolbar;
    private CalendarDao calendarDao;
    private Cursor mCursor;
    private Calendar mCalendar;
    private int mCalendarId = -1;
    private AlarmManager alarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);
        mToolbar = findViewById(R.id.id_toolbar_detail);
        mToolbar.setTitle(R.string.NoteDetail);
        // 显示返回按钮
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // 监听Back键,必须放在设置back键后面
        mToolbar.setNavigationOnClickListener(this);


        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        initData();
        initView();
    }

    private void initData() {
        mCalendar = new Calendar("", "", "");
        Intent intent = getIntent();
        mCalendarId = intent.getIntExtra(CALENDAR_ID, -1);
        // 如果有ID参数,从数据库中获取信息
        calendarDao = new CalendarDao(this);
        if (mCalendarId != -1) {
            // 进行查询必须使用?匹配参数
            mCursor = calendarDao.queryCalendar("_id=?", new String[]{mCalendarId + ""});
            if (mCursor.moveToNext()) {
                mCalendar.setTitle(mCursor.getString(mCursor.getColumnIndex("title")));
                mCalendar.setContent(mCursor.getString(mCursor.getColumnIndex("content")));
                mCalendar.setNotifyTime(mCursor.getString(mCursor.getColumnIndex("notify_time")));
            }
        } else {
            String content = intent.getStringExtra(SPEECH_CONTENT);
            if (content != null) {
                mCalendar.setContent(content);
            }
            String notifyTime = intent.getStringExtra(TIME_CONTENT);
            if (notifyTime != null) {
                mCalendar.setNotifyTime(notifyTime);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
        mEtTitle = findViewById(R.id.id_et_title);
        mEtContent = findViewById(R.id.id_et_content);
        mEtime = findViewById(R.id.id_et_time);
        mBtnModify = findViewById(R.id.id_btn_modify);
        mEtTitle.setText(mCalendar.getTitle());
        mEtime.setText(!mCalendar.getNotifyTime().equals("")? mCalendar.getNotifyTime(): TextFormatUtil.formatDate(new Date()));
        mEtime.setOnClickListener(this);
        mEtContent.setText(mCalendar.getContent());
        mBtnModify.setOnClickListener(this);
        Intent intent = getIntent();
        boolean isUpdate = intent.getBooleanExtra("IS_UPDATE", false);
        if (isUpdate) {
            mBtnModify.setText(R.string.ModifyNote);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_btn_modify) {
            String title = mEtTitle.getText().toString();
            String content = mEtContent.getText().toString();
            String notifyTime = mEtime.getText().toString();
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("content", content);
            values.put("notify_time", notifyTime);
            values.put("is_sync", mCalendar.isSync());
            int rowID;
            // 向数据库添加或者更新已有记录
            if (mCalendarId == -1) {
                rowID = (int) calendarDao.insertCalendar(values);
            } else {
                rowID = calendarDao.updateCalendar(values, "_id=?", new String[]{mCalendarId + ""});
            }
            if (rowID != -1) {
                if (mCalendarId == -1) {
                    try {
                        addAlarm();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                }
                getContentResolver().notifyChange(Uri.parse("content://com.terry.Calendar"), null);
                finish();
            }
        } if (v.getId() == R.id.id_et_time) {
            //时间选择器
                new TimePickerBuilder(CalendarDetailActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        mEtime.setText(TextFormatUtil.formatDate(date));
                    }
                })
                        .isDialog(true)
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .build()
                        .show();
        }else {
            onBackPressed();
        }
    }

    private void addAlarm() throws ParseException {

//        Intent alarmIntent = new Intent(getApplicationContext(), MyReceiver.class);
//        PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        java.util.Calendar calendar= java.util.Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        registerOneTimeAlarm(operation, calendar.getTimeInMillis());
        Intent intent = new Intent(this, AlarmService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("NOTIFICATION");
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        int type = AlarmManager.RTC_WAKEUP;
        //new Date()：表示当前日期，可以根据项目需求替换成所求日期
        //getTime()：日期的该方法同样可以表示从1970年1月1日0点至今所经历的毫秒数
        long triggerAtMillis = TextFormatUtil.parseText(mEtime.getText().toString()).getTime();
        alarmManager.set(type, triggerAtMillis, pi);
    }

    private void registerOneTimeAlarm(PendingIntent alarmIntent, long when) {
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, when, alarmIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, when, alarmIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, alarmIntent);
        }
    }
}
