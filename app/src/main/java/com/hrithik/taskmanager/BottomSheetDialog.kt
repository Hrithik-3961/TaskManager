package com.hrithik.taskmanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.util.*


class BottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG1 = "BottomSheetDialogFragment"
        const val TAG2 = "MaterialDatePicker"
    }

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskText.requestFocus()
        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(taskText, InputMethodManager.HIDE_IMPLICIT_ONLY)

        taskText.background = null

        var currentDateTime = Calendar.getInstance()

        calendarBtn.setOnClickListener {
            currentDateTime = pickDate(currentDateTime)
            Log.d("tag1", currentDateTime.time.toString())
        }
        clockBtn.setOnClickListener {
            currentDateTime = pickTime(currentDateTime)
            Log.d("tag1", currentDateTime.time.toString())
        }
    }

    private fun pickDate(currentDateTime: Calendar): Calendar {

        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            currentDateTime.set(year, month, day)
        }, startYear, startMonth, startDay).show()

        return currentDateTime
    }

    private fun pickTime(currentDateTime: Calendar): Calendar {
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, hour, minute ->
            currentDateTime.set(
                currentDateTime.get(Calendar.YEAR),
                currentDateTime.get(Calendar.MONTH),
                currentDateTime.get(Calendar.DAY_OF_MONTH),
                hour, minute
            )
        }, startHour, startMinute, false).show()

        return currentDateTime
    }

}