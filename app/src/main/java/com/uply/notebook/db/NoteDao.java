package com.uply.notebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 关于notes数据的CRUD
 */
public class NoteDao {
    private DBHelper mHelper;

    public NoteDao(Context context) {
        mHelper = new DBHelper(context);
    }

    public long insertNote(ContentValues contentValues) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.insert(DBHelper.T_NOTES, null, contentValues);
        db.close();
        return id;
    }

    public int updateNote(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count;
        count = db.update(DBHelper.T_NOTES, values, whereClause, whereArgs);
        db.close();
        return count;
    }

    public int deleteNote(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count = db.delete(DBHelper.T_NOTES, whereClause, whereArgs);
        return count;
    }

    public Cursor queryNote(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = db.query(false, DBHelper.T_NOTES, null, selection,
                selectionArgs, null, null, null, null);
        return c;
    }
}




