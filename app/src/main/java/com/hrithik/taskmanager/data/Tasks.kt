package com.hrithik.taskmanager.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Tasks(
    val task: String,
    val dateTime: String,
    val timeInMillis: Long,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {

    constructor() : this("", "", 0L)//required for Firebase
}