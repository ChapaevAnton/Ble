package com.w4eret1ckrtb1tch.ble.ui.ble

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.jakewharton.rxbinding3.view.clicks
import com.w4eret1ckrtb1tch.ble.R
import com.w4eret1ckrtb1tch.ble.di.PmModule
import com.w4eret1ckrtb1tch.ble.di.PmModule.getWithProperty
import com.w4eret1ckrtb1tch.ble.ui.common.Screen
import kotlinx.android.synthetic.main.screen_ble.btStartAdvertiser
import kotlinx.android.synthetic.main.screen_ble.btStartScan
import kotlinx.android.synthetic.main.screen_ble.btStopAdvertiser
import kotlinx.android.synthetic.main.screen_ble.btStopScan
import me.dmdev.rxpm.bindTo

class BleScreen : Screen<BlePm>() {

    private val requestPermissionScanLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
            val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions[Manifest.permission.BLUETOOTH_SCAN] == true &&
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            } else {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            }
            if (isGranted) {
                Toast.makeText(requireContext(), "Permissions granted ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Not permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val requestPermissionAdvertiserLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permissions granted ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Not permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override val screenLayout: Int = R.layout.screen_ble

    // QUESTION: передал параметр именно как в проекте...
    override fun providePresentationModel(): BlePm =
        getWithProperty(PmModule.PARAM_VALUE to "1234567890")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onBindPresentationModel(pm: BlePm) {
        super.onBindPresentationModel(pm)

        btStartAdvertiser.clicks().map {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissionAdvertiserLauncher.launch(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        } bindTo pm.startAdvertisingClick

        btStopAdvertiser.clicks() bindTo pm.stopAdvertisingClick

        btStartScan.clicks().map {
            // QUESTION: возможно понадобится еще permission...
            requestPermissionScanLauncher.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } else {
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }
            )
        } bindTo pm.startScanningClick

        btStopScan.clicks() bindTo pm.stopScanningClick

    }

}




