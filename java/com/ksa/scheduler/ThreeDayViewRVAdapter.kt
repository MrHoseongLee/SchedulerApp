package com.ksa.scheduler
/*

import android.content.res.Resources
import android.view.View
import android.widget.Button
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.day_item.view.*

class ThreeDayViewRVAdapter : RecyclerView.Adapter<ThreeDayViewRVAdapter.Holder>() {

    private val tasks: HashMap<Date, ArrayList<Assignment>> = MainActivity.dataManager.assignments
    private val time: Int = MainActivity.dataManager.totalTimeInDay()
    private val startTime: Int = MainActivity.dataManager.startTime.hour
    private val xdpi: Float = MainActivity.appContext.resources.displayMetrics.xdpi
    private val width: Int = Resources.getSystem().displayMetrics.widthPixels / 3

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskButtons = ArrayList<Button>()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.day_item, viewGroup, false)
        // view.translationY = MainActivity.statusBarHeight.toFloat()
        view.layoutParams.width = width
        view.layoutParams.height = (8 * time * xdpi / 25.4f).toInt() + MainActivity.navigationBarHeight
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return 4096
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        for (button in holder.taskButtons) {
            holder.itemView.layout_day.removeView(button)
        }
        holder.taskButtons.clear()
        holder.itemView.dayNumber.text = (MainActivity.currentDate + (position - 1024)).toString()
        val tasksForCurrentPos = tasks[MainActivity.currentDate + (position - 1024)]
        if(tasksForCurrentPos !== null) {
            for(task in tasksForCurrentPos) {
                val button = Button(ContextThemeWrapper(holder.itemView.rootView.context,
                    R.style.TaskButton))
                button.width = width
                button.height = (8 * task.estimatedTime.hour * xdpi / 25.4f).toInt()
                button.translationY = (8 * (task.dueTime.hour - startTime) * xdpi / 25.4f)
                holder.itemView.layout_day.addView(button)
                holder.taskButtons.add(button)
            }
        }
    }

}
 */
