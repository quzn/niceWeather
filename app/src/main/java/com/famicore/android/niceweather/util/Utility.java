package com.famicore.android.niceweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.famicore.android.niceweather.model.City;
import com.famicore.android.niceweather.model.County;
import com.famicore.android.niceweather.model.NiceWeatherDB;
import com.famicore.android.niceweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {
    public synchronized static boolean handleProvincesResponse(NiceWeatherDB niceWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if ((allProvinces != null)&&(allProvinces.length > 0)){
                for (String provinceInfo : allProvinces){
                    String[] nameCode = provinceInfo.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(nameCode[0]);
                    province.setProvinceName(nameCode[1]);
                    niceWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleCitiesResponse(NiceWeatherDB niceWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if ((allCities != null)&&(allCities.length > 0)){
                for (String cityInfo : allCities){
                    String[] nameCode = cityInfo.split("\\|");
                    City city = new City();
                    city.setCityCode(nameCode[0]);
                    city.setCityName(nameCode[1]);
                    city.setProvinceId(provinceId);
                    niceWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleCountiesResponse(NiceWeatherDB niceWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if ((allCounties != null)&&(allCounties.length > 0)){
                for (String countyInfo : allCounties){
                    String[] nameCode = countyInfo.split("\\|");
                    County county = new County();
                    county.setCountyCode(nameCode[0]);
                    county.setCountyName(nameCode[1]);
                    county.setCityId(cityId);
                    niceWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherObject = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherObject.getString("city");
            String cityCode = weatherObject.getString("cityid");
            String temp1 = weatherObject.getString("temp1");
            String temp2 = weatherObject.getString("temp2");
            String weatherDesp = weatherObject.getString("weather");
            String pulishTime = weatherObject.getString("ptime");
            saveWeatherInfo(context,cityName,cityCode,temp1,temp2,weatherDesp,pulishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String pulishTime){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        // 存入数据需要Editor的帮忙，但可以直接获取数据
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("citySelected",true);
        editor.putString("cityName",cityName);
        editor.putString("weatherCode",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weatherDesp",weatherDesp);
        editor.putString("publishTime",pulishTime);
        editor.putString("currentDate",dateFormat.format(new Date()));
        editor.apply();
    }
}
