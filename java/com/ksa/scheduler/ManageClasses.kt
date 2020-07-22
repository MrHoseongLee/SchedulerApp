package com.ksa.scheduler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.manage_class_menu.*

class ManageClasses : AppCompatActivity() {

    private lateinit var rvAdapter: ClassesRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_class_menu)

        manageClassWrapper.translationY += MainActivity.statusBarHeight
        activity = this

        classesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ManageClasses, LinearLayoutManager.VERTICAL,
                false)
            rvAdapter = ClassesRVAdapter()
            adapter = rvAdapter
        }

        rvAdapter.loadData()

        addClass.translationY -= MainActivity.navigationBarHeight

        addClass.setOnClickListener {
            val intent = Intent(this, EditClass::class.java)
            intent.putExtra("isNew", true)
            startActivity(intent)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                rvAdapter.deleteAtPos(viewHolder.layoutPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(classesRecyclerView)
    }

    override fun onRestart() {
        super.onRestart()
        rvAdapter.loadData()
    }

    companion object {
        lateinit var activity: AppCompatActivity
    }

}