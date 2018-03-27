package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.aquarids.knocker.ScreenStatus

class ScreenReceiver : BroadcastReceiver() {

    private var mHaveRegistered = false

    var listener: ScreenListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (Intent.ACTION_SCREEN_ON == action) {
            listener?.onScreenChanged(ScreenStatus(true))
        } else if (Intent.ACTION_SCREEN_OFF == action) {
            listener?.onScreenChanged(ScreenStatus(false))
        }
    }

    fun register(context: Context?) {
        if (context != null) {
            val screenOnOffFilter = IntentFilter()
            screenOnOffFilter.addAction(Intent.ACTION_SCREEN_ON)
            screenOnOffFilter.addAction(Intent.ACTION_SCREEN_OFF)
            screenOnOffFilter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
            context.registerReceiver(this, screenOnOffFilter)
            mHaveRegistered = true
        }
    }

    fun unregister(context: Context?) {
        if (context != null) {
            context.unregisterReceiver(this)
            mHaveRegistered = false
        }
    }

    fun isRegistered() = mHaveRegistered

    interface ScreenListener {
        fun onScreenChanged(model: ScreenStatus)
    }
}