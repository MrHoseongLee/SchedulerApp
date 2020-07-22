package com.ksa.scheduler

import android.content.Intent
import android.view.animation.OvershootInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class ButtonMenu (private val activity: MainActivity) {

    private var isMenuOpen = false
    private val translationY = 100f
    private val interpolator = OvershootInterpolator()

    fun init() {
        activity.ButtonMenuWrapper.translationY -= MainActivity.navigationBarHeight

        activity.addMeeting.alpha = 0f
        activity.addAssignment.alpha = 0f

        activity.addMeeting.translationY = translationY
        activity.addAssignment.translationY = translationY

        activity.addMeeting.isClickable = false
        activity.addAssignment.isClickable = false

        setOnClickListener(activity.gotoAddEventMenu)
        setOnClickListener(activity.addMeeting)
        setOnClickListener(activity.addAssignment)
    }

    private fun setOnClickListener(button: FloatingActionButton) {
        button.setOnClickListener {
            when(button.id) {
                R.id.gotoAddEventMenu -> menuButtonClicked()
                R.id.addMeeting -> addMeetingButtonClicked()
                R.id.addAssignment -> addAssignmentButtonClicked()
            }
        }
    }

    private fun menuButtonClicked() {
        if(isMenuOpen) {
            closeMenu()
        } else{
            openMenu()
        }
    }

    private fun addMeetingButtonClicked() {
        val intent = Intent(activity, AddMeeting::class.java)
        activity.startActivity(intent)
    }

    private fun addAssignmentButtonClicked() {
        val intent = Intent(activity, AddAssignment::class.java)
        activity.startActivity(intent)
    }

    private fun openMenu() {
        isMenuOpen = !isMenuOpen

        activity.gotoAddEventMenu.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start()

        activity.addMeeting.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        activity.addAssignment.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()

        activity.addMeeting.isClickable = true
        activity.addAssignment.isClickable = true
    }

    private fun closeMenu() {
        isMenuOpen = !isMenuOpen

        activity.gotoAddEventMenu.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start()

        activity.addMeeting.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        activity.addAssignment.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()

        activity.addMeeting.isClickable = false
        activity.addAssignment.isClickable = false
    }

}
