package com.example.michael.myapplication.baby;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import timber.log.Timber;

public class BabyPhone {

    static final int PORT = 4455;
    public static final String SERVICE_TYPE = "_baby._tcp";
    private final NsdManager nsdManager;

    public BabyPhone(Context applicationContext) {
        this.nsdManager = (NsdManager) applicationContext.getSystemService(Context.NSD_SERVICE);
    }

    public void publish() {

// Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("BabyPhone");
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(PORT);
        nsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, createRegistrationListener());
    }

    private NsdManager.RegistrationListener createRegistrationListener() {
        return new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Timber.d("Registration failed with error %d", errorCode);
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Timber.d("Unregistration failed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Timber.d("Someone registered");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Timber.d("Someone unregistered");
            }
        };
    }
}
