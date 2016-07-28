package com.zgh.locationdemo;

import android.app.Application;

import com.zgh.locationlib.LocationUtil;

/**
 * Created by zhuguohui on 2016/7/28.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocationUtil.init(this);
    }
}
