package com.example.michael.myapplication.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.AttributeSet
import android.widget.ImageButton
import com.example.michael.myapplication.R
import com.example.michael.myapplication.services.GenericService
import timber.log.Timber

class RoleButton : ImageButton {

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    inner class UnactivableStopReceiver : BroadcastReceiver() {
        var registred = false

        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("Button ${activable?.getName()} received ${intent?.action}")
            if (registred && intent?.action.equals(unactivable?.getStopAction())) {
                broadcastManager.unregisterReceiver(this)
                registred = false
                startActivable()
            }
        }
    }

    var activable : GenericService? = null
    var unactivable : GenericService? = null
    var checked : Boolean = false
    val unactivableStoppedAction = UnactivableStopReceiver()

    private var broadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

    init {
        setOnClickListener({
            if (checked) {
                activable?.stop()
                checked = false
            } else {
                if (unactivable?.isRunning()!!) {
                    unactivable?.stop()
                    activateOnBroadcast()
                } else {
                    startActivable()
                }
                checked = true
            }
            applyStatus()
        })
        applyStatus()
    }

    private fun applyStatus() {
        background.setColorFilter(ContextCompat.getColor(context, if (checked) R.color.colorAccent else R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
    }

    fun startActivable() {
        activable!!.start()
    }

    private fun activateOnBroadcast() {
        broadcastManager.registerReceiver(unactivableStoppedAction, IntentFilter(unactivable?.getStopAction()))
        unactivableStoppedAction.registred = true
    }

    inner class ServiceStatusReceiver(private val service: GenericService) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("Button ${activable?.getName()} received ${intent?.action}")
            when (intent?.action) {
                service.getStopAction() -> checked = false
                else -> Timber.d("Button ${activable?.getName()} do not handled ${intent?.action}")
            }
            applyStatus()
        }
    }

    fun setStartable(service: GenericService) {
        activable = service
        val intentFilter = IntentFilter()
        intentFilter.addAction(service.getStartAction())
        intentFilter.addAction(service.getStopAction())
        broadcastManager.registerReceiver(ServiceStatusReceiver(service), intentFilter)
    }

    fun setStoppable(service: GenericService) {
        unactivable = service
    }
}

