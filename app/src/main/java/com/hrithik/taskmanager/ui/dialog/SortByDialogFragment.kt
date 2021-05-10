package com.hrithik.taskmanager.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.SortOrder
import com.hrithik.taskmanager.databinding.ItemDialogBinding
import com.hrithik.taskmanager.databinding.SortByDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortByDialogFragment : DialogFragment() {

    private val viewModel: SortByDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.sort_by_dialog, null)
        dialog.setContentView(view)

        val binding = SortByDialogBinding.bind(view)

        val layout = binding.linear
        binding.title.text = resources.getString(R.string.sort_by)

        val timeAdded = layoutInflater.inflate(R.layout.item_dialog, null)
        val dueDate = layoutInflater.inflate(R.layout.item_dialog, null)

        val timeAddedBinding = ItemDialogBinding.bind(timeAdded)
        val dueDateBinding = ItemDialogBinding.bind(dueDate)

        timeAddedBinding.itemText.text = resources.getString(R.string.time_added)
        dueDateBinding.itemText.text = resources.getString(R.string.due_date)
        timeAddedBinding.radioBtn.isClickable = false
        dueDateBinding.radioBtn.isClickable = false
        layout.addView(timeAdded, params)
        layout.addView(dueDate, params)


        if (viewModel.sortOrder == SortOrder.BY_TIME_ADDED)
            timeAddedBinding.radioBtn.isChecked = true
        else
            dueDateBinding.radioBtn.isChecked = true


        timeAdded.setOnClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_TIME_ADDED)
            dialog.dismiss()
        }

        dueDate.setOnClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_DUE_DATE)
            dialog.dismiss()
        }

        return dialog

    }
}