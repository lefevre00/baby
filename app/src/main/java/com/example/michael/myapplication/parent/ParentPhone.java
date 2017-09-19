package com.example.michael.myapplication.parent;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.WorkerThread;

import com.example.michael.myapplication.baby.BabyPhone;

import timber.log.Timber;

@WorkerThread
public class ParentPhone {
    private final NsdManager nsdManager;

    public ParentPhone(Context applicationContext) {
        this.nsdManager = (NsdManager) applicationContext.getSystemService(Context.NSD_SERVICE);
    }

    public ParentPhone publish() {
        return this;
    }

    public void lookForChild() {
        NsdManager.DiscoveryListener listener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Timber.d("Start discovery failed [%s]", serviceType);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Timber.d("Stop discovery failed [%s]", serviceType);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Timber.d("Discovery started [%s]", serviceType);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Timber.d("Discovery stopped [%s]", serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Timber.d("Found service %s", serviceInfo.getServiceType());
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Timber.d("Service lost [%s]", serviceInfo.getServiceType());
            }
        };
        nsdManager.discoverServices(BabyPhone.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, listener);
    }
}
