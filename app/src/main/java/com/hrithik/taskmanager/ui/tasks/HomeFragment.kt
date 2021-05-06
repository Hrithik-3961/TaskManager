package com.hrithik.taskmanager.ui.tasks

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrithik.taskmanager.BottomSheetDialog
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.Tasks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_dialog.view.*
import kotlinx.android.synthetic.main.item_task_sorted.*
import kotlinx.android.synthetic.main.sort_by_dialog.view.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), BottomSheetDialog.BottomSheetListener {

    private var unsortedList = ArrayList<Tasks>()
    private var sortedList = ArrayList<Tasks>()
    private lateinit var adapter: TasksAdapter
    private var sorted = false
    private lateinit var prefs: SharedPreferences
    private val viewModel: TasksViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireContext().getSharedPreferences("Preferences", AppCompatActivity.MODE_PRIVATE)
        sorted = prefs.getBoolean("sorted", false)

        fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetDialog()
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

        }

        toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sortBy -> {
                    showSortByDialog()
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        adapter = if (sorted) {
            TasksAdapter(requireContext())
        } else
            TasksAdapter(requireContext())
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

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
                while (pos < sortedList.size && sortedList[pos].dateTime.isNotEmpty() && sortedList[pos].timeInMillis <= task.timeInMillis)
                    pos++
            sortedList.add(pos, task)
        }
        if (!sorted)
            adapter.notifyItemInserted(unsortedList.size)
        else {
            adapter.notifyItemInserted(pos)
        }
    }

    private fun showSortByDialog() {

        sorted = prefs.getBoolean("sorted", false)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val dialog = Dialog(requireContext())
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
                adapter = TasksAdapter(requireContext())
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        dueDate.radioBtn.setOnClickListener {
            prefs.edit().putBoolean("sorted", true).apply()
            if (!sorted) {
                adapter = TasksAdapter(requireContext())
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        timeAdded.itemText.setOnClickListener {
            prefs.edit().putBoolean("sorted", false).apply()
            if (sorted) {
                adapter = TasksAdapter(requireContext())
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        dueDate.itemText.setOnClickListener {
            prefs.edit().putBoolean("sorted", true).apply()
            if (!sorted) {
                adapter = TasksAdapter(requireContext())
                recyclerView.adapter = adapter
            }
            dialog.dismiss()
        }

        dialog.show()
    }

}