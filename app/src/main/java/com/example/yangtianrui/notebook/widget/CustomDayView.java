package com.example.yangtianrui.notebook.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yangtianrui.notebook.R;
import com.ldf.calendar.interf.IDayRenderer;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.DayView;


/**
 * @Auther: Uply
 * @Date: 2019/3/17 09:57
 * @Description:
 */
@SuppressLint("ViewConstructor")
public class CustomDayView extends DayView {


    public CustomDayView(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    @Override
    public void refreshContent() {
        super.refreshContent();
    }

    @Override
    public IDayRenderer copy() {
        return new CustomDayView(context, layoutResource);
    }
}
