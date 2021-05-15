package com.hrithik.taskmanager.ui.addEditTask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.databinding.BottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG1 = "BottomSheetDialogFragment"
    }

    private lateinit var binding: BottomSheetBinding

    private val viewModel: AddEditViewModel by viewModels()

    private val currentDateTime = Calendar.getInstance()
    private var timePicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.bottom_sheet, container, false)
        if (Build.VERSION.SDK_INT > 30)
            view.setOnApplyWindowInsetsListener { _, windowInsets ->
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                view.setPadding(0, 0, 0, imeHeight)
                return@setOnApplyWindowInsetsListener windowInsets
            }
        else
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = BottomSheetBinding.bind(view)

        binding.apply {
            taskText.requestFocus()
            val imm: InputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(taskText, InputMethodManager.HIDE_IMPLICIT_ONLY)

            taskText.background = null
            taskText.setText(viewModel.taskName)
            dateTimeText.text = viewModel.dateTimeText
            currentDateTime.timeInMillis = viewModel.timeInMillis
            dateTimeLayout.visibility =
                if (dateTimeText.text.isNullOrEmpty()) View.GONE else View.VISIBLE

            taskText.addTextChangedListener { text ->
                saveBtn.isEnabled = !text.isNullOrBlank()
                viewModel.taskName = text.toString().capitalize(Locale.getDefault()).trim()
            }

            calendarBtn.setOnClickListener { pickDate() }
            clockBtn.setOnClickListener { pickTime() }
            cross.setOnClickListener {
                dateTimeText.text = ""
                viewModel.dateTimeText = ""
                dateTimeLayout.visibility = View.GONE
            }

            saveBtn.setOnClickListener {
                viewModel.onSaveClicked()
                dismiss()
            }
        }
    }

    private fun pickDate() {

        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val hour = if (timePicked) currentDateTime.get(Calendar.HOUR_OF_DAY) else 23
        val minute = if (timePicked) currentDateTime.get(Calendar.MINUTE) else 59

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            currentDateTime.set(year, month, day, hour, minute, 0)
            currentDateTime.set(Calendar.MILLISECOND, 0)
            val sdf = SimpleDateFormat(getPattern())
            binding.dateTimeText.text = sdf.format(currentDateTime.timeInMillis)
            binding.dateTimeLayout.visibility = View.VISIBLE
            viewModel.dateTimeText = binding.dateTimeText.text.toString()
            viewModel.timeInMillis = currentDateTime.timeInMillis

        }, startYear, startMonth, startDay)
        val datePicker = datePickerDialog.datePicker
        datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue_1))
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_blue_1))
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
    }

    private fun pickTime() {
        val year = currentDateTime.get(Calendar.YEAR)
        val month = currentDateTime.get(Calendar.MONTH)
        val day = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour =
            if (timePicked) currentDateTime.get(Calendar.HOUR_OF_DAY) else Calendar.getInstance()
                .get(
                    Calendar.HOUR_OF_DAY
                )
        val startMinute =
            if (timePicked) currentDateTime.get(Calendar.MINUTE) else Calendar.getInstance().get(
                Calendar.MINUTE
            )

        val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
            currentDateTime.set(year, month, day, hour, minute, 0)
            currentDateTime.set(Calendar.MILLISECOND, 0)
            timePicked = true
            val sdf = SimpleDateFormat(getPattern())
            binding.dateTimeText.text = sdf.format(currentDateTime.timeInMillis)
            binding.dateTimeLayout.visibility = View.VISIBLE
            viewModel.dateTimeText = binding.dateTimeText.text.toString()
            viewModel.timeInMillis = currentDateTime.timeInMillis
        }, startHour, startMinute, false)
        timePickerDialog.setButton(TimePickerDialog.BUTTON_NEUTRAL, "Clear") { dialogInterface, _ ->
            currentDateTime.set(year, month, day, 23, 59, 0)
            currentDateTime.set(Calendar.MILLISECOND, 0)
            timePicked = false
            dialogInterface.dismiss()
            val sdf = SimpleDateFormat(getPattern())
            binding.dateTimeText.text = sdf.format(currentDateTime.timeInMillis)
            binding.dateTimeLayout.visibility = View.VISIBLE
            viewModel.dateTimeText = binding.dateTimeText.text.toString()
            viewModel.timeInMillis = currentDateTime.timeInMillis
        }
        timePickerDialog.show()
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue_1))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_blue_1))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
    }

    private fun getPattern(): String {
        return if (!timePicked)
            when {
                currentDateTime.get(Calendar.YEAR) == Calendar.getInstance()
                    .get(Calendar.YEAR) -> "E, dd MMM"
                else -> "E, dd MMM yyyy"
            }
        else
            when {
                currentDateTime.get(Calendar.YEAR) == Calendar.getInstance()
                    .get(Calendar.YEAR) -> "E, dd MMM, hh:mm a"
                else -> "E, dd MMM yyyy, hh:mm a"
            }
    }
}