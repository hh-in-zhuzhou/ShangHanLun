package me.huanghai.searchController;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    public static Activity activity;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
//        SingletonData.getInstance();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
} 
