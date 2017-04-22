package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperDeals extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="deals.db";
    private static final int SCHEMA=2;
    static final String TABLE="deals";
    static final String ID="id";
    static final String NAME="name";
    static final String RESTAURANT="restaurant";
    static final String ADDRESS="address";
    static final String DESCRIPTION="description";
    static final String PRICE="price";
    static final String QUANTITY="quantity";
    static final String IMAGE="image";
    static final String CART_QUANTITY="cartquantity";

    public DatabaseHelperDeals(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE+" ("+ID+" TEXT, "+NAME+" TEXT, "+RESTAURANT+" TEXT, "+
                ADDRESS+" TEXT, "+DESCRIPTION+" TEXT, "+PRICE+" TEXT, "+QUANTITY+" TEXT, "+
                IMAGE+" TEXT, "+CART_QUANTITY+" TEXT);");
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