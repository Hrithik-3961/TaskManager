package com.hrithik.taskmanager.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.hrithik.taskmanager.data.Tasks
import com.hrithik.taskmanager.data.room.TaskDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FirebaseDatabase(private val uid: String) {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(uid)

    fun insert(task: Tasks) {
        collection.document(task.id.toString()).set(task)
    }

    fun delete(task: Tasks) {
        collection.document(task.id.toString()).delete()
    }

    fun addAllTasksToRoom(taskDao: TaskDao) {
        collection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    GlobalScope.launch {
                        taskDao.insert(document.toObject(Tasks::class.java))
                    }
                }

            }
    }

}