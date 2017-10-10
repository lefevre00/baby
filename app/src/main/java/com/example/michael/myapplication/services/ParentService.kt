package com.example.michael.myapplication.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import android.os.IBinder
import timber.log.Timber
import java.net.ServerSocket

class ParentService : Service() {

    companion object {
        var SERVICE_NAME = "ParentService"
        val SERVICE_TYPE = "_babyphone._tcp"
    }

    // Binder given to clients
    private val mBinder = ParentServiceBinder()

    inner class ParentServiceBinder : Binder() {
        val service: ParentService
            get() = this@ParentService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private lateinit var nsdManager: NsdManager

    override fun onCreate() {
        super.onCreate()
        Timber.d("ParentService started")
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Service destroyed")
    }

    private var mRegistrationListener: RegistrationListener? = null

    fun register() {
        val serviceInfo = NsdServiceInfo()
        serviceInfo.serviceName = SERVICE_NAME
        serviceInfo.serviceType = SERVICE_TYPE
        serviceInfo.port = ServerSocket(0).localPort

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, createRegistrationListener())
    }

    private fun createRegistrationListener(): RegistrationListener? {
        return object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                Timber.d("Registered service ${serviceInfo.serviceName} on port ${serviceInfo.port}")
                mRegistrationListener = this
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo,
                                              errorCode: Int) {
                Timber.w("Registration of service ${serviceInfo.serviceName} failed")
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                Timber.d("Service ${serviceInfo.serviceName} unregistered")
                mRegistrationListener = null
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo,
                                                errorCode: Int) {
                Timber.w("Unregistration failed for service ${serviceInfo.serviceName}")
            }
        }
    }

    fun unregister() {
        if (mRegistrationListener != null) {
            nsdManager.unregisterService(mRegistrationListener)
        }
    }
}