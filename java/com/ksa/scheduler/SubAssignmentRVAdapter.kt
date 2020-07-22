package com.ksa.scheduler

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.subassignment_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class SubAssignmentRVAdapter: RecyclerView.Adapter<SubAssignmentRVAdapter.Holder>() {

    private var subAssignments: ArrayList<SubAssignment> = ArrayList()

    private lateinit var currentActivity: AppCompatActivity

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.subassignment_item, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return subAssignments.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (subAssignments[position].date.year == -1) {
            holder.itemView.subAssignmentName.text = "Undefined"
        } else {
            holder.itemView.subAssignmentName.text = subAssignments[position].toString()
        }
        holder.itemView.subAssignmentMutable.isChecked = subAssignments[position].movable

        holder.itemView.subAssignmentName.setOnClickListener {
            val date: Date = subAssignments[position].date
            val time: Time = subAssignments[position].time
            val c = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                subAssignments[position].time = Time(hour, minute)
                subAssignments[position].movable = false
                notifyDataSetChanged()
            }
            TimePickerDialog(currentActivity, R.style.TimePicker, timeSetListener, time.hour, time.minute, false).show()
            val datePickerDialog = DatePickerDialog(currentActivity, R.style.DatePicker,
                DatePickerDialog.OnDateSetListener{ _, Year, Month, Day
                    -> run { subAssignments[position].date = Date(Year, Month+1, Day)
                }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        holder.itemView.subAssignmentMutable.setOnClickListener {
            subAssignments[position].movable = holder.itemView.subAssignmentMutable.isChecked
        }
    }

    fun setCurrentActivity (activity: AppCompatActivity) {
        currentActivity = activity
    }

    fun loadData (id: Int) {
        subAssignments = MainActivity.dataManager.assignments[id]!!.subAssignments
        subAssignments.sortWith(Comparator { o1, o2 -> o1.compareTo(o2) })
        notifyDataSetChanged()
    }

}

