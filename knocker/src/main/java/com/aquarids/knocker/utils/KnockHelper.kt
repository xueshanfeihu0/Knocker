package com.aquarids.knocker.utils

import android.app.Application
import android.app.KeyguardManager
import android.content.Context
import android.util.Log
import android.view.Window
import android.view.WindowManager

fun isKeyguardSecure(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isKeyguardSecure
}

fun Window.setLockScreenWindowFlag() {
    this.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Window.setLockScreenWindowWithDismissFlag() {
    this.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    this.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
}

fun Window.setDismissKeyguardFlag() {
    this.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun getApplication(): Application? {
    var app: Application? = null
    try {
        app = Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null) as Application
    } catch (e: Exception) {
        try {
            app = Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null) as Application
        } catch (ex: Exception) {

        }
    } finally {
        return app
    }
}