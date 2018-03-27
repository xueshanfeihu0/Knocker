package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.aquarids.knocker.BatteryStatus

class BatteryReceiver : BroadcastReceiver() {

    private var mHaveRegistered = false
    private var mIsPlugged = false
    private var mLevel = -1

    var listener: BatteryListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BATTERY_CHANGED == intent?.action) {
            val isPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) > 0
            val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)

            val chargedPercentage: Int = if (batteryScale <= 0) batteryLevel else (batteryLevel * 100f / batteryScale).toInt()

            if (mIsPlugged != isPlugged || mLevel != chargedPercentage) {
                mIsPlugged = isPlugged
                mLevel = chargedPercentage
                listener?.onBatteryChanged(BatteryStatus(mIsPlugged, mLevel))
            }
        }
    }

    fun register(context: Context?) {
        context?.let {
            val batteryFilter = IntentFilter()
            batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(this, batteryFilter)
            mHaveRegistered = true
        }
    }

    fun unregister(context: Context?) {
        context?.let {
            context.unregisterReceiver(this)
            mHaveRegistered = false
        }
    }

    fun isRegistered() = mHaveRegistered

    interface BatteryListener {
        fun onBatteryChanged(model: BatteryStatus)
    }
}