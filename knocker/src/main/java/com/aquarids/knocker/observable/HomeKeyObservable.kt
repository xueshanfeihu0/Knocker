package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.aquarids.knocker.utils.getApplication
import java.util.*

object HomeKeyObservable : Observable() {

    private val mReceiver = HomeKeyReceiver()

    @Synchronized
    override fun addObserver(observer: Observer?) {
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
        }
    }

    class HomeKeyReceiver : BroadcastReceiver() {

        private var mHaveRegistered = false

        companion object {
            const val SYSTEM_DIALOG_REASON_KEY = "reason"
            const val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (SYSTEM_DIALOG_REASON_HOME_KEY == reason) {
                    setChanged()
                    notifyObservers()
                }
            }
        }

        fun register(context: Context?) {
            context?.let {
                val timeFilter = IntentFilter()
                timeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                timeFilter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
                context.registerReceiver(this, timeFilter)
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