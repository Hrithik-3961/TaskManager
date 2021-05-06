package com.hrithik.taskmanager.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Tasks(
    val task: String,
    val dateTime: String,
    val timeInMillis: Long,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable