package com.hrithik.taskmanager.ui.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.SortOrder
import com.hrithik.taskmanager.data.Tasks
import com.hrithik.taskmanager.databinding.FragmentHomeBinding
import com.hrithik.taskmanager.util.exhaustive
import com.hrithik.taskmanager.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), TasksAdapter.OnItemClickListener {

    private lateinit var adapter: TasksAdapter
    private val viewModel: TasksViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TasksAdapter(false, this)
        val binding = FragmentHomeBinding.bind(view)
        binding.apply {
            fab.setOnClickListener {
                viewModel.onAddNewTask()
            }

            toolBar.setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
                    R.id.search -> {
                        searchView = item.actionView as SearchView
                        val pendingQuery = viewModel.searchQuery.value
                        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                            item.expandActionView()
                            searchView.setQuery(pendingQuery, false)
                        }
                        searchView.onQueryTextChanged {
                            viewModel.searchQuery.value = it
                        }
                        true
                    }

                    R.id.sortBy -> {
                        viewModel.onSortByClicked()
                        true
                    }

                    R.id.deleteAllCompleted -> {
                        viewModel.onDeleteAllCompletedClicked()
                        true
                    }
                    else -> false
                }
            }

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

            viewModel.tasks.observe(viewLifecycleOwner) { list ->
                run {
                    viewLifecycleOwner.lifecycleScope.launch {
                        if (viewModel.preferencesFLow.first().sortOrder == SortOrder.BY_DUE_DATE) {
                            adapter.submitList(updateList(list as ArrayList<Tasks>))
                            adapter.sorted = true
                            recyclerView.adapter = adapter
                        } else {
                            adapter.submitList(list)
                            adapter.sorted = false
                            recyclerView.adapter = adapter
                        }
                    }
                }
            }
        }


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val tasks = adapter.currentList[viewHolder.adapterPosition]
                viewModel.onTaskSwiped(tasks)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val tasks = adapter.currentList[viewHolder.adapterPosition]
                if (tasks.timeInMillis == 0L)
                    return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }).attachToRecyclerView(binding.recyclerView)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), "Task Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClicked(event.tasks)
                            }
                            .show()
                    }
                    is TasksViewModel.TasksEvent.OpenAddTaskBottomSheet -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToBottomSheetDialog()
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.OpenEditTaskBottomSheet -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToBottomSheetDialog(event.tasks)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action =
                            HomeFragmentDirections.actionGlobalDeleteAllCompleteDialogFragment()
                        findNavController().navigate(action)
                    }

                    is TasksViewModel.TasksEvent.NavigateToSortByScreen -> {
                        val action =
                            HomeFragmentDirections.actionGlobalSortByDialogFragment(event.sortOrder)
                        findNavController().navigate(action)
                    }

                }.exhaustive
            }
        }
    }

    private fun updateList(list: ArrayList<Tasks>): ArrayList<Tasks> {
        var i = 2
        val task = list[0]
        list.add(0, Tasks(getDueDate(task.timeInMillis, task.dateTime), "", 0L))
        while (i < list.size) {
            val previousTask = list[i - 1]
            val currentTask = list[i]
            if (getDueDate(previousTask.timeInMillis, previousTask.dateTime) != getDueDate(
                    currentTask.timeInMillis,
                    currentTask.dateTime
                )
            ) {
                list.add(
                    i,
                    Tasks(getDueDate(currentTask.timeInMillis, currentTask.dateTime), "", 0L)
                )
                i++
            }
            i++
        }
        return list
    }

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

    override fun onItemClick(tasks: Tasks) {
        viewModel.onItemClicked(tasks)
    }

    override fun onCompletedClicked(tasks: Tasks, isChecked: Boolean) {
        viewModel.onTaskCompleted(tasks, isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}