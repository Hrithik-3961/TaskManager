package com.hrithik.taskmanager.data.room

import androidx.room.*
import com.hrithik.taskmanager.data.SortOrder
import com.hrithik.taskmanager.data.Tasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Tasks)

    @Update
    suspend fun update(note: Tasks)

    @Delete
    suspend fun delete(note: Tasks)

    @Query("DELETE FROM Tasks WHERE completed == 1")
    suspend fun deleteCompletedTasks()

    @Query("DELETE FROM Tasks")
    suspend fun clearTable()

    @Query("SELECT * FROM Tasks WHERE task LIKE '%' || :searchQuery || '%' ORDER BY created")
    fun getTasksSortedByTimeAdded(searchQuery: String): Flow<List<Tasks>>

    @Query("SELECT * FROM Tasks WHERE task LIKE '%' || :searchQuery || '%' ORDER BY dateTime == '', timeInMillis ")
    fun getTasksSortedByDueDate(searchQuery: String): Flow<List<Tasks>>

    @Query("SELECT * FROM Tasks WHERE completed == 0 ORDER BY created")
    fun getTasksSortedByTimeAddedForWidget(): Flow<List<Tasks>>

    @Query("SELECT * FROM Tasks WHERE completed == 0 ORDER BY dateTime == '', timeInMillis ")
    fun getTasksSortedByDueDateForWidget(): Flow<List<Tasks>>

    fun getTasks(query: String, sortOrder: SortOrder): Flow<List<Tasks>> =
        when (sortOrder) {
            SortOrder.BY_TIME_ADDED -> getTasksSortedByTimeAdded(query)
            SortOrder.BY_DUE_DATE -> getTasksSortedByDueDate(query)
        }

    fun getTasks(sortOrder: SortOrder) =
        when (sortOrder) {
            SortOrder.BY_TIME_ADDED -> getTasksSortedByTimeAddedForWidget()
            SortOrder.BY_DUE_DATE -> getTasksSortedByDueDateForWidget()
        }

}