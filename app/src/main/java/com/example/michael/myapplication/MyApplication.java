package com.example.michael.myapplication;

import android.app.Application;

import com.example.michael.myapplication.services.ChildService;
import com.example.michael.myapplication.services.ParentService;

import timber.log.Timber;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
