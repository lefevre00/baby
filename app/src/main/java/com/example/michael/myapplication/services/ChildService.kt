package com.example.michael.myapplication.services

import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import timber.log.Timber


class ChildService : GenericService() {


    companion object {
        val SERVICE_NAME = "Kids"
    }

    private val mBinder = ChildServiceBinder()
    private var mHandler : Handler? = null
    private var mDiscoveryListener: NsdManager.DiscoveryListener? = null

    inner class ChildServiceBinder : Binder() {
        val service: ChildService
            get() = this@ChildService
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("$SERVICE_NAME started")
        mHandler = Handler(Looper.getMainLooper())
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("$SERVICE_NAME destroyed")
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onStart() {
        mNsdManager.discoverServices(GenericService.SERVICE_TYPE,
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
                broadcastManager.sendBroadcast(Intent(getStartAction()))
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                Timber.d("Stop discovering service $serviceType ")
                mDiscoveryListener = null
                broadcastManager.sendBroadcast(Intent(getStopAction()))
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                Timber.w("LOST service ${serviceInfo?.serviceName} ")
            }
        }
    }

    private fun createResolveListener(): NsdManager.ResolveListener? {
      return object : NsdManager.ResolveListener {
          override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
              Timber.w("FAILED to resolve ${serviceInfo?.serviceName}, error $errorCode")
          }

          override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
              Timber.d("Resolved service ${serviceInfo?.serviceName} on ${serviceInfo?.host}:${serviceInfo?.port}")
          }
      }
    }

    override fun isRunning(): Boolean {
        return mDiscoveryListener != null
    }

    override fun onStop() {
        if (mDiscoveryListener != null) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener)
        }
    }

    override fun getName(): String {
        return SERVICE_NAME
    }
}