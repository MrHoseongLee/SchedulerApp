package com.ksa.scheduler

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.checking_dialog.view.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var rvAdapter: WeeklyViewRVAdapter
    var selectedItemPos = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        appContext = applicationContext
        activity = this

        val calendar: Calendar = Calendar.getInstance()
        currentDate = Date(calendar.get(Calendar.YEAR) ,calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH))

        val time = Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        setNavigationBarHeight()
        setStatusBarHeight()

        mainViewWrapper.translationY = statusBarHeight.toFloat()

        dataManager.setMainActivity(this)
        dataManager.loadData()

        for ((_, meeting) in dataManager.meetings) {
            if (meeting.date < currentDate || meeting.endTime < time) {
                dataManager.removeMeeting(meeting)
            }
        }

        var count = 0
        var count2 = 0

        for ((_, assignment) in dataManager.assignments) {
            for (subAssignment in assignment.subAssignments) {
                if (subAssignment.date <= currentDate && subAssignment.time + assignment.estimatedTime <= time) {
                    count += 1
                    val builder = AlertDialog.Builder(this, R.style.DialogBox)
                    var progress: Float
                    val dialogView =
                        View.inflate(applicationContext, R.layout.checking_dialog, null)
                    dialogView.nameOfTask.text = "${assignment.name}  ${subAssignment.date}  ${subAssignment.time}"
                    builder.setView(dialogView).setPositiveButton("Set") {
                            _, _ -> progress = dialogView.progress.progress / 100f
                        if (progress == 1f) {
                            dataManager.removeAssignment(assignment, false)
                        } else {
                            assignment.estimatedTime += -(assignment.estimatedTime.time * progress).toInt()
                        }
                        count2 += 1
                        if (count == count2) {
                            dataManager.schedule()
                        }
                    }.show()
                }
            }
        }

        today = 1022 + currentDate.getDayOfWeek()
        initRecyclerView()
        calendarRecyclerView.layoutManager?.scrollToPosition(1022)

        val buttonMenu = ButtonMenu(this)
        buttonMenu.init()

        supportActionBar?.title = "${currentDate.year} / ${currentDate.month}"

        val width: Int = android.content.res.Resources.getSystem().displayMetrics.widthPixels / 8
        val density: Float = applicationContext.resources.displayMetrics.densityDpi.toFloat() / 160f
        val xdpi: Float = applicationContext.resources.displayMetrics.xdpi / 25.4f

        times.layoutParams.height = (12 * (dataManager.totalTimeInDay() + 1.5f) * xdpi +
                30f * density).toInt() + navigationBarHeight

        calendarRecyclerView.layoutParams.height = (12 * (dataManager.totalTimeInDay() + 1.5f) * xdpi +
                30f * density).toInt() + navigationBarHeight

        for (i in (dataManager.startTime.hour+1)..dataManager.endTime.hour){
            val text = TextView(
                ContextThemeWrapper(applicationContext, R.style.TimeText))
            text.width = width
            text.height = (12f * xdpi).toInt()
            text.text = "$i"
            text.translationY = 6f * xdpi
            times.addView(text)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.manageClassButton -> {
                val intent = Intent(this, ManageClasses::class.java)
                startActivity(intent)
            }
            R.id.manageAssignmentsButton -> {
                val intent = Intent(this, ShowAllAssignments::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        calendarRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL
            ,false)
            rvAdapter = WeeklyViewRVAdapter()
            adapter = rvAdapter
        }
        val snapHelper = WeeklyViewSnapHelper(1)
        snapHelper.attachToRecyclerView(calendarRecyclerView)
        snapHelper.setSnapBlockCallback(object : WeeklyViewSnapHelper.SnapBlockCallback {
            override fun onBlockSnap(snapPosition: Int) {
                if (selectedItemPos == snapPosition)
                    return
                selectedItemPos = snapPosition
                val nowDate = currentDate + (selectedItemPos - today)
                supportActionBar?.title = "${nowDate.year} / ${nowDate.month}"
            }

        })
    }

    private fun setNavigationBarHeight() {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if(resourceId > 0){
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
    }

    private fun setStatusBarHeight() {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if(resourceId > 0){
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
    }

    override fun onRestart() {
        super.onRestart()
        rvAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        dataManager.saveData()
    }

    companion object {
        lateinit var appContext: Context
        lateinit var currentDate: Date
        lateinit var activity: AppCompatActivity
        var today: Int = 1022
        val dataManager: DataManager = DataManager()
        var navigationBarHeight: Int = 0
        var statusBarHeight: Int = 0

        val colorMap: Map<String, Int> = mapOf(
            "A" to R.color.A,
            "B" to R.color.B,
            "C" to R.color.C,
            "D" to R.color.D,
            "E" to R.color.E,
            "F" to R.color.F,
            "G" to R.color.G,
            "H" to R.color.H,
            "I" to R.color.I,
            "J" to R.color.J,
            "K" to R.color.K,
            "L" to R.color.L,
            "M" to R.color.M,
            "N" to R.color.N,
            "O" to R.color.O,
            "P" to R.color.P,
            "Q" to R.color.Q,
            "R" to R.color.R,
            "S" to R.color.S,
            "T" to R.color.T,
            "U" to R.color.U,
            "V" to R.color.V,
            "W" to R.color.W,
            "X" to R.color.X,
            "Y" to R.color.Y
        )
    }

}
