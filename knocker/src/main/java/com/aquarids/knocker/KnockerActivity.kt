package com.aquarids.knocker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aquarids.knocker.observable.HomeKeyObservable
import com.aquarids.knocker.ui.SliderLayout
import com.aquarids.knocker.utils.isKeyguardSecure
import com.aquarids.knocker.utils.setLockScreenWindowFlag
import com.aquarids.knocker.utils.setLockScreenWindowWithDismissFlag
import java.util.*

class KnockerActivity : AppCompatActivity(), Observer {

    private val mContainer by lazy { findViewById<SliderLayout>(R.id.container) }

    companion object {

        @JvmStatic
        fun start(context: Context, frg: KnockerFragment) {
            // todo pass a frg
            context.startActivity(Intent(context, KnockerActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knocker)

        init()
    }

    fun addFragment() {

    }

    private fun init() {
        if (isKeyguardSecure(this)) {
            window.setLockScreenWindowFlag()
        } else {
            window.setLockScreenWindowWithDismissFlag()
        }
        KnockerManager.monitorHomeKey(this)

        mContainer.listener = object : SliderLayout.SliderListener {
            override fun onSlide(view: SliderLayout, direction: Int, hasScrolled: Boolean) {
                if (hasScrolled) {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        KnockerManager.freeHomeKey(this)
    }

    override fun onBackPressed() {
        return
    }

    override fun update(observable: Observable?, arg: Any?) {
        observable?.let {
            if (observable === HomeKeyObservable) {
                if (!isFinishing) {
                    finish()
                }
            }
        }
    }
}
