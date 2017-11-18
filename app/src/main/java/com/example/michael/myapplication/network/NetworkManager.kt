package com.example.michael.myapplication.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.example.michael.myapplication.R

class NetworkManager(val context: Context) {

    fun info(): String? {
        val wifiIp = getWifiIp()
        return wifiIp ?: context.getString(R.string.no_wifi_connection)
    }

    private fun getWifiIp(): String? {
        if (isConnected()) {
            val wifiService: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiService.isWifiEnabled) {
                return wifiService.connectionInfo.ipAddress.toString()
            }
        }
        return null
    }

    private fun isConnected(): Boolean {
        val connectivityManager: ConnectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo.isConnectedOrConnecting
    }
}

