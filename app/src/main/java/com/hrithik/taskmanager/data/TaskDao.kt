package com.hrithik.taskmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Tasks)

    @Update
    suspend fun update(note: Tasks)

    @Delete
    suspend fun delete(note: Tasks)

    @Query("SELECT * FROM Tasks")
    fun getAllTasks(): Flow<List<Tasks>>

}