package com.aquarids.knocker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.aquarids.knocker.observable.HomeKeyObservable
import com.aquarids.knocker.ui.SliderLayout
import com.aquarids.knocker.utils.isKeyguardSecure
import com.aquarids.knocker.utils.setLockScreenWindowFlag
import com.aquarids.knocker.utils.setLockScreenWindowWithDismissFlag
import java.util.*

class KnockerActivity : AppCompatActivity(), Observer, KnockerFragment.FragmentInteractionListener {

    private var mContainer: SliderLayout? = null

    private val mFrgClazzName by lazy { intent.getStringExtra(CLASS_NAME) }

    companion object {

        private const val CLASS_NAME = "class_name"

        @JvmStatic
        fun start(context: Context, className: String) {
            val intent = Intent(context, KnockerActivity::class.java)
            intent.putExtra(CLASS_NAME, className)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knocker)

        if (mFrgClazzName.isNullOrEmpty()) {
            finish()
        }

        mContainer = findViewById(R.id.container)

        init()
    }

    private fun addFragment() {
        try {
            val frgClass = Class.forName(mFrgClazzName)
            val frgInstance = frgClass.newInstance() as KnockerFragment
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frg_content, frgInstance)
            transaction.commit()
        } catch (e: Exception) {
            Log.e("knocker", e.message)
            finish()
        }
    }

    private fun init() {
        if (isKeyguardSecure(this)) {
            window.setLockScreenWindowFlag()
        } else {
            window.setLockScreenWindowWithDismissFlag()
        }
        KnockerManager.monitorHomeKey(this)

        mContainer?.listener = object : SliderLayout.SliderListener {
            override fun onSlide(view: SliderLayout, direction: Int, hasScrolled: Boolean) {
                if (hasScrolled) {
                    finish()
                }
            }
        }
        addFragment()
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

    override fun turnOffLocker() {
        finish()
    }
}
