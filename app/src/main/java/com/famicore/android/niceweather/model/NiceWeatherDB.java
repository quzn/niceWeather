package com.famicore.android.niceweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.famicore.android.niceweather.db.NiceWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NiceWeatherDB {
    public static final String DB_NAME = "nice_weather";
    public static final int VERSION = 1;
    private static NiceWeatherDB sNiceWeatherDB;
    private SQLiteDatabase mDb;
    // 构造方法私有化
    private NiceWeatherDB (Context context){
        NiceWeatherOpenHelper dbHelper = new NiceWeatherOpenHelper(context,DB_NAME,null,VERSION);
        mDb = dbHelper.getWritableDatabase();
    }

    public synchronized static NiceWeatherDB getInstance(Context context){
        if(sNiceWeatherDB == null){
            sNiceWeatherDB = new NiceWeatherDB(context);
        }
        return sNiceWeatherDB;
    }

    public void saveCity(City city){
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("city_code", city.getCityCode());
        mDb.insert("City",null, values);
    }
    public void saveCounty(County county){
        ContentValues values = new ContentValues();
        values.put("county_name",county.getCountyName());
        values.put("county_code", county.getCountyCode());
        values.put("city_id",county.getCityId());
        mDb.insert("County",null,values);
    }
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = mDb.query("County",null,"city_id = ?",new String[] {String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
