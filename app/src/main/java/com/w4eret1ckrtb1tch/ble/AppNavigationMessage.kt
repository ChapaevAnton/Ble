package com.w4eret1ckrtb1tch.ble

import me.dmdev.rxpm.navigation.NavigationMessage

sealed class AppNavigationMessage : NavigationMessage {

    object Back : AppNavigationMessage()
}