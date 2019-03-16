package com.example.yangtianrui.notebook.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by yangtianrui on 16-5-23.
 * 提供数据,共loader更新数据
 */
public class NoteProvider extends ContentProvider {

    private NoteDao mNoteDao;

    @Override
    public boolean onCreate() {
        mNoteDao = new NoteDao(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Log.v("LOG","NoteProvider -- query()");
        Cursor c = mNoteDao.queryNote(selection, selectionArgs);
//        Log.v("LOG", "id=" + c.getInt(1) + " content=" + c.getString(2));
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mNoteDao.insertNote(values);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mNoteDao.deleteNote(selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mNoteDao.updateNote(values, selection, selectionArgs);
    }
}
