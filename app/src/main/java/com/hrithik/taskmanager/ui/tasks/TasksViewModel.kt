package com.hrithik.taskmanager.ui.tasks

import android.content.Context
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.hrithik.taskmanager.data.PreferencesManager
import com.hrithik.taskmanager.data.SortOrder
import com.hrithik.taskmanager.data.Tasks
import com.hrithik.taskmanager.data.firebase.FirebaseDatabase
import com.hrithik.taskmanager.data.room.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
) : ViewModel() {

    val database = FirebaseDatabase(FirebaseAuth.getInstance().currentUser!!.uid)

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

    fun addAllTasksToRoom() {
        database.addAllTasksToRoom(taskDao)
    }

    fun onItemClicked(tasks: Tasks) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.OpenEditTaskBottomSheet(tasks))
    }

    fun onTaskCompleted(tasks: Tasks, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(tasks.copy(completed = isChecked))
        database.insert(tasks.copy(completed = isChecked))
    }

    fun onTaskSwiped(tasks: Tasks) = viewModelScope.launch {
        taskDao.delete(tasks)
        database.delete(tasks)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteMessage(tasks))
    }

    fun onUndoDeleteClicked(tasks: Tasks) = viewModelScope.launch {
        taskDao.insert(tasks)
        database.insert(tasks)
    }

    fun onAddNewTask() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.OpenAddTaskBottomSheet)
    }

    fun onDeleteAllCompletedClicked() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }

    fun onSortByClicked() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToSortByScreen(preferencesFLow.first().sortOrder))
    }

    fun onLogOutClicked(context: Context) {
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        viewModelScope.launch {
            taskDao.clearTable()
            tasksEventChannel.send(TasksEvent.NavigateToSignInScreen)
        }
    }

    sealed class TasksEvent {
        object OpenAddTaskBottomSheet : TasksEvent()
        data class OpenEditTaskBottomSheet(val tasks: Tasks) : TasksEvent()
        data class ShowUndoDeleteMessage(val tasks: Tasks) : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()
        data class NavigateToSortByScreen(val sortOrder: SortOrder) : TasksEvent()
        object NavigateToSignInScreen : TasksEvent()
    }

}