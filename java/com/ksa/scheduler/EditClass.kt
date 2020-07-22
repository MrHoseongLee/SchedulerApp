package com.ksa.scheduler

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_class_time_dialog.view.*
import kotlinx.android.synthetic.main.color_picker_dialog.view.*
import kotlinx.android.synthetic.main.edit_class_menu.*

class EditClass: AppCompatActivity() {

    private lateinit var rvAdapter: ClassTimesRVAdapter
    private val daysOfWeek =
        arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    private var className: String = ""
    private var classColor: Int = R.color.A

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_class_menu)

        editClassWrapper.translationY += MainActivity.statusBarHeight

        timesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditClass, LinearLayoutManager.VERTICAL,
                false)
            rvAdapter = ClassTimesRVAdapter()
            adapter = rvAdapter
        }

        rvAdapter.setCurrentActivity(this)

        if (!intent.extras!!.getBoolean("isNew")) {
            className = intent.extras!!.getString("name")!!
            classColor = intent.extras!!.getInt("color")
            rvAdapter.loadData(className)
            editClassText.setText(className)
            editClassColorButton.background.setTint(ContextCompat.getColor(applicationContext, classColor))
        }

        editClassText.setOnFocusChangeListener {_: View?, hasFocus: Boolean ->
            if (hasFocus) {
                editClassText.hint = ""
            } else {
                editClassText.hint = resources.getString(R.string.input_class_name)
            }
        }

        editClassText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editClassText.windowToken, 0)
                    editClassText.clearFocus()
                    true
                }
                else -> false
            }
        }

        addClassTime.translationY -= MainActivity.navigationBarHeight

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
        itemTouchHelper.attachToRecyclerView(timesRecyclerView)

        editClassColorButton.setOnClickListener {
            val newClassName: String = editClassText.text.toString()
            if (className == "" && newClassName == "") {
                Toast.makeText(this, "Input Class Name first", Toast.LENGTH_SHORT).show()
            } else {
                if (className == "") {
                    className = newClassName
                }
                val builder = AlertDialog.Builder(this, R.style.DialogBox)
                val dialogView =
                    View.inflate(applicationContext, R.layout.color_picker_dialog, null)

                val show = builder.setView(dialogView).show()

                dialogView.buttonA.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonB.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonC.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonD.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonE.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonF.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonG.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonH.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonI.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonJ.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonK.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonL.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonM.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonN.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonO.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonP.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonP.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonP.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonQ.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonR.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonS.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonT.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonU.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonV.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonW.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonX.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
                dialogView.buttonY.setOnClickListener { v: View -> onButtonClick(v); show.dismiss() }
            }

        }

        addClassTime.setOnClickListener {
            val newClassName: String = editClassText.text.toString()
            if (className == "" && newClassName == "") {
                Toast.makeText(this, "Input Class Name first", Toast.LENGTH_SHORT).show()
            } else if (className != newClassName && MainActivity.dataManager.classNameExists(newClassName)) {
                Toast.makeText(this, "Class $className already exists", Toast.LENGTH_SHORT).show()
            } else {
                if (className == "") {
                    className = newClassName
                }
                val builder = AlertDialog.Builder(this, R.style.DialogBox)
                val dialogView =
                    View.inflate(applicationContext, R.layout.add_class_time_dialog, null)
                val dialogDayOfWeekPicker = dialogView.addClassTimeDayOfWeek
                val dialogStartPeriodPicker = dialogView.addClassTimeStartPeriod
                val isDoubleClass = dialogView.addClassTimeIsDoubleClass

                dialogDayOfWeekPicker.minValue = 0
                dialogDayOfWeekPicker.maxValue = daysOfWeek.size - 1
                dialogDayOfWeekPicker.displayedValues = daysOfWeek

                dialogStartPeriodPicker.minValue = 1
                dialogStartPeriodPicker.maxValue = 11

                builder.setView(dialogView)
                    .setPositiveButton("Add") { _, _ ->
                        MainActivity.dataManager.addClass(className,
                            Class(className, dialogDayOfWeekPicker.value, dialogStartPeriodPicker.value, isDoubleClass.isChecked))
                        rvAdapter.loadData(className)
                    }.show()
            }

        }

    }

    override fun onPause() {
        super.onPause()

        val newClassName: String = editClassText.text.toString()

        /*
        if (MainActivity.dataManager.classes[className] == null) {
            MainActivity.dataManager.classes[className] = ArrayList()
            MainActivity.dataManager.classColors[className] = "A"
        }
         */

        if (className != newClassName) {
            MainActivity.dataManager.changeClassKey(className, newClassName)
            className = newClassName
        }

        if (MainActivity.dataManager.classColors[className] == null) {
            MainActivity.dataManager.classColors[className] = "A"
        }

        if (MainActivity.dataManager.classes[className] != null && MainActivity.dataManager.classes[className]!!.isEmpty()) {
            MainActivity.dataManager.removeClass(className)
        }
    }

    private fun onButtonClick (v: View) {
        val classColorString = when (v.id) {
            R.id.buttonA -> "A"
            R.id.buttonB -> "B"
            R.id.buttonC -> "C"
            R.id.buttonD -> "D"
            R.id.buttonE -> "E"
            R.id.buttonF -> "F"
            R.id.buttonG -> "G"
            R.id.buttonH -> "H"
            R.id.buttonI -> "I"
            R.id.buttonJ -> "H"
            R.id.buttonK -> "K"
            R.id.buttonL -> "L"
            R.id.buttonM -> "M"
            R.id.buttonN -> "N"
            R.id.buttonO -> "O"
            R.id.buttonP -> "P"
            R.id.buttonQ -> "Q"
            R.id.buttonR -> "R"
            R.id.buttonS -> "S"
            R.id.buttonT -> "T"
            R.id.buttonU -> "U"
            R.id.buttonV -> "V"
            R.id.buttonW -> "W"
            R.id.buttonX -> "X"
            R.id.buttonY -> "Y"
            else -> "?"
        }
        MainActivity.dataManager.changeClassColor(className, classColorString)
        classColor = MainActivity.colorMap[classColorString] ?: error("")
        editClassColorButton.background.setTint(ContextCompat.getColor(applicationContext, classColor))
    }

}