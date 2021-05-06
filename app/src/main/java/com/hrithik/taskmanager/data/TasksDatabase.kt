package com.hrithik.taskmanager.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Tasks::class], version = 1)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

}