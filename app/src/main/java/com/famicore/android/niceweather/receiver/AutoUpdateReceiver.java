package com.famicore.android.niceweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.famicore.android.niceweather.service.AutoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver {
    // 服务启动广播、广播启动服务，如此循环，生生不息...
    @Override
    public void onReceive(Context context, Intent intent){
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
