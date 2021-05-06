package com.hrithik.taskmanager.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hrithik.taskmanager.data.Tasks
import com.hrithik.taskmanager.databinding.ItemTaskSortedBinding
import com.hrithik.taskmanager.databinding.ItemTaskUnsortedBinding
import kotlinx.android.synthetic.main.item_task_sorted.view.*
import java.text.DateFormat
import java.util.*

class TasksAdapter(private var context: Context) :
    ListAdapter<Tasks, RecyclerView.ViewHolder>(DiffCallback()) {

    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val prefs = context.getSharedPreferences("Preferences", AppCompatActivity.MODE_PRIVATE)
        val sorted = prefs.getBoolean("sorted", false)
        return if (sorted) {
            val binding =
                ItemTaskSortedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SortedViewHolder(binding)
        } else {
            val binding =
                ItemTaskUnsortedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UnsortedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (holder.javaClass == UnsortedViewHolder::class.java) {
            val viewHolder = holder as UnsortedViewHolder
            viewHolder.bind(currentItem)
        } else {
            val viewHolder = holder as SortedViewHolder
            val showDate = if (position > 0) {
                val previousTask = getItem(position - 1)
                getDueDate(currentItem.timeInMillis, currentItem.dateTime) != getDueDate(
                    previousTask.timeInMillis,
                    previousTask.dateTime
                )

            } else
                true

            if (position < itemCount - 1 && itemCount > 1) {
                val nextTask = getItem(position + 1)
                if (getDueDate(currentItem.timeInMillis, currentItem.dateTime) == getDueDate(
                        nextTask.timeInMillis,
                        nextTask.dateTime
                    )
                )
                    mRecyclerView.post {
                        mRecyclerView.getChildAt(position + 1).title.visibility = View.GONE
                    }
            }
            viewHolder.bind(
                currentItem,
                showDate,
                getDueDate(currentItem.timeInMillis, currentItem.dateTime)
            )
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    private fun getDueDate(timeInMillis: Long, dateTime: String): String {
        if (dateTime.isEmpty())
            return "No Due Date"

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val pattern = if (calendar.get(Calendar.YEAR) == Calendar.getInstance()
                .get(Calendar.YEAR)
        ) "E, dd MMM" else "E, dd MMM yyyy"
        val df = DateFormat.getDateInstance()
        df.parse(pattern)
        return df.format(calendar.time)
    }

    class UnsortedViewHolder(private val binding: ItemTaskUnsortedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tasks: Tasks) {
            binding.apply {
                task.text = tasks.task
                dateTime.text = tasks.dateTime
                if (tasks.dateTime.isEmpty())
                    dateTime.visibility = View.GONE
            }
        }
    }

    class SortedViewHolder(private val binding: ItemTaskSortedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tasks: Tasks, showDate: Boolean, date: String) {
            binding.apply {
                itemText.text = tasks.task
                title.visibility = if (showDate) View.VISIBLE else View.GONE
                title.text = date
            }
        }


    }

    class DiffCallback : DiffUtil.ItemCallback<Tasks>() {
        override fun areItemsTheSame(oldItem: Tasks, newItem: Tasks): Boolean =
            (oldItem.id == newItem.id)

        override fun areContentsTheSame(oldItem: Tasks, newItem: Tasks): Boolean =
            (oldItem == newItem)
    }
}