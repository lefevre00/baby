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
import java.util.concurrent.atomic.AtomicBoolean

class RoleButton : ImageButton {

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    inner class UnactivableStopReceiver : BroadcastReceiver() {
        var registred = false

        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("${activable?.getName()} received intent ${intent?.action}")
            if (registred && intent?.action.equals(unactivable?.getStopAction())) {
                broadcastManager.unregisterReceiver(this)
                registred = false
                startActivable()
            }
        }
    }

    var activable : GenericService? = null
    var unactivable : GenericService? = null
    var checked : AtomicBoolean = AtomicBoolean(false)
    val unactivableStoppedAction = UnactivableStopReceiver()

    private var broadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

    init {
        setOnClickListener({
            if (checked.get()) {
                activable?.stop()
                checked.set(false)
            } else {
                if (unactivable?.isRunning()!!) {
                    unactivable?.stop()
                    activateOnBroadcast()
                } else {
                    startActivable()
                }
                checked.set(true)
            }
            applyStatus()
        })
        applyStatus()

    }

    private fun applyStatus() {
        background.setColorFilter(ContextCompat.getColor(context, if (checked.get()) R.color.colorAccent else R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
    }

    fun startActivable() {
        activable!!.start()
    }

    private fun activateOnBroadcast() {
        broadcastManager.registerReceiver(unactivableStoppedAction, IntentFilter(unactivable?.getStopAction()))
//        unactivableStoppedAction.registred = true
    }

    inner class ServiceStatusReceiver(private val service: GenericService) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
                Timber.d("Button for ${activable?.getName()} received action ${intent?.action}")
            when (intent?.action) {
                service.getStopAction() -> checked.set(false)
                else -> Timber.d("Action not handled : ${intent?.action}")
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

