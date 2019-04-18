package com.uply.notebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @Auther: Uply
 * @Date: 2019/4/18 23:56
 * @Description:关于calendar数据的CRUD
 */
public class CalendarDao {
    private DBHelper mHelper;

    public CalendarDao(Context context) {
        mHelper = new DBHelper(context);
    }

    public long insertCalendar(ContentValues contentValues) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.insert(DBHelper.T_CALENDAR, null, contentValues);
        db.close();
        return id;
    }

    public int updateCalendar(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count;
        count = db.update(DBHelper.T_CALENDAR, values, whereClause, whereArgs);
        db.close();
        return count;
    }

    public int deleteCalendar(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count = db.delete(DBHelper.T_CALENDAR, whereClause, whereArgs);
        return count;
    }

    public Cursor queryCalendar(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = db.query(false, DBHelper.T_CALENDAR, null, selection,
                selectionArgs, null, null, null, null);
        return c;
    }
}
