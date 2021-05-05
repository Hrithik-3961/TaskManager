package com.hrithik.taskmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_dialog.view.*
import kotlinx.android.synthetic.main.item_task_sorted.view.*
import kotlinx.android.synthetic.main.item_task_unsorted.view.*
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(private var context: Context, var arrayList: ArrayList<Tasks>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val prefs = context.getSharedPreferences("Preferences", AppCompatActivity.MODE_PRIVATE)
        val sorted = prefs.getBoolean("sorted", false)
        return if (sorted) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_task_sorted, parent, false)
            SortedViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_task_unsorted, parent, false)
            UnsortedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = arrayList[position]
        if (holder.javaClass == UnsortedViewHolder::class.java) {
            val viewHolder = holder as UnsortedViewHolder
            viewHolder.task.text = task.task
            viewHolder.dateTime.text = task.dateTime
            if (task.dateTime.isEmpty())
                viewHolder.dateTime.visibility = View.GONE
        } else {
            val viewHolder = holder as SortedViewHolder
            if (position > 0) {
                val previousTask = arrayList[position - 1]
                if (getDueDate(task.date, task.dateTime) != getDueDate(
                        previousTask.date,
                        previousTask.dateTime
                    )
                ) {
                    viewHolder.date.visibility = View.VISIBLE
                    viewHolder.date.text = getDueDate(task.date, task.dateTime)
                } else
                    viewHolder.date.visibility = View.GONE

            } else {
                viewHolder.date.visibility = View.VISIBLE
                viewHolder.date.text = getDueDate(task.date, task.dateTime)
            }
            if (position < arrayList.size - 1 && arrayList.size > 1) {
                val nextTask = arrayList[position + 1]
                if (getDueDate(task.date, task.dateTime) == getDueDate(
                        nextTask.date,
                        nextTask.dateTime
                    )
                )
                    mRecyclerView.post {
                        mRecyclerView.getChildAt(position + 1).title.visibility = View.GONE
                    }
            }
            viewHolder.task.text = task.task
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemId(position: Int): Long = position.toLong()

    private fun getDueDate(date: Date, dateTime: String): String {
        if (dateTime.isEmpty())
            return "No Due Date"

        val calendar = Calendar.getInstance()
        calendar.time = date
        val pattern = if (calendar.get(Calendar.YEAR) == Calendar.getInstance()
                .get(Calendar.YEAR)
        ) "E, dd MMM" else "E, dd MMM yyyy"
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(date)
    }

    inner class UnsortedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.task
        val dateTime: TextView = itemView.dateTime
        val radioBtn: RadioButton = itemView.radio_btn
    }

    inner class SortedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.title
        val task: TextView = itemView.itemText
        val radioBtn: RadioButton = itemView.radioBtn
    }
}