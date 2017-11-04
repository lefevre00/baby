package com.example.michael.myapplication.services

import android.app.Service
import android.content.Context
import android.net.nsd.NsdManager
import android.support.v4.content.LocalBroadcastManager

abstract class GenericService : Service() {

    companion object {
        val SERVICE_TYPE = "_babyphone._tcp"
    }

    protected lateinit var mNsdManager: NsdManager
    protected lateinit var broadcastManager : LocalBroadcastManager


    override fun onCreate() {
        super.onCreate()
        mNsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        broadcastManager = LocalBroadcastManager.getInstance(this)
    }

    abstract fun isRunning(): Boolean
    abstract fun stop()
    abstract fun start()
    abstract fun getStartAction(): String
    abstract fun getStopAction(): String
    abstract fun getName(): String
}