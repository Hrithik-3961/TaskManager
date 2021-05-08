package com.hrithik.taskmanager.ui.dialog

import androidx.hilt.lifecycle.ViewModelInject
import com.hrithik.taskmanager.data.TaskDao


class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
)