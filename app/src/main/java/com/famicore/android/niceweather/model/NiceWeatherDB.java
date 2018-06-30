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
    // 加锁方法
    public synchronized static NiceWeatherDB getInstance(Context context){
        if(sNiceWeatherDB == null){
            sNiceWeatherDB = new NiceWeatherDB(context);
        }
        return sNiceWeatherDB;
    }

    public void saveProvince(Province province){
        ContentValues values = new ContentValues();
        values.put("province_name",province.getProvinceName());
        values.put("province_code",province.getProvinceCode());
        mDb.insert("Province",null,values);
    }
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        Cursor cursor = mDb.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCity(City city){
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("city_code", city.getCityCode());
        values.put("province_id",city.getProvinceId());
        mDb.insert("City",null, values);
    }
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();
        Cursor cursor = mDb.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("province_id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCounty(County county){
        ContentValues values = new ContentValues();
        values.put("county_name",county.getCountyName());
        values.put("county_code", county.getCountyCode());
        values.put("city_id",county.getCityId());
        mDb.insert("County",null,values);
    }
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<>();
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
