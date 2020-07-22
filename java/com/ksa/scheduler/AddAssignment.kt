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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.add_assignment_menu.*
import kotlinx.android.synthetic.main.color_picker_dialog.view.*
import kotlinx.android.synthetic.main.time_picker_dialog.view.*
import java.util.Calendar

class AddAssignment : AppCompatActivity() {

    private lateinit var dueDate: Date
    private lateinit var dueTime: Time
    private lateinit var estimatedTime: Time
    private var priority: Int = 5
    private var assignmentColor: String = "A"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_assignment_menu)

        addAssignmentMenuWrapper.translationY = MainActivity.statusBarHeight.toFloat()

        // Set dueDate input
        var c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        dueDate = Date(year, month+1, day)

        addAssignmentDateInput.hint = MainActivity.currentDate.toString()

        addAssignmentPriorityPicker.minValue = 1
        addAssignmentPriorityPicker.maxValue = 10
        addAssignmentPriorityPicker.value = priority

        addAssignmentDateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, R.style.DatePicker,
                DatePickerDialog.OnDateSetListener{ _, Year, Month, Day
                    -> run {
                    this.addAssignmentDateInput.text =
                        this.getString(R.string.date, Year, Month+1, Day)
                    dueDate = Date(Year, Month+1, Day) }
                }, year, month, day)
            datePickerDialog.show()
        }

        addAssignmentDueTimeInput.setOnClickListener {
            c = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                dueTime = Time(hour, minute)
                addAssignmentDueTimeInput.text = dueTime.toString()
            }
            if (this::dueTime.isInitialized) {
                TimePickerDialog(this, R.style.TimePicker, timeSetListener, dueTime.hour, dueTime.minute, false).show()
            } else {
                TimePickerDialog(this, R.style.TimePicker, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
            }
        }

        addAssignmentEstimatedTimeInput.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DialogBox)
            val dialogView =
                View.inflate(applicationContext, R.layout.time_picker_dialog, null)
            builder.setView(dialogView).setPositiveButton("Set") {
                _, _ -> estimatedTime = Time(dialogView.hourPicker.value, dialogView.minutePicker.value)
                addAssignmentEstimatedTimeInput.text = estimatedTime.toString()
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

        addAssignmentNameInput.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                addAssignmentNameInput.hint = ""
            } else {
                addAssignmentNameInput.hint = resources.getString(R.string.input_assignment_name)
            }
        }

        addAssignmentNameInput.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(addAssignmentNameInput.windowToken, 0)
                    addAssignmentNameInput.clearFocus()
                    true
                }
                else -> false
            }
        }

        addAssignmentColorButton.setOnClickListener {
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

        addAssignmentExitButton.setOnClickListener {
            finish()
        }

        // Set save button
        addAssignmentSave.setOnClickListener {
            if (validate()){
                saveInput()
            }
        }
    }

    private fun validate (): Boolean {
        val assignmentName: String = addAssignmentNameInput.text.toString()
        priority = addAssignmentPriorityPicker.value
        if (assignmentName == "") {
            addAssignmentNameInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
            return false
        }
        if (!this::dueTime.isInitialized) {
            addAssignmentDueTimeInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
            return false
        }
        if (!this::estimatedTime.isInitialized) {
            addAssignmentEstimatedTimeInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
        }
        return true
    }

    private fun saveInput () {
        val assignmentName: String = addAssignmentNameInput.text.toString()
        val assignment = Assignment(assignmentName, dueDate, dueTime, estimatedTime, priority, ArrayList(), assignmentColor)
        MainActivity.dataManager.addAssignment(assignment)
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
        addAssignmentColorButton.background.setTint(ContextCompat.getColor(applicationContext, color))
    }
}
