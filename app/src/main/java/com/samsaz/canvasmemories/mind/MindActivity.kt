package com.samsaz.canvasmemories.mind

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Screen

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindActivity : AppCompatActivity() {

    private lateinit var viewModel: MindViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind)

        viewModel = ViewModelProviders.of(this)[MindViewModel::class.java]
        viewModel.getScreenLiveData().observe(this, Observer { goToScreen(it) })
    }

    private fun goToScreen(screen: Screen) {
        val oldFragment = supportFragmentManager.findFragmentById(R.id.container)
        var newFragment: Fragment? = null
        var addToBackStack = false
        when (screen) {
            is Screen.Editor -> {
                if (oldFragment != null && oldFragment !is MindEditorFragment &&
                    supportFragmentManager.backStackEntryCount > 0
                ) {
                    onBackPressed()
                } else if (oldFragment == null || oldFragment is MindStatsFragment) {
                    newFragment = MindEditorFragment()
                }
            }
            is Screen.Stats -> {
                if (oldFragment == null || oldFragment is MindEditorFragment) {
                    addToBackStack = oldFragment != null
                    newFragment = MindStatsFragment()
                }
            }
        }

        if (newFragment == null)
            return

        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.container, newFragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate())
            super.onBackPressed()
    }
}