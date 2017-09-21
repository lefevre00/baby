package com.example.michael.myapplication.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import com.example.michael.myapplication.baby.BabyPhone
import timber.log.Timber

class ParentService : IntentService("ParentService") {

    companion object {
        val START = "START"
        val STOP = "STOP"
        val LISTEN = "LISTEN"

        fun start(context : Context) { send(context, START) }
        fun stop(context : Context) { send(context, STOP) }
        fun listen(context : Context) { send(context, LISTEN) }

        private fun send(context: Context, action: String) {
            val intent = Intent(context, ParentService::class.java)
            intent.action = action
            context.startService(intent)
        }
    }

    private lateinit var mNsdManager: NsdManager
    private var mDiscoveryListener: NsdManager.DiscoveryListener? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("Parent service started")
        mNsdManager = applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            START  -> onParentStart()
            LISTEN -> onListen()
            STOP -> onParentStop()
            else -> Timber.d("Received message ${intent?.action}")
        }
    }

    private fun onParentStart() {
        Timber.d("On Parent service started")
    }

    private fun onListen() {
        mDiscoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Timber.d("Start discovery failed [%s]", serviceType)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Timber.d("Stop discovery failed [%s]", serviceType)
            }

            override fun onDiscoveryStarted(serviceType: String) {
                Timber.d("Discovery started [%s]", serviceType)
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Timber.d("Discovery stopped [%s]", serviceType)
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Timber.d("Found service %s", serviceInfo.serviceType)
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Timber.d("Service lost [%s]", serviceInfo.serviceType)
            }
        }
        mNsdManager.discoverServices(BabyPhone.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener)
    }

    private fun onParentStop() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener)
        mDiscoveryListener = null
        stopSelf()
    }
}