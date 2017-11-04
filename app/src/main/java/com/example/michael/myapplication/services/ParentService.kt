package com.example.michael.myapplication.services

import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import android.os.IBinder
import timber.log.Timber
import java.net.ServerSocket

class ParentService : GenericService() {

    companion object {
        val SERVICE_NAME = "ParentService"
        val RECEIVER_STARTED = "RECEIVER_STARTED"
        val RECEIVER_STOPPED = "RECEIVER_STOPPED"
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

    override fun onCreate() {
        super.onCreate()
        Timber.d("ParentService started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ParentService destroyed")
    }

    private var mRegistrationListener: RegistrationListener? = null

    override fun start() {
        if (isRunning()) {
            Timber.d("$SERVICE_NAME already running")
            return
        }

        val serviceInfo = NsdServiceInfo()
        serviceInfo.serviceName = SERVICE_NAME
        serviceInfo.serviceType = SERVICE_TYPE
        serviceInfo.port = ServerSocket(0).localPort

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, createRegistrationListener())
    }

    override fun isRunning(): Boolean {
        return mRegistrationListener != null
    }

    private fun createRegistrationListener(): RegistrationListener? {
        return object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                Timber.d("${serviceInfo.serviceName} registred on port ${serviceInfo.port}")
                mRegistrationListener = this
                broadcastManager.sendBroadcast(Intent(RECEIVER_STARTED))
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo,
                                              errorCode: Int) {
                Timber.w("Registration of service ${serviceInfo.serviceName} failed")
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                Timber.d("${serviceInfo.serviceName} unregistred")
                mRegistrationListener = null
                broadcastManager.sendBroadcast(Intent(RECEIVER_STOPPED))
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo,
                                                errorCode: Int) {
                Timber.w("Unregistration failed for service ${serviceInfo.serviceName}")
            }
        }
    }

    override fun stop() {
        if (mRegistrationListener != null) {
            mNsdManager.unregisterService(mRegistrationListener)
        }
    }

    override fun getName(): String {
        return ChildService.SERVICE_NAME
    }

    override fun getStartAction(): String {
        return RECEIVER_STARTED
    }

    override fun getStopAction(): String {
        return RECEIVER_STOPPED
    }
}