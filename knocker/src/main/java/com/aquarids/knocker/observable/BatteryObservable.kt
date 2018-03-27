package com.aquarids.knocker.observable

import com.aquarids.knocker.BatteryStatus
import com.aquarids.knocker.utils.getApplication
import java.util.*

object BatteryObservable : Observable() {

    private val mReceiver = BatteryReceiver()

    private var mCurrentBatteryModel: BatteryStatus? = null

    init {
        mReceiver.listener = object : BatteryReceiver.BatteryListener {
            override fun onBatteryChanged(model: BatteryStatus) {
                mCurrentBatteryModel = model
                setChanged()
                notifyObservers(mCurrentBatteryModel)
            }
        }
    }

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

}