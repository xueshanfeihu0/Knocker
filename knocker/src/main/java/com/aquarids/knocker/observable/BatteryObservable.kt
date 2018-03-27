package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.aquarids.knocker.BatteryStatus
import com.aquarids.knocker.utils.getApplication
import java.util.*

object BatteryObservable : Observable() {

    private val mReceiver = BatteryReceiver()

    private var mCurrentBatteryModel: BatteryStatus? = null

    @Synchronized
    override fun addObserver(observer: Observer) {
        super.addObserver(observer)
        if (countObservers() > 0 && !mReceiver.isRegistered()) {
            getApplication()?.let {
                mReceiver.register(it)
            }
        }
        mCurrentBatteryModel?.let {
            observer.update(this, it)
        }
    }

    @Synchronized
    override fun deleteObserver(observer: Observer) {
        super.deleteObserver(observer)
        if (countObservers() == 0 && mReceiver.isRegistered()) {
            getApplication()?.let {
                mReceiver.unregister(it)
            }
        }
    }

    class BatteryReceiver : BroadcastReceiver() {

        private var mHaveRegistered = false
        private var mIsPlugged = false
        private var mLevel = -1

        override fun onReceive(context: Context?, intent: Intent?) {
            if (Intent.ACTION_BATTERY_CHANGED == intent?.action) {
                val isPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) > 0
                val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)

                val chargedPercentage: Int = if (batteryScale <= 0) batteryLevel else (batteryLevel * 100f / batteryScale).toInt()

                if (mIsPlugged != isPlugged || mLevel != chargedPercentage) {
                    mIsPlugged = isPlugged
                    mLevel = chargedPercentage
                    mCurrentBatteryModel = BatteryStatus(mIsPlugged, mLevel)
                    setChanged()
                    notifyObservers(mCurrentBatteryModel)
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
    }
}