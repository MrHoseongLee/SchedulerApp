package com.ksa.scheduler

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.day_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class WeeklyViewRVAdapter : RecyclerView.Adapter<WeeklyViewRVAdapter.Holder>() {

    private val assignments: HashMap<Date, ArrayList<SubAssignment>> = MainActivity.dataManager.assignmentByDate
    private val meetings: HashMap<Date, ArrayList<Meeting>> = MainActivity.dataManager.meetingByDate
    private val classes: Array<ArrayList<Class>> = MainActivity.dataManager.classByDayOfWeek
    private val classColors: HashMap<String, String> = MainActivity.dataManager.classColors
    private val time: Int = MainActivity.dataManager.totalTimeInDay()
    private val startTime: Int = MainActivity.dataManager.startTime.time
    private val xdpi: Float = MainActivity.appContext.resources.displayMetrics.xdpi
    private val width: Int = android.content.res.Resources.getSystem().displayMetrics.widthPixels / 8

    private lateinit var recyclerView: RecyclerView

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buttons = ArrayList<View>()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.day_item, viewGroup, false)
        view.layoutParams.width = width
        for (i in 1..time+1) {
            val line = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.line, viewGroup, false)
            line.translationY = 12f * i * xdpi / 25.4f
            view.layout_day.addView(line)
        }
        val lunchButton = Button(ContextThemeWrapper(viewGroup.context, R.style.TaskButton))
        lunchButton.width = width
        lunchButton.height = (10f * xdpi / 25.4f).toInt()
        lunchButton.isClickable = false
        lunchButton.translationY = 12.5f * (760 - startTime) * xdpi / 25.4f / 60f
        lunchButton.text = "점심"
        ViewCompat.setBackgroundTintList(lunchButton,
            ContextCompat.getColorStateList(MainActivity.appContext, R.color.white))
        view.layout_day.addView(lunchButton)

        val dinnerButton = Button(ContextThemeWrapper(viewGroup.context, R.style.TaskButton))
        dinnerButton.width = width
        dinnerButton.height = (10f * xdpi / 25.4f).toInt()
        dinnerButton.isClickable = false
        dinnerButton.translationY = 12.5f * (1095 - startTime) * xdpi / 25.4f / 60f
        dinnerButton.text = "저녁"
        ViewCompat.setBackgroundTintList(dinnerButton,
            ContextCompat.getColorStateList(MainActivity.appContext, R.color.white))
        view.layout_day.addView(dinnerButton)

        recyclerView.recycledViewPool.setMaxRecycledViews(viewType, Int.MAX_VALUE)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return 4096
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        for (button in holder.buttons) {
            holder.itemView.layout_day.removeView(button)
        }
        holder.buttons.clear()
        holder.itemView.dayNumber.text = (MainActivity.currentDate + (position - MainActivity.today)).day.toString()

        val currentDate = MainActivity.currentDate + (position - MainActivity.today)

        // Create assignment buttons
        val assignmentsForCurrentPos = assignments[currentDate]
        if(assignmentsForCurrentPos !== null) {
            for(assignment in assignmentsForCurrentPos) {
                val button = Button(ContextThemeWrapper(holder.itemView.rootView.context, R.style.TaskButton))
                button.width = width
                button.height = (12.5f * assignment.estimatedTime.time * xdpi / 25.4f / 60f).toInt()
                button.translationY = (12f * (assignment.time.time - startTime) * xdpi / 25.4f / 60f)
                val assignmentColor: Int = MainActivity.colorMap[assignment.parent.color] ?: error("")
                ViewCompat.setBackgroundTintList(button,
                    ContextCompat.getColorStateList(MainActivity.appContext, assignmentColor))
                button.text = assignment.parent.name
                button.setOnClickListener {
                    val intent = Intent(MainActivity.activity, EditAssignment::class.java)
                    intent.putExtra("id", assignment.parent.id)
                    MainActivity.activity.startActivity(intent)
                }
                holder.itemView.layout_day.addView(button)
                holder.buttons.add(button)
            }
        }

        // Create meeting buttons
        val meetingForCurrentPos = meetings[currentDate]
        if(meetingForCurrentPos !== null) {
            for(meeting in meetingForCurrentPos) {
                val button = Button(ContextThemeWrapper(holder.itemView.rootView.context, R.style.TaskButton))
                button.width = width
                button.height = (12.5f * (meeting.endTime.time - meeting.startTime.time) * xdpi / 25.4f / 60f).toInt()
                button.translationY = (12f * (meeting.startTime.time - startTime) * xdpi / 25.4f / 60f)
                val meetingColor: Int = MainActivity.colorMap[meeting.color] ?: error("")
                ViewCompat.setBackgroundTintList(button,
                    ContextCompat.getColorStateList(MainActivity.appContext, meetingColor))
                button.text = meeting.name
                button.setOnClickListener {
                    val intent = Intent(MainActivity.activity, EditMeeting::class.java)
                    intent.putExtra("id", meeting.id)
                    MainActivity.activity.startActivity(intent)
                }
                holder.itemView.layout_day.addView(button)
                holder.buttons.add(button)
            }
        }

        // Create class buttons
        for (classData in classes[position % 7]) {
            val button = Button(ContextThemeWrapper(holder.itemView.rootView.context, R.style.TaskButton))
            button.width = width
            if (classData.isDoubleClass) {
                button.height = (24.5f * xdpi / 25.4f).toInt()
            } else {
                button.height = (12.5f * xdpi / 25.4f).toInt()
            }
            button.isClickable = false
            button.translationY = (12f * (MainActivity.dataManager.timeConverter[classData.startPeriod-1].time
                    - startTime) / 60f * xdpi / 25.4f)
            val classColor: Int = MainActivity.colorMap[classColors[classData.className]] ?: error("")
            ViewCompat.setBackgroundTintList(button,
                ContextCompat.getColorStateList(MainActivity.appContext, classColor))
            button.text = classData.className
            holder.itemView.layout_day.addView(button)
            holder.buttons.add(button)
        }

        if (position == MainActivity.today) {
            val c = Calendar.getInstance()
            val currentTime = Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
            val line = LayoutInflater.from(holder.itemView.rootView.context)
                .inflate(R.layout.indicator, holder.itemView.rootView as ViewGroup, false)
            line.z = 10f
            line.translationY = 12f * (currentTime.time - startTime) / 60f * xdpi / 25.4f
            holder.itemView.layout_day.addView(line)
            holder.buttons.add(line)
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

}