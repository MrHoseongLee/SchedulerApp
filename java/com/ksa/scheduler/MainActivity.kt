package com.ksa.scheduler

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var rvAdapter: ThreeDayViewRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext
        val calendar: Calendar = Calendar.getInstance()
        currentDate = Date(calendar.get(Calendar.YEAR)
            ,calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
        initRecyclerView()
        DataManager.saveData()
        loadData()
        recyclerView.layoutManager?.scrollToPosition(1023)
        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(recyclerView)
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL
            ,false)
            rvAdapter = ThreeDayViewRVAdapter()
            adapter = rvAdapter
        }
    }

    private fun loadData() {
        rvAdapter.loadData()
    }

    companion object {
        lateinit var appContext: Context
        lateinit var currentDate: Date
    }

}
