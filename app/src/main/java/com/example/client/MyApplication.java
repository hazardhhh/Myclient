package com.example.client;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static MyApplication application;
    protected static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = this;
    }

    /**
     *  获取上下文
     *
     *  @return
     */
    public static Context getContext(){
        return application;
    }

}
