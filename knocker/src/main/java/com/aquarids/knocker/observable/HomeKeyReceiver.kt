package com.aquarids.knocker.observable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class HomeKeyReceiver : BroadcastReceiver() {

    private var mHaveRegistered = false

    var listener: HomeKeyListener? = null

    companion object {
        const val SYSTEM_DIALOG_REASON_KEY = "reason"
        const val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
            val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
            if (SYSTEM_DIALOG_REASON_HOME_KEY == reason) {
                listener?.onHomeKeyTouch()
            }
        }
    }

    fun register(context: Context?) {
        context?.let {
            context.unregisterReceiver(this)
            mHaveRegistered = false
        }
    }

    fun unregister(context: Context?) {
        context?.let {
            val timeFilter = IntentFilter()
            timeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            timeFilter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
            context.registerReceiver(this, timeFilter)
            mHaveRegistered = true
        }
    }

    fun isRegistered() = mHaveRegistered

    interface HomeKeyListener {
        fun onHomeKeyTouch()
    }
}