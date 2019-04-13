package com.uply.notebook.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uply.notebook.R;
import com.uply.notebook.util.TextFormatUtil;
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
        holder.mTvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        holder.mTvContent.setText(TextFormatUtil.getNoteSummary(cursor.getString(cursor.getColumnIndex("content"))));
        holder.mTvCreateTime.setText(cursor.getString(cursor.getColumnIndex("create_time")));
    }

    @Override
    protected void onContentChanged() {

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=inflater.inflate(R.layout.item_note, viewGroup,false);
        return new NoteViewHolder(v);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTitle;
        TextView mTvContent;
        TextView mTvCreateTime;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.id_tv_note_title);
            mTvContent = itemView.findViewById(R.id.id_tv_note_summary);
            mTvCreateTime = itemView.findViewById(R.id.id_tv_note_create_time);
        }
    }
}
