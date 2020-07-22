package com.ksa.scheduler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.show_all_assignments.*

class ShowAllAssignments: AppCompatActivity() {

    private lateinit var rvAdapter: AssignmentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_all_assignments)

        showAllAssignmentWrapper.translationY = MainActivity.statusBarHeight.toFloat()
        showAllAssignmentsRV.apply {
            layoutManager = LinearLayoutManager(this@ShowAllAssignments, LinearLayoutManager.VERTICAL
                ,false)
            rvAdapter = AssignmentRVAdapter()
            adapter = rvAdapter
        }
        rvAdapter.setCurrentActivity(this)
        rvAdapter.loadData()
    }

    override fun onRestart() {
        super.onRestart()
        rvAdapter.notifyDataSetChanged()
    }

}
