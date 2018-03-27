package com.aquarids.knocker.observable

import com.aquarids.knocker.utils.getApplication
import java.util.*

object HomeKeyObservable : Observable() {

    private val mReceiver = HomeKeyReceiver()

    init {
        mReceiver.listener = object : HomeKeyReceiver.HomeKeyListener {
            override fun onHomeKeyTouch() {
                setChanged()
                notifyObservers()
            }
        }
    }

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
}