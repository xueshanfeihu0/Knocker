package com.aquarids.knocker

import android.content.Context
import com.aquarids.knocker.observable.BatteryObservable
import com.aquarids.knocker.observable.HomeKeyObservable
import com.aquarids.knocker.observable.ScreenObservable
import com.aquarids.knocker.observable.TimeObservable
import com.aquarids.knocker.utils.getApplication
import java.util.*

object KnockerManager {

    private var mFragmentClassName = ""

    fun monitorHomeKey(observer: Observer) {
        HomeKeyObservable.addObserver(observer)
    }

    fun freeHomeKey(observer: Observer) {
        HomeKeyObservable.deleteObserver(observer)
    }

    fun monitorBattery(observer: Observer) {
        BatteryObservable.addObserver(observer)
    }

    fun freeBattery(observer: Observer) {
        BatteryObservable.deleteObserver(observer)
    }

    fun monitorScreen(observer: Observer) {
        ScreenObservable.addObserver(observer)
    }

    fun freeScreen(observer: Observer) {
        ScreenObservable.deleteObserver(observer)
    }

    fun monitorTime(observer: Observer) {
        TimeObservable.addObserver(observer)
    }

    fun freeTime(observer: Observer) {
        TimeObservable.deleteObserver(observer)
    }

    fun startKnockerService(frgClassName: String) {
        getApplication()?.let {
            mFragmentClassName = frgClassName
            KnockerService.start(it)
        }
    }

    fun stopKnockerService() {
        getApplication()?.let {
            KnockerService.stop(it)
            mFragmentClassName = ""
        }
    }

    fun startKnockerAct(context: Context) {
        if (mFragmentClassName.isEmpty()) {
            return
        }
        KnockerActivity.start(context, mFragmentClassName)
    }
}