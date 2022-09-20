package com.w4eret1ckrtb1tch.ble.data.system.ble

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import io.reactivex.Completable


class BluetoothAdvertiserService(private val context: Context) {

    private val bluetoothAdvertiser = context
        .getSystemService<BluetoothManager>()
        ?.adapter
        ?.bluetoothLeAdvertiser

    private var advertiseCallback: AdvertiseCallback? = null

    private val settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
        .build()

    private val advertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .setIncludeTxPowerLevel(true)
        .addServiceUuid(SERVICE_UUID)
        .build()

    private var scanResponse: AdvertiseData? = null

    init {
        Log.d("TAG", "BluetoothAdvertiserService: $context")
    }

    fun startAdvertising(userId: ParcelUuid? = null): Completable {
        return Completable
            .create { emitter ->
                if (bluetoothAdvertiser == null) {
                    return@create emitter
                        .onError(IllegalStateException("The service or null if the class is not a supported system service"))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@create emitter.onError(IllegalStateException("Permission not granted at run time"))
                    }
                }

                if (advertiseCallback != null) {
                    return@create emitter.onError(IllegalStateException("Advertising running"))
                }

                if (userId != null) {
                    scanResponse = AdvertiseData.Builder()
                        .setIncludeDeviceName(false)
                        .addServiceUuid(userId)
                        .build()
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

                if (advertiseCallback == null) {
                    return@create emitter
                        .onError(IllegalStateException("Advertising initialization error"))
                }

                bluetoothAdvertiser.startAdvertising(
                    settings,
                    advertiseData,
                    scanResponse,
                    advertiseCallback
                )

            }
            .doOnComplete {
                Log.d("TAG", "startAdvertising: ok")
            }
            .doOnError {
                Log.d("TAG", "startAdvertising: $it")
            }
    }

    fun stopAdvertising(): Completable {
        return Completable
            .fromAction {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) throw IllegalStateException("Permission not granted at run time")
                }

                if (advertiseCallback == null) throw IllegalStateException("Advertising not running")

                bluetoothAdvertiser?.stopAdvertising(advertiseCallback)
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