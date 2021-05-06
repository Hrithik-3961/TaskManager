package com.hrithik.taskmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hrithik.taskmanager.BottomSheetDialog
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.Tasks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BottomSheetDialog.BottomSheetListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onSaveClick(task: Tasks) {
    }

}