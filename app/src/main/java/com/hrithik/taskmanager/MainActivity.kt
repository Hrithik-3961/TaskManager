package com.hrithik.taskmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomSheetDialog.BottomSheetListener {

    var arrayList = ArrayList<Tasks>()
    private lateinit var adapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            BottomSheetDialog().apply { show(supportFragmentManager, BottomSheetDialog.TAG1) }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = TasksAdapter(this, arrayList)
        recyclerView.adapter = adapter
    }

    override fun onSaveClick(task: Tasks) {
        arrayList.add(task)
        adapter.notifyDataSetChanged()
    }
}