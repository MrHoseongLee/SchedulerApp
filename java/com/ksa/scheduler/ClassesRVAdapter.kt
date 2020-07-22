package com.ksa.scheduler

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.class_item.view.*

class ClassesRVAdapter : RecyclerView.Adapter<ClassesRVAdapter.Holder>() {

    private var classes: List<String> = ArrayList()
    private var classColors: HashMap<String, String> = HashMap()

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
        val className: String = classes[position]
        val classColor: Int = MainActivity.colorMap[classColors[className]] ?: error("")
        holder.itemView.className.text = className
        ViewCompat.setBackgroundTintList(holder.itemView.className,
             ContextCompat.getColorStateList(ManageClasses.activity, classColor))
        holder.itemView.className.setOnClickListener {
            val intent = Intent(ManageClasses.activity, EditClass::class.java)
            intent.putExtra("isNew", false)
            intent.putExtra("name", className)
            intent.putExtra("color", classColor)
            ManageClasses.activity.startActivity(intent)
        }
    }

    fun loadData() {
        classes = MainActivity.dataManager.classes.keys.toList().sorted()
        classColors = MainActivity.dataManager.classColors
        notifyDataSetChanged()
    }

    fun deleteAtPos (position: Int) {
        MainActivity.dataManager.removeClass(classes[position])
        loadData()
    }
}
