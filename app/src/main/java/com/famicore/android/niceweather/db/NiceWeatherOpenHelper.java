package com.famicore.android.niceweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NiceWeatherOpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement , "
            + "city_name text ,"
            + "city_code text )";

    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement ,"
            + "county_name text ,"
            + "county_code text,"
            + "city_id integer )";
    public NiceWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name , factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
