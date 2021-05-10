package com.hrithik.taskmanager.ui.addEditTask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrithik.taskmanager.data.TaskDao
import com.hrithik.taskmanager.data.Tasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Tasks>("task")

    var taskName = state.get<String>("taskName") ?: task?.task ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var dateTimeText = state.get<String>("dateTime") ?: task?.dateTime ?: ""
        set(value) {
            field = value
            state.set("dateTime", value)
        }

    var timeInMillis =
        state.get<Long>("timeInMillis") ?: task?.timeInMillis ?: System.currentTimeMillis()
        set(value) {
            field = value
            state.set("timeInMillis", value)
        }

    fun onSaveClicked() {
        if (task != null) {
            val updatedTask = task.copy(
                task = taskName,
                dateTime = dateTimeText,
                timeInMillis = timeInMillis,
                completed = task.completed
            )
            updateTask(updatedTask)
        } else {
            val newTask =
                Tasks(task = taskName, dateTime = dateTimeText, timeInMillis = timeInMillis)
            createTask(newTask)
        }
    }


    private fun createTask(newTask: Tasks) = viewModelScope.launch {
        taskDao.insert(newTask)
    }

    private fun updateTask(updatedTask: Tasks) = viewModelScope.launch {
        taskDao.update(updatedTask)
    }


}
