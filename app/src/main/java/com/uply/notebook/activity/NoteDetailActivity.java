package com.uply.notebook.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uply.notebook.R;
import com.uply.notebook.bean.Note;
import com.uply.notebook.db.NoteDao;
import com.uply.notebook.util.TextFormatUtil;
import com.uply.notebook.widget.LineEditText;

import java.util.Date;


public class NoteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String NOTE_ID = "note_id";
    private EditText mEtTitle;
    private LineEditText mEtContent;
    private Button mBtnModify;
    private Button mBtnDelete;
    private Toolbar mToolbar;
    private NoteDao mNoteDao;
    private Cursor mCursor;
    private Note mNote;
    private int mNoteID = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        mToolbar = findViewById(R.id.id_toolbar_detail);
        mToolbar.setTitle(R.string.NoteDetail);
        // 显示返回按钮
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // 监听Back键,必须放在设置back键后面
        mToolbar.setNavigationOnClickListener(this);
        initData();
        initView();
    }

    private void initData() {
        mNote = new Note("", "", null);
        Intent intent = getIntent();
        mNoteID = intent.getIntExtra(NOTE_ID, -1);
        // 如果有ID参数,从数据库中获取信息
        mNoteDao = new NoteDao(this);
        if (mNoteID != -1) {
            // 进行查询必须使用?匹配参数
            mCursor = mNoteDao.queryNote("_id=?", new String[]{mNoteID + ""});
            if (mCursor.moveToNext()) {
                mNote.setTitle(mCursor.getString(mCursor.getColumnIndex("title")));
                mNote.setContent(mCursor.getString(mCursor.getColumnIndex("content")));
                mNote.setCreateTime(mCursor.getString(mCursor.getColumnIndex("create_time")));
                mNote.setIsSync(mCursor.getString(mCursor.getColumnIndex("is_sync")));
            }
        } else {
            String content = intent.getStringExtra("SPEECH_CONTENT");
            if (content != null) {
                mNote.setContent(content);
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
        mBtnModify = findViewById(R.id.id_btn_modify);
        mBtnDelete = findViewById(R.id.id_btn_delete);
        mEtTitle.setText(mNote.getTitle());
        mEtContent.setText(mNote.getContent());
        mBtnModify.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        if (mNoteID != -1) {
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
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("content", content);
            values.put("create_time", TextFormatUtil.formatDate(new Date()));
            values.put("is_sync", mNote.getIsSync());
            int rowID;
            // 向数据库添加或者更新已有记录
            if (mNoteID == -1) {
                rowID = (int) mNoteDao.insertNote(values);
            } else {
                rowID = mNoteDao.updateNote(values, "_id=?", new String[]{mNoteID + ""});
            }
            if (rowID != -1) {
                if (mNoteID == -1) {
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                }
                getContentResolver().notifyChange(Uri.parse("content://com.terry.NoteBook"), null);
                finish();
            }
        } else if (v.getId() == R.id.id_btn_delete){
            int result;
            if (mNoteID != -1) {
                result = mNoteDao.deleteNote("_id=?", new String[]{mNoteID + ""});
                if (result != -1) {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    getContentResolver().notifyChange(Uri.parse("content://com.terry.Calendar"), null);
                }
            }
            finish();
        } else {
            onBackPressed();
        }
    }
}
