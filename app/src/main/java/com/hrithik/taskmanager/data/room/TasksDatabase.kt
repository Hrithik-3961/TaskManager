package com.hrithik.taskmanager.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hrithik.taskmanager.data.Tasks

@Database(entities = [Tasks::class], version = 1)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

}