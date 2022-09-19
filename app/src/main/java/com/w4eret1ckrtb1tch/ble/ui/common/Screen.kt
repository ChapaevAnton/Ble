package com.w4eret1ckrtb1tch.ble.ui.common

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.w4eret1ckrtb1tch.ble.ui.extension.findScreen
import com.w4eret1ckrtb1tch.ble.ui.extension.showDialog
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.base.PmFragment
import me.dmdev.rxpm.passTo
import me.dmdev.rxpm.widget.bindTo

abstract class Screen<PM : ScreenPm> : PmFragment<PM>(), BackHandler {

    abstract val screenLayout: Int

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(screenLayout, container, false)
    }

    override fun onBindPresentationModel(pm: PM) {
        pm.errorDialog bindTo { message, _ ->
            AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        }
    }

    override fun handleBack(): Boolean {
        Unit passTo presentationModel.backAction
        return true
    }

    val progressConsumer = Consumer<Boolean> {
        if (it) {
            childFragmentManager
                .showDialog(ProgressDialog())
        } else {
            childFragmentManager
                .findScreen<ProgressDialog>()
                ?.dismiss()
        }
    }
}
