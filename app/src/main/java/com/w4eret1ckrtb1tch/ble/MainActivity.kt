package com.w4eret1ckrtb1tch.ble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w4eret1ckrtb1tch.ble.ui.ble.BleScreen
import com.w4eret1ckrtb1tch.ble.ui.extension.openScreen
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationMessageHandler

class MainActivity : AppCompatActivity(), NavigationMessageHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.openScreen(BleScreen(), addToBackStack = false)
        }
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is AppNavigationMessage.Back -> super.onBackPressed()
        }
        return true
    }
}