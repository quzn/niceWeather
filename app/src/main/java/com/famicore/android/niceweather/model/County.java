package com.famicore.android.niceweather.model;

public class County {
    private int mId;
    private String mCountyName;
    private String mCountyCode;
    private int mCityId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCountyName() {
        return mCountyName;
    }

    public void setCountyName(String countyName) {
        mCountyName = countyName;
    }

    public String getCountyCode() {
        return mCountyCode;
    }

    public void setCountyCode(String countyCode) {
        mCountyCode = countyCode;
    }

    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        mCityId = cityId;
    }
}
