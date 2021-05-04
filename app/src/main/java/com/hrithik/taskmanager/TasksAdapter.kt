package com.hrithik.taskmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(var context: Context, var arrayList: ArrayList<Tasks>): RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task: Tasks = arrayList[position]
        holder.task.text = task.task
        holder.dateTime.text = task.dateTime
        if(task.dateTime.isEmpty())
            holder.dateTime.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var task: TextView = itemView.findViewById(R.id.task)
        var dateTime: TextView = itemView.findViewById(R.id.dateTime)

    }

}