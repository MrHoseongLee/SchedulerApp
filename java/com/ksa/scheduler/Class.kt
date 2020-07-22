package com.ksa.scheduler

import android.graphics.Color

// Day of Week : 0 -> Sunday etc
data class Class (val className: String, var dayOfWeek: Int, var startPeriod: Int, var isDoubleClass: Boolean) {
    override fun toString(): String {
        val dayOfWeekString: String = when (dayOfWeek) {
            0 -> "Sunday"
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            else -> ""
        }

        return if(isDoubleClass) {
            "$dayOfWeekString $startPeriod D"
        } else {
            "$dayOfWeekString $startPeriod"
        }
    }
}