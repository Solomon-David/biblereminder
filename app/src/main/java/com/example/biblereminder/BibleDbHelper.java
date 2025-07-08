// BibleDbHelper.java
package com.example.biblereminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BibleDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bible_readings.db";
    private static final int DB_VERSION = 1;

    public BibleDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE readings ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "date TEXT,"
                + "book TEXT,"
                + "chapter TEXT,"
                + "start_verse TEXT,"
                + "end_verse TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS readings");
        onCreate(db);
    }
}
