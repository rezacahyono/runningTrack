package com.tugaspti.runningtrack.ui.tracking

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tugaspti.runningtrack.R

class CancelRunDialog: DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener: (() -> Unit)) {
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.cancelRun))
            .setMessage(resources.getString(R.string.messageCancel))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                yesListener?.let { yes ->
                    yes()
                }

            }
            .setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }}