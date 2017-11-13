package com.example.michael.myapplication.services

import android.app.Service
import android.content.Context
import android.net.nsd.NsdManager
import android.support.v4.content.LocalBroadcastManager
import timber.log.Timber

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
    abstract fun onStart()
    abstract fun onStop()
    abstract fun getName(): String

    fun start() {
        if (isRunning()) {
            Timber.i("${getName()} is already running")
            return
        }
        onStart()
    }
    fun stop() {
        if (isRunning()) {
            onStop()
        } else {
            Timber.i("${getName()} is not running, can't be stopped")
        }
    }

    fun getStartAction(): String {
        return "${getName()}_STARTED"
    }

    fun getStopAction(): String {
        return "${getName()}_STOPPED"
    }
}