package com.hrithik.taskmanager

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_dialog.view.*
import kotlinx.android.synthetic.main.item_task_sorted.*
import kotlinx.android.synthetic.main.item_task_sorted.view.*

class MainActivity : AppCompatActivity(), BottomSheetDialog.BottomSheetListener {

    private var unsortedList = ArrayList<Tasks>()
    private var sortedList = ArrayList<Tasks>()
    private lateinit var adapter: TasksAdapter
    private var sorted = false
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("Preferences", MODE_PRIVATE)
        sorted = prefs.getBoolean("sorted", false)

        fab.setOnClickListener {
            BottomSheetDialog().apply { show(supportFragmentManager, BottomSheetDialog.TAG1) }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = if (sorted) {
            TasksAdapter(this, sortedList)
        } else
            TasksAdapter(this, unsortedList)
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }

    override fun onSaveClick(task: Tasks) {
        unsortedList.add(task)

        var pos = 0

        if (sortedList.isEmpty())
            sortedList.add(task)
        else {
            if (task.dateTime.isEmpty())
                pos = sortedList.size
            else
                while (pos < sortedList.size && sortedList[pos].dateTime.isNotEmpty() && sortedList[pos].date <= task.date)
                    pos++
            sortedList.add(pos, task)
        }
        if (!sorted)
            adapter.notifyItemInserted(unsortedList.size)
        else {
            adapter.notifyItemInserted(pos)
        }
    }

    fun showSortByDialog(item: MenuItem) {

        sorted = prefs.getBoolean("sorted", false)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.sort_by_dialog)
        val layout = dialog.linear
        layout.title.text = resources.getString(R.string.sort_by)

        val timeAdded = layoutInflater.inflate(R.layout.item_dialog, null)
        val dueDate = layoutInflater.inflate(R.layout.item_dialog, null)
        timeAdded.itemText.text = resources.getString(R.string.time_added)
        dueDate.itemText.text = resources.getString(R.string.due_date)
        layout.addView(timeAdded, params)
        layout.addView(dueDate, params)

        if (sorted) dueDate.radioBtn.isChecked = true else timeAdded.radioBtn.isChecked = true

        timeAdded.radioBtn.setOnClickListener {
            prefs.edit().putBoolean("sorted", false).apply()
            if (sorted) {
                adapter = TasksAdapter(this, unsortedList)
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        dueDate.radioBtn.setOnClickListener {
            prefs.edit().putBoolean("sorted", true).apply()
            if (!sorted) {
                adapter = TasksAdapter(this, sortedList)
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        timeAdded.itemText.setOnClickListener {
            prefs.edit().putBoolean("sorted", false).apply()
            if (sorted) {
                adapter = TasksAdapter(this, unsortedList)
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        dueDate.itemText.setOnClickListener {
            prefs.edit().putBoolean("sorted", true).apply()
            if (!sorted) {
                adapter = TasksAdapter(this, sortedList)
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        dialog.show()
    }

}