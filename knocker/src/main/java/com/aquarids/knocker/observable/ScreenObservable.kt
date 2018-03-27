package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.aquarids.knocker.ScreenStatus
import com.aquarids.knocker.utils.getApplication
import java.util.*

object ScreenObservable : Observable() {

    private val mReceiver = ScreenReceiver()

    @Synchronized
    override fun addObserver(observer: Observer) {
        super.addObserver(observer)
        if (countObservers() > 0 && !mReceiver.isRegistered()) {
            getApplication()?.let {
                mReceiver.register(it)
            }
        }
    }

    @Synchronized
    override fun deleteObserver(observer: Observer) {
        super.deleteObserver(observer)
        if (countObservers() == 0 && mReceiver.isRegistered()) {
            getApplication()?.let {
                mReceiver.unregister(it)
            }
            countObservers() > 0        }
    }

    class ScreenReceiver : BroadcastReceiver() {

        private var mHaveRegistered = false

        override fun onReceive(context: Context?, intent: Intent?) {
            setChanged()
            val action = intent?.action ?: return
            if (Intent.ACTION_SCREEN_ON == action) {
                notifyObservers(ScreenStatus(true))
            } else if (Intent.ACTION_SCREEN_OFF == action) {
                notifyObservers(ScreenStatus(false))
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
    }
}