package com.hrithik.taskmanager.ui.tasks

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
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
import com.hrithik.taskmanager.databinding.ItemDialogBinding
import com.hrithik.taskmanager.databinding.SortByDialogBinding
import com.hrithik.taskmanager.util.exhaustive
import com.hrithik.taskmanager.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
                when (item.itemId) {
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
                    }

                    R.id.sortBy -> {
                        showSortByDialog()
                        return@setOnMenuItemClickListener true
                    }

                    R.id.deleteAllCompleted -> {
                        viewModel.onDeleteAllCompletedClicked()
                    }
                }
                false
            }

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.preferencesFLow.collect {
                    adapter.sorted = it.sortOrder == SortOrder.BY_DUE_DATE
                }
            }

            viewModel.tasks.observe(viewLifecycleOwner) {
                adapter.submitList(it)
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
                        val action = HomeFragmentDirections.actionHomeFragmentToBottomSheetDialog()
                        findNavController().navigate(action)
                    }

                }.exhaustive
            }
        }
    }


    private fun showSortByDialog() {

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.sort_by_dialog, null)
        dialog.setContentView(view)

        val binding = SortByDialogBinding.bind(view)

        val layout = binding.linear
        binding.title.text = resources.getString(R.string.sort_by)

        val timeAdded = layoutInflater.inflate(R.layout.item_dialog, null)
        val dueDate = layoutInflater.inflate(R.layout.item_dialog, null)

        val timeAddedBinding = ItemDialogBinding.bind(timeAdded)
        val dueDateBinding = ItemDialogBinding.bind(dueDate)

        timeAddedBinding.itemText.text = resources.getString(R.string.time_added)
        dueDateBinding.itemText.text = resources.getString(R.string.due_date)
        timeAddedBinding.radioBtn.isClickable = false
        dueDateBinding.radioBtn.isClickable = false
        layout.addView(timeAdded, params)
        layout.addView(dueDate, params)

        viewLifecycleOwner.lifecycleScope.launch {
            if (viewModel.preferencesFLow.first().sortOrder == SortOrder.BY_TIME_ADDED)
                timeAddedBinding.radioBtn.isChecked = true
            else
                dueDateBinding.radioBtn.isChecked = true
        }

        timeAdded.setOnClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_TIME_ADDED)
            dialog.dismiss()
        }

        dueDate.setOnClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_DUE_DATE)
            dialog.dismiss()
        }

        dialog.show()
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