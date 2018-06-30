package com.famicore.android.niceweather.util;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
