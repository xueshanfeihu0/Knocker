package com.aquarids.knocker.observable

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
}