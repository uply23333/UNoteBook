package com.uply.notebook.fragment;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldf.calendar.Utils;
import com.uply.notebook.R;
import com.uply.notebook.adapter.CalendarNoteAdapter;
import com.uply.notebook.bean.Note;
import com.uply.notebook.db.CalendarDao;
import com.uply.notebook.util.TextFormatUtil;
import com.uply.notebook.widget.CustomDayView;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class MyCalendarFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MyCalendarFragment";
    private static String ADD_CALENDAR_SUCCESS = "ADD_CALENDAR_SUCCESS";

    private View root;
    private TextView tvYear;
    private TextView tvDay;
    private TextView tvMonth;
    private TextView backToday;
    private CoordinatorLayout content;
    private MonthPager monthPager;
    private RecyclerView rvToDoList;
    private TextView nextMonthBtn;
    private TextView lastMonthBtn;

    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarNoteAdapter mAdapter;
    private OnSelectDateListener onSelectDateListener;
    private CalendarViewAdapter calendarAdapter;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private CalendarDate currentDate;
    private boolean initiated = false;
    private CalendarDao mCalendarDao;
    private Cursor mCursor;
    private AlarmManager alarmManager;
    final Uri uri = Uri.parse("content://com.terry.Calendar");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_calendar, container, false);
        context = getContext();
        content = root.findViewById(R.id.content);
        monthPager = root.findViewById(R.id.calendar_view);
        monthPager.setViewHeight(Utils.dpi2px(context, 270));
        backToday = root.findViewById(R.id.back_today_button);
        tvYear = root.findViewById(R.id.show_year_view);
        tvMonth = root.findViewById(R.id.show_month_view);
        tvDay = root.findViewById(R.id.show_day_view);
        nextMonthBtn = root.findViewById(R.id.next_month);
        lastMonthBtn = root.findViewById(R.id.last_month);
        rvToDoList = root.findViewById(R.id.list);
        rvToDoList.setHasFixedSize(true);
        //这里用线性显示 类似于listview
        rvToDoList.setLayoutManager(new LinearLayoutManager(context));
        mCalendarDao = new CalendarDao(getActivity());
        getRemoteData();
        // 查询所有行
        mCursor = mCalendarDao.queryCalendar("notify_time like ?", new String[]{TextFormatUtil.formatDate(new Date()).substring(0, 10) + "%"});
        mAdapter = new CalendarNoteAdapter(context, mCursor);
        rvToDoList.setAdapter(mAdapter);
        alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        initCurrentDate();
        initCalendarView();
        initToolbarClickListener();
        return root;
    }

    /**
     * onWindowFocusChanged回调时，将当前月的种子日期修改为今天
     *
     * @return void
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
//        if (!initiated) {
//            CalendarDate today = new CalendarDate();
//            calendarAdapter.notifyDataChanged(today);
//            initiated = true;
//            initiated = true;
//        }
    }

    /**
     * 此时重启Loader机制更新数据
     */
    @Override
    public void onResume() {
        super.onResume();
        mCursor = mCalendarDao.queryCalendar(null, null);
        mCursor = mCalendarDao.queryCalendar("notify_time like ?",
                new String[]{addZero(currentDate.getYear()) + "-" + addZero(currentDate.getMonth()) + "-" + addZero(currentDate.getDay()) +"%"});
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://com.terry.Calendar");
        return new CursorLoader(getActivity(), uri, null, "notify_time like ?",
                new String[]{addZero(currentDate.getYear()) + "-" + addZero(currentDate.getMonth()) + "-" + addZero(currentDate.getDay()) +"%"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        initMarkData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void onClickBackToDayBtn() {
        refreshMonthPager(new CalendarDate());
    }

    /**
     * 初始化currentDate
     *
     * @return void
     */
    private void initCurrentDate() {
        Log.d(TAG, "initCurrentDate: ");
        currentDate = new CalendarDate();
        tvYear.setText(currentDate.getYear() + "年");
        tvMonth.setText(currentDate.getMonth() + "");
        tvDay.setText(currentDate.getDay() + "");
    }

    private void initCalendarView() {
        Log.d(TAG, "initCalendarView: ");
        initListener();
        CustomDayView customDayView = new CustomDayView(context, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                context,
                onSelectDateListener,
                CalendarAttr.CalendarType.MONTH,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(new CalendarViewAdapter.OnCalendarTypeChanged() {
            @Override
            public void onCalendarTypeChanged(CalendarAttr.CalendarType type) {
                rvToDoList.scrollToPosition(0);
            }
        });
        initMarkData();
        initMonthPager();
    }

    /**
     * 初始化对应功能的listener
     *
     * @return void
     */
    private void initToolbarClickListener() {
        Log.d(TAG, "initToolbarClickListener: ");
        backToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBackToDayBtn();
            }
        });
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
            }
        });
        lastMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initMarkData() {
        Cursor cursor = mCalendarDao.queryCalendar(null, null);
        HashMap markData = new HashMap<>();
        while (cursor.moveToNext()) {
            String [] date = cursor.getString(cursor.getColumnIndex("notify_time")).split(" ");
            if (date.length == 2) {
                markData.put(subZero(date[0]), "1");
            }
        }
        Log.d(TAG, "initMarkData: " + markData.size());
        calendarAdapter.setMarkData(markData);
    }


    private void getRemoteData() {
        Cursor cursor = mCalendarDao.queryCalendar(null, null);
        final Set<com.uply.notebook.bean.Calendar> calendarSet = new HashSet<>();

        while (cursor.moveToNext()) {
            String notifyTime = cursor.getString(cursor.getColumnIndex("notify_time"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            calendarSet.add(new com.uply.notebook.bean.Calendar(title, content, notifyTime));
        }
        BmobQuery<com.uply.notebook.bean.Calendar> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userName", BmobUser.getCurrentUser(BmobUser.class).getUsername());
        bmobQuery.setLimit(50); // 返回50条数据
        // 从服务器获取数据
        bmobQuery.findObjects(new FindListener<com.uply.notebook.bean.Calendar>() {
            @Override
            public void done(List<com.uply.notebook.bean.Calendar> list, BmobException e) {
                if (e == null) {
                    // 获取所有没有在服务器中的数据
                    list.removeAll(calendarSet);
                    ContentResolver resolver = getActivity().getContentResolver();
                    // 将此数据写入数据库中
                    for (com.uply.notebook.bean.Calendar note : list) {
                        ContentValues values = new ContentValues();
                        values.put("title", note.getTitle());
                        values.put("content", note.getContent());
                        values.put("notify_time", note.getNotifyTime());
                        values.put("is_sync", "true");
                        resolver.insert(uri, values);
                    }
                    resolver.notifyChange(uri, null);
                    // 通知UI更新界面
                    Snackbar.make(root, "同步完成", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(root, e.getErrorCode() + "," + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initMonthPager() {
        Log.d(TAG, "initMonthPager: ");
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) != null) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    Log.i(TAG, "onPageSelected: ");
//                    tvYear.setText(date.getYear() + "年");
//                    tvMonth.setText(date.getMonth() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initListener() {
        Log.d(TAG, "initListener: ");
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                //your code
                Log.i(TAG, "onSelectDate: ");
                currentDate = date;
                refreshMonthPager(date);
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示上一个月 ， 1表示下一个月
                monthPager.selectOtherMonth(offset);
            }
        };
    }

    private void refreshSelectBackground() {
        CustomDayView themeDayView = new CustomDayView(context, R.layout.custom_day_focus);
        calendarAdapter.setCustomDayRenderer(themeDayView);
        calendarAdapter.notifyDataSetChanged();
        calendarAdapter.notifyDataChanged(new CalendarDate());
    }

    private void refreshMonthPager(CalendarDate date) {
        Log.d(TAG, "refreshMonthPager: ");
        calendarAdapter.notifyDataChanged(date);
        tvYear.setText(date.getYear() + "年");
        tvMonth.setText(date.getMonth() + "月");
        tvDay.setText(date.getDay() + "日");
        mCursor = mCalendarDao.queryCalendar("notify_time like ?",
                new String[]{addZero(date.getYear()) + "-" + addZero(date.getMonth()) + "-" + addZero(date.getDay()) +"%"});
        mAdapter.swapCursor(mCursor);
    }

    private String addZero(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    private String subZero(String number) {
        String[] arr = number.split("-");
        if (arr.length != 3) {
            return null;
        }
        if (arr[1].charAt(0) == '0') {
            arr[1] = arr[1].substring(1);
        }
        if (arr[2].charAt(0) == '0') {
            arr[2] = arr[2].substring(1);
        }
        return arr[0] + "-" + arr[1] + "-" + arr[2];
    }


//    public class AddCalTaskReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(MyCalendarFragment.ADD_CALENDAR_SUCCESS)) {
//                //更新日历上的标记数据
//                initMarkData();
//            }
//        }
//
//    }
}
