package com.w4eret1ckrtb1tch.ble.ui.common

import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import com.w4eret1ckrtb1tch.ble.R

class ProgressDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.ProgressDialogTheme).apply {
            setContentView(ProgressBar(context))
        }
    }
}