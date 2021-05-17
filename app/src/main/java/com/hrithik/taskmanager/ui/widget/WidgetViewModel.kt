package com.hrithik.taskmanager.ui.widget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hrithik.taskmanager.data.PreferencesManager
import com.hrithik.taskmanager.data.room.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
) : ViewModel() {

    private val preferencesFLow = preferencesManager.preferencesFlow

    private val taskFlow = preferencesFLow.flatMapLatest { preferences ->
        taskDao.getTasks(preferences.sortOrder)
    }

    val tasks = taskFlow.asLiveData()
}