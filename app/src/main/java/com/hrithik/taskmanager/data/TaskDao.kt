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

    @Query("SELECT * FROM Tasks WHERE task LIKE '%' || :searchQuery || '%' ORDER BY created")
    fun getTasksSortedByTimeAdded(searchQuery: String): Flow<List<Tasks>>

    @Query("SELECT * FROM Tasks WHERE task LIKE '%' || :searchQuery || '%' ORDER BY dateTime == '', timeInMillis ")
    fun getTasksSortedByDueDate(searchQuery: String): Flow<List<Tasks>>

    fun getTasks(query: String, sortOrder: SortOrder): Flow<List<Tasks>> =
        when (sortOrder) {
            SortOrder.BY_TIME_ADDED -> getTasksSortedByTimeAdded(query)
            SortOrder.BY_DUE_DATE -> getTasksSortedByDueDate(query)
        }

    @Query("DELETE FROM Tasks WHERE completed == 1")
    suspend fun deleteCompletedTasks()
}