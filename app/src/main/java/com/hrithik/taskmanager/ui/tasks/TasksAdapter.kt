package com.hrithik.taskmanager.ui.tasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hrithik.taskmanager.data.Tasks
import com.hrithik.taskmanager.databinding.ItemTaskSortedBinding
import com.hrithik.taskmanager.databinding.ItemTaskUnsortedBinding
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(var sorted: Boolean, private val listener: OnItemClickListener) :
    ListAdapter<Tasks, RecyclerView.ViewHolder>(DiffCallback()) {

    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding =
            ItemTaskUnsortedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnsortedViewHolder(binding)

        /*return if (sorted) {
            val binding =
                ItemTaskSortedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SortedViewHolder(binding)
        } else {
            val binding =
                ItemTaskUnsortedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UnsortedViewHolder(binding)
        }*/
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (holder.javaClass == UnsortedViewHolder::class.java) {
            val viewHolder = holder as UnsortedViewHolder
            viewHolder.bind(currentItem)
        } else {
            val viewHolder = holder as SortedViewHolder
            val showDate: Boolean = if (position > 0) {
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
                        val binding =
                            ItemTaskSortedBinding.bind(mRecyclerView.getChildAt(position + 1))
                        binding.title.visibility = View.GONE
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
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(calendar.time)
    }

    inner class UnsortedViewHolder(private val binding: ItemTaskUnsortedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val tasks = getItem(position)
                        listener.onItemClick(tasks)
                    }
                }
                radioBtn.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val tasks = getItem(position)
                        listener.onCompletedClicked(tasks, !tasks.completed)
                    }
                }
            }
        }

        fun bind(tasks: Tasks) {
            binding.apply {
                task.text = tasks.task
                dateTime.text = tasks.dateTime
                radioBtn.isChecked = tasks.completed
                if (!tasks.completed)
                    radioBtn.isChecked = false
                if (radioBtn.isChecked) {
                    dateTime.visibility = View.GONE
                    task.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    task.paintFlags = 0
                    dateTime.visibility = View.VISIBLE
                }
                if (tasks.dateTime.isEmpty())
                    dateTime.visibility = View.GONE
            }
        }
    }

    inner class SortedViewHolder(private val binding: ItemTaskSortedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val tasks = getItem(position)
                        listener.onItemClick(tasks)
                    }
                }
                radioBtn.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val tasks = getItem(position)
                        listener.onCompletedClicked(tasks, radioBtn.isChecked)
                    }
                }
            }
        }

        fun bind(tasks: Tasks, showDate: Boolean, date: String) {
            binding.apply {
                itemText.text = tasks.task
                title.visibility = if (showDate) View.VISIBLE else View.GONE
                title.text = date
                radioBtn.isChecked = tasks.completed
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(tasks: Tasks)
        fun onCompletedClicked(tasks: Tasks, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Tasks>() {
        override fun areItemsTheSame(oldItem: Tasks, newItem: Tasks): Boolean =
            (oldItem.id == newItem.id)

        override fun areContentsTheSame(oldItem: Tasks, newItem: Tasks): Boolean =
            (oldItem == newItem)
    }
}