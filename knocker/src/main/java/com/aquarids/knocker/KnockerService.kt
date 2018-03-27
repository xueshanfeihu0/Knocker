package com.aquarids.knocker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.aquarids.knocker.observable.ScreenObservable
import java.util.*

class KnockerService : Service(), Observer {

    companion object {

        fun start(context: Context?) {
            context?.let {
                context.startService(Intent(context, KnockerService::class.java))
            }
        }

        fun stop(context: Context?) {
            context?.let {
                context.stopService(Intent(context, KnockerService::class.java))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not Support")
    }

    override fun onCreate() {
        super.onCreate()
        KnockerManager.monitorScreen(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        KnockerManager.freeScreen(this)
    }

    override fun update(observable: Observable?, arg: Any?) {
        if (arg is ScreenStatus && arg.isScreenOn) {
            Thread(Runnable {
                KnockerManager.startKnockerAct(this)
            }).start()
        }
    }
}