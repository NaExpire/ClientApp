package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperCurrentOrder extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="currentOrders.db";
    private static final int SCHEMA=2;
    static final String TABLE="currentOrders";
    static final String ID="id";
    static final String NAME="name";
    static final String RESTAURANT="restaurant";
    static final String ADDRESS="address"; //3
    static final String PHONE="phone";
    static final String PRICE="price";
    static final String QUANTITY="quantity";
    static final String IMAGE="image";
    static final String TIME="time"; //8

    public DatabaseHelperCurrentOrder(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE+" ("+ID+" TEXT, "+NAME+" TEXT, "+RESTAURANT+" TEXT,"+
                ADDRESS+" TEXT, "+PHONE+" TEXT, "+PRICE+" TEXT, "+QUANTITY+" TEXT, "+
                IMAGE+" TEXT, "+TIME+" TEXT);");
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

