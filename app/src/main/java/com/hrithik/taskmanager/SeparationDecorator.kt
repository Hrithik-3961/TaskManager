package com.hrithik.taskmanager

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class SeparationDecorator(private var context: Context) : RecyclerView.ItemDecoration() {

    private val textSize = 50F
    private val groupSpacing = 100
    private val paint = Paint()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        paint.textSize = textSize
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> paint.color =
                ContextCompat.getColor(context, R.color.light_grey)
            Configuration.UI_MODE_NIGHT_NO -> paint.color =
                ContextCompat.getColor(context, R.color.dark_grey)
        }

        val adapter = parent.adapter as TasksAdapter
        val arrayList = adapter.arrayList

        if (arrayList.size > 0) {
            val view = parent.getChildAt(0)
            val pos = parent.getChildAdapterPosition(view)
            val text: String = if (arrayList[pos].dateTime.isEmpty()) "No Due Date" else {
                val date = arrayList[pos].date
                val sdf = SimpleDateFormat(getPattern(date))
                sdf.format(date)
            }

            c.drawText(
                text,
                parent.getChildAt(pos).left.toFloat(),
                parent.getChildAt(pos).top - groupSpacing / 2 + textSize / 3,
                paint
            )
        }
        if (arrayList.size > 1) {
            var i = 1
            while (i < parent.childCount) {
                val view = parent.getChildAt(i)
                val pos1 = parent.getChildAdapterPosition(view)
                val task1 = arrayList[pos1 - 1]
                val task2 = arrayList[pos1]
                var text1: String
                var text2: String

                val date1 = task1.date
                val date2 = task2.date
                val pattern1 = getPattern(date1)
                val pattern2 = getPattern(date2)
                var sdf = SimpleDateFormat(pattern1)
                text1 = sdf.format(date1)
                sdf = SimpleDateFormat(pattern2)
                text2 = sdf.format(date2)
                if (task1.dateTime.isEmpty())
                    text1 = "No Due Date"
                if (task2.dateTime.isEmpty())
                    text2 = "No Due Date"

                if (text1 != text2) {
                    c.drawText(
                        text2, parent.getChildAt(pos1).left.toFloat(),
                        parent.getChildAt(pos1).top - groupSpacing / 2 + textSize / 3, paint
                    )
                }
                i++
            }
        }

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        outRect.set(50, groupSpacing, 0, 0)
    }

    private fun getPattern(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return if (calendar.get(Calendar.YEAR) == Calendar.getInstance()
                .get(Calendar.YEAR)
        ) "E, dd MMM" else "E, dd MMM yyyy"
    }

}