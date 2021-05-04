package com.hrithik.taskmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_dialog.view.*
import kotlinx.android.synthetic.main.item_task_unsorted.view.*

class TasksAdapter(private var context: Context, var arrayList: ArrayList<Tasks>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val prefs = context.getSharedPreferences("Preferences", AppCompatActivity.MODE_PRIVATE)
        val sorted = prefs.getBoolean("sorted", false)
        return if (sorted) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_dialog, parent, false)
            SortedViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_task_unsorted, parent, false)
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
            viewHolder.task.text = task.task
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class UnsortedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.task
        val dateTime: TextView = itemView.dateTime
    }

    inner class SortedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.itemText
    }
}