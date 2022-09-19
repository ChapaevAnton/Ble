package com.w4eret1ckrtb1tch.ble.di

import com.w4eret1ckrtb1tch.ble.ui.ble.BlePm
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

object PmModule {

    const val PARAM_VALUE = "PARAM_VALUE"

    fun create() = module {
        factory {
            BlePm(
                value = getProperty(PARAM_VALUE),
                startAdvertisingUseCase = get(),
                stopAdvertisingUseCase = get(),
                startScanningUseCase = get(),
                stopScanningUseCase = get()
            )
        }
    }


    inline fun <reified T : Any> getWithProperty(
        property: Pair<String, Any?>,
    ): T {
        val key = property.first
        val value = property.second
        return value?.let {
            KoinJavaComponent.getKoin().run {
                setProperty(key, it)
                val result = get<T>()
                deleteProperty(key)
                result
            }
        } ?: KoinJavaComponent.getKoin().get()
    }
}