package com.famicore.android.niceweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.famicore.android.niceweather.R;
import com.famicore.android.niceweather.model.City;
import com.famicore.android.niceweather.model.County;
import com.famicore.android.niceweather.model.NiceWeatherDB;
import com.famicore.android.niceweather.model.Province;
import com.famicore.android.niceweather.util.HttpCallbackListener;
import com.famicore.android.niceweather.util.HttpUtil;
import com.famicore.android.niceweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private NiceWeatherDB mNiceWeatherDB;
    private List<String> mDataList = new ArrayList<>();

    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;

    private Province mSelectedProvince;
    private City mSelectedCity;
    private int mCurrentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (!getIntent().getBooleanExtra("fromWeatherActivity",false)){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean("citySelected",false)){
                Intent intent = new Intent(this,WeatherActivity.class);
                // 非点击的直接跳转
                startActivity(intent);
                // 结束当前方法并关闭当前活动
                finish();
                return;
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        mTitleText = findViewById(R.id.title_text);
        mListView = findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mDataList);
        mListView.setAdapter(mAdapter);
        mNiceWeatherDB = NiceWeatherDB.getInstance(this);

        queryProvinces();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCurrentLevel == LEVEL_PROVINCE){
                    mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                }else if (mCurrentLevel == LEVEL_CITY){
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                }else if (mCurrentLevel == LEVEL_COUNTY){
                    String countyCode = mCountyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("countyCode",countyCode);
                    // ItemClick（）点击触发的跳转
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    public void queryProvinces(){
        mProvinceList = mNiceWeatherDB.loadProvinces();
        if(mProvinceList.size() > 0 ){
            mDataList.clear();
            for(Province province : mProvinceList){
                mDataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText("中国");
            mCurrentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }
    public void queryCities(){
        mCityList = mNiceWeatherDB.loadCities(mSelectedProvince.getId());
        if(mCityList.size() > 0){
            mDataList.clear();
            for (City city : mCityList){
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(mSelectedProvince.getProvinceName());
            mCurrentLevel = LEVEL_CITY;
        }else{
            queryFromServer(mSelectedProvince.getProvinceCode(),"city");
        }
    }
    public void queryCounties(){
        mCountyList = mNiceWeatherDB.loadCounties(mSelectedCity.getId());
        if (mCountyList.size() > 0){
            mDataList.clear();
            for (County county : mCountyList){
                mDataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(mSelectedCity.getCityName());
            mCurrentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(mSelectedCity.getCityCode(),"county");
        }
    }

    public void queryFromServer(final String code,final String type){
        String address;
        if (TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        setProgressDialog("show");
        HttpUtil.SendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(mNiceWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(mNiceWeatherDB,response,mSelectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(mNiceWeatherDB,response,mSelectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setProgressDialog("close");
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressDialog("close");
                        Toast.makeText(ChooseAreaActivity.this,"加载失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }
         });
    }
    private void setProgressDialog(String arg){
        switch (arg){
            case "show":
                if (mProgressDialog == null){
                    mProgressDialog = new ProgressDialog(this);
                    mProgressDialog.setMessage("正在加载......");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                }
                mProgressDialog.show();
                break;
            case "close":
                if (mProgressDialog != null){
                    mProgressDialog.dismiss();
                }
                break;
        }
    }

    @Override
    public void onBackPressed(){
        switch (mCurrentLevel){
            case LEVEL_COUNTY :
                queryCities();
                break;
            case LEVEL_CITY :
                queryProvinces();
                break;
            default:
                if (getIntent().getBooleanExtra("fromWeatherActivity",false)){
                    Intent intent = new Intent(this,WeatherActivity.class);
                    startActivity(intent);
                }
                finish();
        }
    }
}
