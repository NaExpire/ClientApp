package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperPastOrder extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="past.db";
    private static final int SCHEMA=3;
    static final String TABLE="past";
    static final String ID="id";
    static final String ITEMS="items";
    static final String RESTAURANT="restaurant";
    static final String TIME="time";
    static final String PRICE="price";
    static final String QUANTITY="quantity";
    static final String RATING="rating";

    public DatabaseHelperPastOrder(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE+" ("+ID+" TEXT, "+ITEMS+" TEXT, "+RESTAURANT+" TEXT,"+
                TIME+" TEXT, "+PRICE+" TEXT, "+QUANTITY+" TEXT, "+ RATING+" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        // Create a new one.
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion,
                            int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        // Create a new one.
        onCreate(db);
    }
}