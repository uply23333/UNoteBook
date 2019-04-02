package com.uply.notebook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yangtianrui on 16-5-21.
 */
public class DBHelper extends SQLiteOpenHelper {
    public final static String DB_NAME = "UPLY_NOTE_BOOK";
    public final static String T_NOTES = "notes";
    public final static int DB_VERSION = 1;
    private static final String CREATE_TABLE  = "CREATE TABLE " + T_NOTES + " (_id integer PRIMARY KEY AUTOINCREMENT" +
            ",title text, content text,create_time text, is_sync text DEFAULT 'false' NOT NULL)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_NOTES);
        onCreate(db);
    }
}
