package com.hrithik.taskmanager.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.hrithik.taskmanager.data.PreferencesManager
import com.hrithik.taskmanager.data.SortOrder
import com.hrithik.taskmanager.data.TaskDao
import com.hrithik.taskmanager.data.Tasks
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFLow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val taskFlow = combine(searchQuery.asFlow(), preferencesFLow) { query, preferences ->
        Pair(query, preferences)
    }.flatMapLatest { (query, preferences) ->
        taskDao.getTasks(query, preferences.sortOrder)
    }
    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onItemClicked(tasks: Tasks) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.OpenEditTaskBottomSheet(tasks))
    }

    fun onTaskCompleted(tasks: Tasks, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(tasks.copy(completed = isChecked))
    }

    fun onTaskSwiped(tasks: Tasks) = viewModelScope.launch {
        taskDao.delete(tasks)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteMessage(tasks))
    }

    fun onUndoDeleteClicked(tasks: Tasks) = viewModelScope.launch {
        taskDao.insert(tasks)
    }

    fun onAddNewTask() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.OpenAddTaskBottomSheet)
    }

    sealed class TasksEvent {
        object OpenAddTaskBottomSheet : TasksEvent()
        data class OpenEditTaskBottomSheet(val tasks: Tasks) : TasksEvent()
        data class ShowUndoDeleteMessage(val tasks: Tasks) : TasksEvent()
    }

}