package com.hrithik.taskmanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG1 = "BottomSheetDialogFragment"
    }

    private val currentDateTime = Calendar.getInstance()
    private var timePicked = false
    private lateinit var mListener: BottomSheetListener

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

        mListener = context as BottomSheetListener
        setOnSaveClickListener(mListener)

        taskText.requestFocus()
        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(taskText, InputMethodManager.HIDE_IMPLICIT_ONLY)

        taskText.background = null

        taskText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                saveBtn.isEnabled = s.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        calendarBtn.setOnClickListener { pickDate() }
        clockBtn.setOnClickListener { pickTime() }
        dateTimeText.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP ->
                        if (event.rawX >= dateTimeText.right - dateTimeText.totalPaddingRight)
                            dateTimeText.visibility = View.GONE
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        saveBtn.setOnClickListener {
            val task =
                Tasks(taskText.text.toString(), dateTimeText.text.toString(), currentDateTime.time)
            mListener.onSaveClick(task)
            dismiss()
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
            dateTimeText.text = sdf.format(currentDateTime.time)
            dateTimeText.visibility = View.VISIBLE
        }, startYear, startMonth, startDay)
        val datePicker = datePickerDialog.datePicker
        datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.light_blue_1))
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.dark_blue_1))
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.black))
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
            dateTimeText.text = sdf.format(currentDateTime.time)
            dateTimeText.visibility = View.VISIBLE
        }, startHour, startMinute, false)
        timePickerDialog.setButton(TimePickerDialog.BUTTON_NEUTRAL, "Clear") { dialogInterface, _ ->
            currentDateTime.set(year, month, day, 23, 59, 0)
            currentDateTime.set(Calendar.MILLISECOND, 0)
            timePicked = false
            dialogInterface.dismiss()
            val sdf = SimpleDateFormat(getPattern())
            dateTimeText.text = sdf.format(currentDateTime.time)
            dateTimeText.visibility = View.VISIBLE
        }
        timePickerDialog.show()
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.light_blue_1))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.dark_blue_1))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(context!!, R.color.black))
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

    interface BottomSheetListener {
        fun onSaveClick(task: Tasks)
    }

    private fun setOnSaveClickListener(listener: BottomSheetListener) {
        mListener = listener
    }

}