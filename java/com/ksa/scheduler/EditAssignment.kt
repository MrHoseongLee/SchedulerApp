package com.ksa.scheduler

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.edit_assignment_menu.*
import kotlinx.android.synthetic.main.color_picker_dialog.view.*
import kotlinx.android.synthetic.main.time_picker_dialog.view.*

class EditAssignment: AppCompatActivity() {

    private lateinit var dueDate: Date
    private lateinit var dueTime: Time
    private lateinit var estimatedTime: Time
    private lateinit var rvAdapter: SubAssignmentRVAdapter
    private var id: Int = 0
    private var priority: Int = 5
    private var assignmentColor: String = "A"

    private val noneDate = Date(-1, 0, 0)
    private val noneTime = Time(-1, 0)

    private lateinit var assignment: Assignment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_assignment_menu)

        editAssignmentMenuWrapper.translationY = MainActivity.statusBarHeight.toFloat()

        id = intent.extras!!.getInt("id")

        assignment = MainActivity.dataManager.assignments[id]!!

        dueDate = assignment.dueDate
        dueTime = assignment.dueTime
        estimatedTime = assignment.estimatedTime
        priority = assignment.priority
        assignmentColor = assignment.color

        editAssignmentRV.apply {
            layoutManager = LinearLayoutManager(this@EditAssignment, LinearLayoutManager.VERTICAL
                ,false)
            rvAdapter = SubAssignmentRVAdapter()
            adapter = rvAdapter
        }

        rvAdapter.setCurrentActivity(this)
        rvAdapter.loadData(id)

        var divisionText = ""

        for (subAssignment in assignment.subAssignments) {
            divisionText += if (subAssignment.estimatedTime.minute < 10) {
                "${subAssignment.estimatedTime.hour}.0${subAssignment.estimatedTime.minute} "
            } else {
                "${subAssignment.estimatedTime.hour}.${subAssignment.estimatedTime.minute} "
            }
        }

        if (divisionText.isNotEmpty()) {
            divisionText = divisionText.reversed().substring(1).reversed()
        }

        editAssignmentDivisionInput.setText(divisionText)

        editAssignmentNameInput.setText(assignment.name)
        editAssignmentDateInput.text = dueDate.toString()
        editAssignmentDueTimeInput.text = dueTime.toString()
        editAssignmentEstimatedTimeInput.text = estimatedTime.toString()

        val color = MainActivity.colorMap[assignmentColor] ?: error("")
        editAssignmentColorButton.background.setTint(ContextCompat.getColor(applicationContext, color))

        editAssignmentPriorityPicker.minValue = 1
        editAssignmentPriorityPicker.maxValue = 10
        editAssignmentPriorityPicker.value = priority

        editAssignmentDateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, R.style.DatePicker,
                DatePickerDialog.OnDateSetListener{ _, Year, Month, Day
                    -> run {
                    this.editAssignmentDateInput.text =
                        this.getString(R.string.date, Year, Month+1, Day)
                    dueDate = Date(Year, Month+1, Day) }
                }, dueDate.year, dueDate.month, dueDate.day)
            datePickerDialog.show()
        }

        editAssignmentDueTimeInput.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                dueTime = Time(hour, minute)
                editAssignmentDueTimeInput.text = dueTime.toString()
            }
            TimePickerDialog(this, R.style.TimePicker, timeSetListener, dueTime.hour, dueTime.minute, false).show()
        }

        editAssignmentEstimatedTimeInput.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DialogBox)
            val dialogView =
                View.inflate(applicationContext, R.layout.time_picker_dialog, null)
            builder.setView(dialogView).setPositiveButton("Set") {
                    _, _ -> estimatedTime = Time(dialogView.hourPicker.value, dialogView.minutePicker.value)
                editAssignmentEstimatedTimeInput.text = estimatedTime.toString()
            }.show()
            dialogView.hourPicker.minValue = 0
            dialogView.hourPicker.maxValue = 23
            dialogView.minutePicker.minValue = 0
            dialogView.minutePicker.maxValue = 59
            if (this::estimatedTime.isInitialized) {
                dialogView.hourPicker.value = estimatedTime.hour
                dialogView.minutePicker.value = estimatedTime.minute
            } else {
                dialogView.hourPicker.value = 0
                dialogView.minutePicker.value = 0
            }
        }

        editAssignmentNameInput.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                editAssignmentNameInput.hint = ""
            } else {
                editAssignmentNameInput.hint = resources.getString(R.string.input_assignment_name)
            }
        }

        editAssignmentNameInput.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editAssignmentNameInput.windowToken, 0)
                    editAssignmentNameInput.clearFocus()
                    true
                }
                else -> false
            }
        }

        editAssignmentDivisionInput.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editAssignmentDivisionInput.windowToken, 0)
                    val divisionTimes = editAssignmentDivisionInput.text.toString().split(" ")
                    var sumOfTime = Time(0, 0)
                    for (divisionTime in divisionTimes) {
                        val timeData = divisionTime.split(".")
                        if (timeData.size == 2) {
                            sumOfTime += Time(timeData[0].toInt(), timeData[1].toInt())
                        } else {
                            sumOfTime = noneTime
                            break
                        }
                    }
                    if (sumOfTime == estimatedTime) {
                        editAssignmentDivisionInput.setTextColor(ContextCompat.getColor(this, R.color.white))
                        assignment.subAssignments.clear()
                        for (element in divisionTimes) {
                            val timeData = element.split(".")
                            val estimatedTime = Time(timeData[0].toInt(), timeData[1].toInt())
                            assignment.subAssignments.add(SubAssignment(noneDate, noneTime, estimatedTime, true, assignment))
                        }
                        rvAdapter.notifyDataSetChanged()
                    } else {
                        editAssignmentDivisionInput.setTextColor(ContextCompat.getColor(this, R.color.red))
                    }
                    editAssignmentDivisionInput.clearFocus()
                    true
                }
                else -> false
            }
        }

        editAssignmentColorButton.setOnClickListener {
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

        editAssignmentExitButton.setOnClickListener {
            finish()
        }

        editAssignmentDeleteButton.setOnClickListener {
            MainActivity.dataManager.removeAssignment(assignment)
            finish()
        }

        // Set save button
        editAssignmentSave.setOnClickListener {
            if (validate()){
                saveInput()
            }
        }
    }

    private fun validate (): Boolean {
        val assignmentName: String = editAssignmentNameInput.text.toString()
        priority = editAssignmentPriorityPicker.value
        if (assignmentName == "") {
            editAssignmentNameInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
            return false
        }
        var sumOfTime = 0
        for (subAssignment in assignment.subAssignments) {
            if (subAssignment.movable) {
                sumOfTime += subAssignment.estimatedTime.time
            }
        }
        if (sumOfTime > estimatedTime.time) {
            Toast.makeText(applicationContext, "Sum of unmovable sub_assignments is greater than estimated time",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveInput () {
        val assignmentName: String = editAssignmentNameInput.text.toString()
        MainActivity.dataManager.editAssignment(id, assignmentName, dueDate, dueTime, estimatedTime, priority, assignmentColor)
        finish()
    }

    private fun onButtonClick (v: View) {
        assignmentColor = when (v.id) {
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
        val color = MainActivity.colorMap[assignmentColor] ?: error("")
        editAssignmentColorButton.background.setTint(ContextCompat.getColor(applicationContext, color))
    }
}
