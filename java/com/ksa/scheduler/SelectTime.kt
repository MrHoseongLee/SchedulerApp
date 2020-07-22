package com.ksa.scheduler

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.select_time_menu.*

class SelectTime: AppCompatActivity() {

    private lateinit var rvAdapter: SimpleWeeklyViewRVAdapter
    var selectedItemPos = 0

    private var id: Int = 0
    private var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_time_menu)
        setSupportActionBar(findViewById(R.id.select_time_toolbar))

        id = intent.extras!!.getInt("id")
        pos = intent.extras!!.getInt("pos")

        supportActionBar?.title = "${MainActivity.currentDate.year} / ${MainActivity.currentDate.month}"

        select_time_mainWrapper.translationY = MainActivity.statusBarHeight.toFloat()

        initRecyclerView()
        select_time_calendarRecyclerView.layoutManager?.scrollToPosition(1022)

        val width: Int = android.content.res.Resources.getSystem().displayMetrics.widthPixels / 8
        val density: Float = applicationContext.resources.displayMetrics.densityDpi.toFloat() / 160f
        val xdpi: Float = applicationContext.resources.displayMetrics.xdpi / 25.4f

        select_time_times.layoutParams.height = (12 * (MainActivity.dataManager.totalTimeInDay() + 1.5f) * xdpi +
                30f * density).toInt() + MainActivity.navigationBarHeight

        select_time_calendarRecyclerView.layoutParams.height = (12 * (MainActivity.dataManager.totalTimeInDay() + 1.5f) * xdpi +
                30f * density).toInt() + MainActivity.navigationBarHeight

        for (i in (MainActivity.dataManager.startTime.hour+1)..MainActivity.dataManager.endTime.hour){
            val text = TextView(ContextThemeWrapper(applicationContext, R.style.TimeText))
            text.width = width
            text.height = (12f * xdpi).toInt()
            text.text = "$i"
            text.translationY = 6f * xdpi
            select_time_times.addView(text)
        }

        val button = Button(applicationContext)
        button.height = width
        button.width = width
        select_temp.addView(button)

        select_time_calendarRecyclerView.setOnTouchListener{ v, event ->
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                val x = ((event.x.toInt() / width) * width).toFloat()
                val y = event.y
                button.translationX = x
                button.translationY = y
            }
            v.performClick()
            true
        }
    }

    private fun initRecyclerView() {
        select_time_calendarRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SelectTime, LinearLayoutManager.HORIZONTAL
                ,false)
            rvAdapter = SimpleWeeklyViewRVAdapter()
            adapter = rvAdapter
        }
        val snapHelper = WeeklyViewSnapHelper(1)
        snapHelper.attachToRecyclerView(select_time_calendarRecyclerView)
        snapHelper.setSnapBlockCallback(object : WeeklyViewSnapHelper.SnapBlockCallback {
            override fun onBlockSnap(snapPosition: Int) {
                if (selectedItemPos == snapPosition)
                    return
                selectedItemPos = snapPosition
                val nowDate = MainActivity.currentDate + (selectedItemPos - MainActivity.today)
                supportActionBar?.title = "${nowDate.year} / ${nowDate.month}"
            }

        })
    }

}
