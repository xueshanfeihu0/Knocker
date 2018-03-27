package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class TimeReceiver : BroadcastReceiver() {

    private var mHaveRegistered = false

    var listener: TimeListener? = null

    fun isRegistered(): Boolean {
        return mHaveRegistered
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        listener?.onTimeChanged()
    }

    fun unregister(context: Context?) {
        if (context != null) {
            context.unregisterReceiver(this)
            mHaveRegistered = false
        }
    }

    fun register(context: Context?) {
        if (context != null) {
            val timeFilter = IntentFilter()
            timeFilter.addAction(Intent.ACTION_TIME_TICK)
            timeFilter.addAction(Intent.ACTION_TIME_CHANGED)
            timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
            timeFilter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
            context.registerReceiver(this, timeFilter)
            mHaveRegistered = true
        }
    }

    interface TimeListener {
        fun onTimeChanged()
    }
}