package com.aquarids.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.aquarids.knocker.KnockerManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KnockerManager.startKnockerService("com.aquarids.sample.TestFragment")
    }
}
