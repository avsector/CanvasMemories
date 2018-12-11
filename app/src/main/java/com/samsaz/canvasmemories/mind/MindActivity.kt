package com.samsaz.canvasmemories.mind

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samsaz.canvasmemories.R

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind)

        val f = supportFragmentManager.findFragmentById(R.id.container)
        if (f == null) {
            val fragment = MindCanvasFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                .commitNow()
        }
    }
}