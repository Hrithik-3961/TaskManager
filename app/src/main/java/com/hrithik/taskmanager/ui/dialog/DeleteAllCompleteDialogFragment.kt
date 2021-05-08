package com.hrithik.taskmanager.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompleteDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Do you really want to delete all completed tasks?")
            .setNegativeButton("Cancel", null)

            .create()
}