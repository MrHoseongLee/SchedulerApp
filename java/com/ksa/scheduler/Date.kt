package com.ksa.scheduler

data class Date (val year: Int, val month: Int, val day: Int) {

    companion object {
        val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }

    override fun hashCode(): Int {
        return year * 1000 + month * 100 + day
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other?.javaClass != javaClass) return false

        other as Date

        if(year == other.year && month == other.month && day == other.day) return true
        return false
    }

    override fun toString(): String {
        return "${year}.${month}.${day}"
    }

    operator fun plus(daysToAdd: Int): Date {
        if(daysToAdd < 0) {
            return minus(-daysToAdd)
        }
        var newDay: Int = day + daysToAdd
        var newMonth: Int = month
        var newYear: Int = year
        while(newDay > daysInMonth[(newMonth - 1).rem(12)]) {
            newDay -= daysInMonth[(newMonth - 1).rem(12)]
            ++newMonth
        }
        while(newMonth > 12) {
            newMonth -= 12
            ++newYear
        }
        return Date(newYear, newMonth, newDay)
    }

    operator fun minus(daysToSubtract: Int): Date {
        if(daysToSubtract < 0) {
            return plus(-daysToSubtract)
        }
        var newDay: Int = day - daysToSubtract
        var newMonth: Int = month
        var newYear: Int = year
        while(newDay <= 0) {
            newDay += when (newMonth < 2){
                true -> daysInMonth[newMonth + 10]
                false -> daysInMonth[newMonth -2]
            }
            --newMonth
        }
        while(newMonth <= 0) {
            newMonth += 12
            --newYear
        }
        return Date(newYear, newMonth, newDay)
    }

}
