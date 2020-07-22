package com.ksa.scheduler

import java.util.*

data class Date (val year: Int, val month: Int, val day: Int) {

    override fun hashCode(): Int {
        return year * 10000 + month * 100 + day
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other?.javaClass != javaClass) return false

        other as Date

        if(year == other.year && month == other.month && day == other.day) return true
        return false
    }

    override fun toString(): String {
        return "${year}/${month}/${day}"
    }

    operator fun plus(daysToAdd: Int): Date {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(year, month - 1, day)
        currentCalendar.add(Calendar.DATE, daysToAdd)
        val newYear = currentCalendar.get(Calendar.YEAR)
        val newMonth = currentCalendar.get(Calendar.MONTH) + 1
        val newDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
        return Date(newYear, newMonth, newDay)
    }

    operator fun minus(daysToSubtract: Int): Date {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(year, month - 1, day)
        currentCalendar.add(Calendar.DATE, -daysToSubtract)
        val newYear = currentCalendar.get(Calendar.YEAR)
        val newMonth = currentCalendar.get(Calendar.MONTH) + 1
        val newDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
        return Date(newYear, newMonth, newDay)
    }

    operator fun compareTo (other: Date): Int {
        val thisCompareSize  = this.year * 10000 + this.month * 100 + this.day
        val otherCompareSize = other.year * 10000 + other.month * 100 + other.day
        return when {
            thisCompareSize > otherCompareSize ->  1
            thisCompareSize < otherCompareSize -> -1
            else -> 0
        }
    }

    fun getDayOfWeek(): Int {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(year, month-1, day)
        return currentCalendar.get(Calendar.DAY_OF_WEEK) - 1
    }

}
