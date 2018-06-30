package com.famicore.android.niceweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.famicore.android.niceweather.R;
import com.famicore.android.niceweather.service.AutoUpdateService;
import com.famicore.android.niceweather.util.HttpCallbackListener;
import com.famicore.android.niceweather.util.HttpUtil;
import com.famicore.android.niceweather.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout mWeatherInfoLayout;
    private TextView mCityNameText;
    private TextView mPublishTimeText;
    private TextView mCurrentDateText;
    private TextView mWeatherDespText;
    private TextView mTemp1Text;
    private TextView mTemp2Text;
    private Button mSwitchButton;
    private Button mRefreshButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        // 设置Layout的目的在于可以控制该组件的可见性
        mWeatherInfoLayout = findViewById(R.id.weather_info_layout);
        mCityNameText = findViewById(R.id.city_name);
        mPublishTimeText = findViewById(R.id.publishTime_text);
        mCurrentDateText = findViewById(R.id.current_date);
        mWeatherDespText = findViewById(R.id.weather_desp);
        mTemp1Text = findViewById(R.id.temp1);
        mTemp2Text = findViewById(R.id.temp2);
        mSwitchButton = findViewById(R.id.switch_city);
        mRefreshButton = findViewById(R.id.refresh_weather);

        mSwitchButton.setText("切换城市");
        mRefreshButton.setText("刷新数据");

        String countyCode = getIntent().getStringExtra("countyCode");

        if (TextUtils.isEmpty(countyCode)){
            showWeather();
        }else {
            // 如果天气信息界面是从区县界面点击转换而来，在数据加载前设置相关界面不可见
            mPublishTimeText.setText("同步中......");
            mCityNameText.setVisibility(View.INVISIBLE);
            mWeatherInfoLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }

        mSwitchButton.setOnClickListener(this);
        mRefreshButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.switch_city :
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("fromWeatherActivity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather :
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = preferences.getString("weatherCode","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    private void queryWeatherCode(String countyCode){
        Log.e("WeatherActivity",countyCode);
        String urlAddress = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(urlAddress,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
        String urlAddress = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(urlAddress,"weatherCode");
    }
    private void queryFromServer(final String urlAddress,final String queryType){
        HttpUtil.SendHttpRequest(urlAddress, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(queryType)){
                    if (!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if (array.length == 2){
                            queryWeatherInfo(array[1]);
                        }
                    }
                }else if ("weatherCode".equals(queryType)){
                    // 匿名内部类中调用外部类方法传递的context时，需要注明“外部类名.this”
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    // 当前对象时运行在由HttpUtil.SendHttpRequest()开启的子线程上，更新UI需转入主线程处理
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPublishTimeText.setText("同步失败！");
                    }
                });
            }
        });
    }
    private void showWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCityNameText.setText(preferences.getString("cityName",""));
        mPublishTimeText.setText(preferences.getString("publishTime",""));
        mCurrentDateText.setText(preferences.getString("currentDate",""));
        mTemp1Text.setText(preferences.getString("temp1",""));
        mTemp2Text.setText(preferences.getString("temp2",""));
        mWeatherDespText.setText(preferences.getString("weatherDesp",""));
        mCityNameText.setVisibility(View.VISIBLE);
        mWeatherInfoLayout.setVisibility(View.VISIBLE);
        // 开启服务
        Intent intent = new Intent(this,AutoUpdateService.class);
        startService(intent);
    }
}
