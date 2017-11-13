package com.example.michael.myapplication;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.example.michael.myapplication.services.ChildService;
import com.example.michael.myapplication.services.ParentService;

import timber.log.Timber;

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
