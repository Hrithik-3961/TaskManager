package com.hrithik.taskmanager.ui.dialog

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.hrithik.taskmanager.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompleteDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Do you really want to delete all completed tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onConfirmClick()
            }
            .create()
            .apply {
                setOnShowListener {
                    when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_blue_1
                                )
                            )
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_grey
                                )
                            )
                        }
                        Configuration.UI_MODE_NIGHT_NO -> {
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.dark_blue_1
                                )
                            )
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        }
                    }
                }
            }
    }

}