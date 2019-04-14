package com.uply.notebook.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ldf.calendar.Utils;
import com.uply.notebook.R;
import com.uply.notebook.activity.NoteDetailActivity;
import com.uply.notebook.adapter.CalendarNoteAdapter;
import com.uply.notebook.db.NoteDao;
import com.uply.notebook.widget.CustomDayView;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;

import java.util.ArrayList;
import java.util.HashMap;


public class MyCalendarFragment extends Fragment {

    private static final String TAG = "MyCalendarFragment";

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
    private OnSelectDateListener onSelectDateListener;
    private CalendarViewAdapter calendarAdapter;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private CalendarDate currentDate;
    private boolean initiated = false;
    private NoteDao mNoteDao;
    private Cursor mCursor;

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
        mNoteDao = new NoteDao(getActivity());
        // 查询所有行
        mCursor = mNoteDao.queryNote(null, null);
        rvToDoList.setAdapter(new CalendarNoteAdapter(context, mCursor));
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
        Log.d(TAG, "initMarkData: ");
        HashMap markData = new HashMap<>();
        //1表示红点，0表示灰点
        markData.put("2019-4-16", "1");
        markData.put("2019-4-15", "0");
        calendarAdapter.setMarkData(markData);
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
    }
}
