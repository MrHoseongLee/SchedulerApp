package com.ksa.scheduler

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.class_item.view.*
import kotlin.collections.ArrayList

class AssignmentRVAdapter: RecyclerView.Adapter<AssignmentRVAdapter.Holder>() {

    private lateinit var assignments: ArrayList<Assignment>

    private lateinit var currentActivity: AppCompatActivity

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.class_item, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return assignments.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.className.text = assignments[position].toString()
        holder.itemView.className.setOnClickListener {
            val intent = Intent(currentActivity, EditAssignment::class.java)
            intent.putExtra("id", assignments[position].id)
            currentActivity.startActivity(intent)
        }
    }

    fun setCurrentActivity (currentActivity: AppCompatActivity) {
        this.currentActivity = currentActivity
    }

    fun loadData () {
        assignments = MainActivity.dataManager.sortedAssignment
    }

}

