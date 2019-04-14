package com.uply.notebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldf.calendar.model.CalendarDate;
import com.uply.notebook.R;
import com.uply.notebook.activity.NoteDetailActivity;
import com.uply.notebook.widget.CursorAdapter;

/**
 * @Auther: Uply
 * @Date: 2019/4/14 01:14
 * @Description:
 */
public class CalendarNoteAdapter extends CursorAdapter<CalendarNoteAdapter.NoteViewHolder> {

    private LayoutInflater inflater;

    public CalendarNoteAdapter(Context context, Cursor c) {
        super(context, c, 1);
        inflater=LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, Cursor cursor) {
        holder.mClTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        holder._id = cursor.getInt(cursor.getColumnIndex("_id"));
        CalendarDate date = new CalendarDate();
        holder.mClTime.setText(cursor.getString(cursor.getColumnIndex("create_time")));
        holder.mClDate.setText(date.getMonth() + "月" + date.getDay() + "日");
    }

    @Override
    protected void onContentChanged() {

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        final NoteViewHolder viewHolder = new NoteViewHolder(inflater.inflate(R.layout.item_note_calendar, viewGroup,false));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewGroup.getContext(), NoteDetailActivity.class);
                intent.putExtra(NoteDetailActivity.NOTE_ID, viewHolder._id);
                viewGroup.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mClTitle;
        TextView mClTime;
        TextView mClDate;
        int _id;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mClTitle = itemView.findViewById(R.id.id_cl_note_tittle);
            mClTime = itemView.findViewById(R.id.id_cl_note_time);
            mClDate = itemView.findViewById(R.id.id_cl_note_date);
        }
    }
}


// TODO: 2019/4/14 日期绑定时间
// TODO: 2019/4/14 添加通知