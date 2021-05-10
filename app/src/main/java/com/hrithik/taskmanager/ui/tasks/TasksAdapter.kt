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

class TasksAdapter(var sorted: Boolean, private val listener: OnItemClickListener) :
    ListAdapter<Tasks, RecyclerView.ViewHolder>(DiffCallback()) {

    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

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
            viewHolder.bind(currentItem)
        }
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
                        listener.onCompletedClicked(tasks, !tasks.completed)
                    }
                }
            }
        }

        fun bind(tasks: Tasks) {
            binding.apply {
                itemText.text = tasks.task
                if (tasks.timeInMillis == 0L) {
                    radioBtn.visibility = View.GONE
                    root.setOnClickListener(null)
                }
                radioBtn.isChecked = tasks.completed
                if (!tasks.completed)
                    radioBtn.isChecked = false
                if (radioBtn.isChecked)
                    itemText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                else
                    itemText.paintFlags = 0

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