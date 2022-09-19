package com.w4eret1ckrtb1tch.ble.data.system.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.getSystemService
import io.reactivex.Completable


class BluetoothAdvertiserService(context: Context) {

    private val bluetoothAdvertiser = context
        .getSystemService<BluetoothManager>()
        ?.adapter
        ?.bluetoothLeAdvertiser

    private var advertiseCallback: AdvertiseCallback? = null

    private val settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
        .build()

    private val data = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .setIncludeTxPowerLevel(true)
        .addServiceUuid(SERVICE_UUID)
        .build()

    init {
        Log.d("TAG", "BluetoothAdvertiserService: $context ")
    }

    @SuppressLint("MissingPermission")
    fun startAdvertising(): Completable {
        return Completable
            .create { emitter ->
                if (advertiseCallback != null) {
                    emitter.onError(IllegalStateException("Advertising running"))
                }

                advertiseCallback = object : AdvertiseCallback() {

                    override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                        super.onStartSuccess(settingsInEffect)
                        emitter.onComplete()
                    }

                    override fun onStartFailure(errorCode: Int) {
                        super.onStartFailure(errorCode)
                        emitter.onError(IllegalStateException(errorCode.toString()))
                    }
                }

                advertiseCallback?.let { callback ->
                    bluetoothAdvertiser?.startAdvertising(settings, data, callback)
                        ?: emitter.onError(IllegalStateException("The service or null if the class is not a supported system service"))
                } ?: emitter.onError(IllegalStateException("Advertising initialization error"))
            }
            .doOnComplete {
                Log.d("TAG", "startAdvertising: ok")
            }
            .doOnError {
                Log.d("TAG", "startAdvertising: $it")
            }
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising(): Completable {
        return Completable
            .fromAction {
                advertiseCallback?.let { callback ->
                    bluetoothAdvertiser?.stopAdvertising(callback)
                } ?: throw IllegalStateException("Advertising not running")
            }
            .doOnComplete {
                Log.d("TAG", "stopAdvertising: ok")
                advertiseCallback = null
            }
            .doOnError {
                Log.d("TAG", "stopAdvertising: $it")
            }
    }

    companion object {
        val SERVICE_UUID: ParcelUuid = ParcelUuid.fromString("795090c7-420d-4048-a24e-18e60180e23c")
    }
}