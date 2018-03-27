package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.aquarids.knocker.utils.getApplication
import java.util.*

object TimeObservable : Observable() {

    private val mReceiver = TimeReceiver()

    @Synchronized
    override fun addObserver(observer: Observer) {
        super.addObserver(observer)
        if (countObservers() > 0 && !mReceiver.isRegistered()) {
            getApplication()?.let {
                mReceiver.register(it)
            }
        }
        observer.update(this, null)
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

    class TimeReceiver : BroadcastReceiver() {

        private var mHaveRegistered = false

        fun isRegistered(): Boolean {
            return mHaveRegistered
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            setChanged()
            notifyObservers()
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
    }
}