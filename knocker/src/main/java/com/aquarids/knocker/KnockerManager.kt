package com.aquarids.knocker

import android.content.Context
import com.aquarids.knocker.observable.HomeKeyObservable
import com.aquarids.knocker.utils.getApplication
import java.util.*

object KnockerManager {

    private var mFragment: KnockerFragment? = null

    fun monitorHomeKey(observer: Observer) {
        HomeKeyObservable.addObserver(observer)
    }

    fun freeHomeKey(observer: Observer) {
        HomeKeyObservable.deleteObserver(observer)
    }

    fun monitorBattery(observer: Observer) {
        HomeKeyObservable.addObserver(observer)
    }

    fun freeBattery(observer: Observer) {
        HomeKeyObservable.deleteObserver(observer)
    }

    fun monitorScreen(observer: Observer) {
        HomeKeyObservable.addObserver(observer)
    }

    fun freeScreen(observer: Observer) {
        HomeKeyObservable.deleteObserver(observer)
    }

    fun monitorTime(observer: Observer) {
        HomeKeyObservable.addObserver(observer)
    }

    fun freeTime(observer: Observer) {
        HomeKeyObservable.deleteObserver(observer)
    }

    fun startKnockerService(frg: KnockerFragment) {
        getApplication()?.let {
            if (null != mFragment) {
                mFragment = null
            }
            mFragment = frg
            KnockerService.start(it)
        }
    }

    fun stopKnockerService() {
        getApplication()?.let {
            KnockerService.stop(it)
            mFragment = null
        }
    }

    fun startKnockerAct(context: Context) {
        if (null == mFragment) {
            return
        }
        mFragment?.let {
            KnockerActivity.start(context, it)
        }
    }

    fun stopKnockerAct() {

    }
}