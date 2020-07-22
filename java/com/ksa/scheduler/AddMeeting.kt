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
import kotlinx.android.synthetic.main.add_meeting_menu.*
import kotlinx.android.synthetic.main.color_picker_dialog.view.*
import java.util.Calendar

class AddMeeting : AppCompatActivity() {

    private lateinit var meetingDate: Date
    private lateinit var startTime: Time
    private lateinit var endTime: Time
    private var meetingColor: String = "A"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_meeting_menu)

        addMeetingMenuWrapper.translationY = MainActivity.statusBarHeight.toFloat()

        // Set dueDate input
        var c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        meetingDate = Date(year, month+1, day)

        addMeetingDateInput.hint = MainActivity.currentDate.toString()

        addMeetingDateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, R.style.DatePicker,
                DatePickerDialog.OnDateSetListener{ _, Year, Month, Day
                    -> run {
                        this.addMeetingDateInput.text =
                            this.getString(R.string.date, Year, Month+1, Day)
                        meetingDate = Date(Year, Month+1, Day)
                    }
                }, year, month, day)
            datePickerDialog.show()
        }

        addMeetingStartTimeInput.setOnClickListener {
            c = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                startTime = Time(hour, minute)
                addMeetingStartTimeInput.text = startTime.toString()
            }
            if (this::startTime.isInitialized) {
                TimePickerDialog(this, R.style.TimePicker, timeSetListener, startTime.hour, startTime.minute, false).show()
            } else {
                TimePickerDialog(this, R.style.TimePicker, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
            }
        }

        addMeetingEndTimeInput.setOnClickListener {
            c = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                endTime = Time(hour, minute)
                addMeetingEndTimeInput.text = endTime.toString()
            }
            if (this::endTime.isInitialized) {
                TimePickerDialog(this, R.style.TimePicker, timeSetListener, endTime.hour, endTime.minute, false).show()
            } else {
                TimePickerDialog(this, R.style.TimePicker, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
            }
        }

        addMeetingNameInput.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                addMeetingNameInput.hint = ""
            } else {
                addMeetingNameInput.hint = resources.getString(R.string.input_meeting_name)
            }
        }

        addMeetingNameInput.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(addMeetingNameInput.windowToken, 0)
                    addMeetingNameInput.clearFocus()
                    true
                }
                else -> false
            }
        }

        addMeetingColorButton.setOnClickListener {
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

        addMeetingExitButton.setOnClickListener {
            finish()
        }

        // Set save button
        addMeetingSave.setOnClickListener {
            if (validate()){
                saveInput()
            }
        }
    }

    private fun validate (): Boolean {
        val meetingName: String = addMeetingNameInput.text.toString()
        if (meetingName == "") {
            addMeetingNameInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
            return false
        }
        if (!this::startTime.isInitialized) {
            addMeetingStartTimeInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
            return false
        }
        if (!this::endTime.isInitialized) {
            addMeetingEndTimeInput.setHintTextColor(ContextCompat.getColor(this, R.color.red))
            return false
        }
        if(endTime.time < startTime.time) {
            Toast.makeText(MainActivity.appContext, "Can't end before starting", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveInput () {
        val meetingName: String = addMeetingNameInput.text.toString()
        val meeting = Meeting(meetingName, meetingDate, startTime, endTime, meetingColor)
        MainActivity.dataManager.addMeeting(meeting)
        finish()
    }

    private fun onButtonClick (v: View) {
        meetingColor = when (v.id) {
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
        val color = MainActivity.colorMap[meetingColor] ?: error("")
        addMeetingColorButton.background.setTint(ContextCompat.getColor(applicationContext, color))
    }
}