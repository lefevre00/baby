package com.example.michael.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.michael.myapplication.services.ChildService
import com.example.michael.myapplication.services.ParentService
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var mParentServiceBound = false
    private var mChildServiceBound = false
    internal var mParentService: ParentService? = null
    internal var mChildService: ChildService? = null

    private val mParentConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("Connected parent service ${name.className}")
            val binder = service as ParentService.ParentServiceBinder
            mParentService = binder.service
            mParentServiceBound = true
            toggleBaby.setStoppable(binder.service)
            toggleParent.setStartable(binder.service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("Disconnected from parent service ${name.className}")
            mParentServiceBound = false
        }
    }

    private val mChildConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Timber.d("Connected to child service $className")
            val binder = service as ChildService.ChildServiceBinder
            mChildService = binder.service
            mChildServiceBound = true
            toggleBaby.setStartable(binder.service)
            toggleParent.setStoppable(binder.service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Timber.d("Disconnected from child service $className")
            mChildServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)

//        toggleBaby.setServices(mChildService, mParentService)
//        ;OnCheckedChangeListener({_, isChecked ->
//            if (isChecked) {
//                if (mParentService!!.isRunning()) {
//                    Timber.d("ParentService running")
//                    startEmitterASAP()
//                } else {
//                    mChildService?.listenBaby()
//                }
//            } else {
//                mChildService?.stopListening()
//                toggleBaby.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
//            }
//        })

//        toggleParent.setOnCheckedChangeListener({_, isChecked ->
//            if (isChecked) {
//                if (mChildService!!.isRunning()) {
//                    startReceiverASAP()
//                } else {
//                    mParentService?.register()
//                }
////                startReceiverOnEmitterStopped()
//            } else {
//                mParentService?.unregister()
//            }
//        })

//        switchParent!!.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                mParentService?.start()
//            } else {
//                mParentService?.stop()
//            }
//        }
//
//        switchChild!!.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // TODO we need to be sure parent service is stopped before launching another service
//                mChildService?.start()
//            } else {
//                mChildService?.stop()
//            }
//        }
    }

    override fun onStart() {
        super.onStart()

        bindService(Intent(this, ParentService::class.java), mParentConnection, Context.BIND_AUTO_CREATE)
        bindService(Intent(this, ChildService::class.java), mChildConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mParentServiceBound) {
            unbindService(mParentConnection)
        }
        if (mChildServiceBound) {
            unbindService(mChildConnection)
        }
    }

    override fun onResume() {
        super.onResume()

//        object : AsyncTask<Void, Void, String>() {
//            override fun doInBackground(params: Array<Void>): String {
//                val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
//            }
//
//            override fun onPostExecute(adress: String?) {
//                wifi_adress.text = adress ?: "Echec"
//            }
//        }.execute()


//        object : AsyncTask<Void, Void, String>() {
//            override fun doInBackground(params: Array<Void>): String {
//                try {
//                    val en = NetworkInterface.getNetworkInterfaces()
//                    while (en.hasMoreElements()) {
//                        val intf = en.nextElement()
//                        val enumIpAddr = intf.inetAddresses
//                        while (enumIpAddr.hasMoreElements()) {
//                            val inetAddress = enumIpAddr.nextElement()
//                            if (!inetAddress.isLoopbackAddress) {
//                                val ip = Formatter.formatIpAddress(inetAddress.hashCode())
//                                Timber.d("Other adress : $ip")
//                                return ip
//                            }
//                        }
//                    }
//                } catch (e: SocketException) {
//                    e.printStackTrace()
//                }
//
//                val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                return cm.activeNetworkInfo.typeName
//            }
//
//            override fun onPostExecute(adress: String?) {
//                other_adress.text = adress ?: "Echec"
//            }
//        }.execute()
    }
}
