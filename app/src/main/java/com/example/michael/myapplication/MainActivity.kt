package com.example.michael.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.michael.myapplication.network.NetworkManager
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == R.id.networkInfo) showNetworkDialog()
        }
        return true
    }

    private fun showNetworkDialog() {
        AlertDialog.Builder(this)
                .setMessage(NetworkManager(this).info() ?: getString(R.string.disconnected))
                .setTitle(getString(R.string.dialog_title_info))
                .show()
    }
}
