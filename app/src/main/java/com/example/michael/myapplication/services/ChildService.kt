package com.example.michael.myapplication.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import timber.log.Timber


class ChildService : Service() {

    companion object {
        val SERVICE_NAME = "ChildService"
    }

    private val mBinder = ChildServiceBinder()
    private var mHandler : Handler? = null
    private lateinit var mNsdManager: NsdManager
    private var mDiscoveryListener: NsdManager.DiscoveryListener? = null

    inner class ChildServiceBinder : Binder() {
        val service: ChildService
            get() = this@ChildService
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("$SERVICE_NAME started")
        mHandler = Handler(Looper.getMainLooper())
        mNsdManager = applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("$SERVICE_NAME destroyed")
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    fun listen() {
        mNsdManager.discoverServices(ParentService.SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD, createDiscoveryListener())
    }

    private fun createDiscoveryListener(): NsdManager.DiscoveryListener? {
        return object : NsdManager.DiscoveryListener {
            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                Timber.d("Found service ${serviceInfo?.serviceName}")
                // Need to resolv service

                mNsdManager.resolveService(serviceInfo, createResolveListener())
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Timber.w("FAILED to stop discovering service $serviceType")
                mNsdManager.stopServiceDiscovery(this)
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Timber.w("FAILED to start discovering service $serviceType ")
                mNsdManager.stopServiceDiscovery(this)
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                Timber.d("Start discovering service $serviceType ")
                mDiscoveryListener = this
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                Timber.d("Stop discovering service $serviceType ")
                mDiscoveryListener = null
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                Timber.w("LOST service ${serviceInfo?.serviceName} ")
            }
        }
    }

    private fun createResolveListener(): NsdManager.ResolveListener? {
      return object : NsdManager.ResolveListener {
          override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
              Timber.w("FAILED to resolve ${serviceInfo?.serviceName} ")
          }

          override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
              Timber.d("Resolved service ${serviceInfo?.serviceName} on ${serviceInfo?.host}:${serviceInfo?.port}")
          }
      }
    }

    fun terminate() {
        if (mDiscoveryListener != null) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener)
        }
    }
}