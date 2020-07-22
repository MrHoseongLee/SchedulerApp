package com.ksa.scheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_class_time_dialog.view.*
import kotlinx.android.synthetic.main.class_item.view.*

class ClassTimesRVAdapter : RecyclerView.Adapter<ClassTimesRVAdapter.Holder>() {

    private var classes: java.util.ArrayList<Class> = ArrayList()

    private val daysOfWeek =
        arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    private lateinit var currentActivity: AppCompatActivity

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.class_item, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return classes.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.className.setOnClickListener {
            val builder = AlertDialog.Builder(currentActivity, R.style.DialogBox)
            val dialogView =
                View.inflate(currentActivity.applicationContext, R.layout.add_class_time_dialog, null)
            val className = classes[position].className
            val dialogDayOfWeekPicker = dialogView.addClassTimeDayOfWeek
            val dialogStartPeriodPicker = dialogView.addClassTimeStartPeriod
            val isDoubleClass = dialogView.addClassTimeIsDoubleClass

            dialogDayOfWeekPicker.minValue = 0
            dialogDayOfWeekPicker.maxValue = daysOfWeek.size - 1
            dialogDayOfWeekPicker.displayedValues = daysOfWeek

            dialogStartPeriodPicker.minValue = 1
            dialogStartPeriodPicker.maxValue = 11

            builder.setView(dialogView)
                .setPositiveButton("Change") { _, _ ->
                    MainActivity.dataManager.changeClass(className, position, dialogDayOfWeekPicker.value,
                        dialogStartPeriodPicker.value, isDoubleClass.isChecked)
                    loadData(className)
                }
                .show()
        }
        holder.itemView.className.text = classes[position].toString()
    }

    fun setCurrentActivity (activity: AppCompatActivity) {
        currentActivity = activity
    }

    fun loadData (className: String) {
        classes = MainActivity.dataManager.classes[className]!!
        notifyDataSetChanged()
    }

    fun deleteAtPos (position: Int) {
        MainActivity.dataManager.removeClassObj(classes[position])
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

}

