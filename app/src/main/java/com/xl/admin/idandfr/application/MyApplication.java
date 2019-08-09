package com.xl.admin.idandfr.application;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by admin on 2018/9/2.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
