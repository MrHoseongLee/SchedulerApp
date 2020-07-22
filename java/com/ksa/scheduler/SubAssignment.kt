package com.ksa.scheduler

data class SubAssignment (var date: Date, var time: Time, var estimatedTime: Time, var movable: Boolean, val parent: Assignment) {
    override fun toString(): String {
        return "$date $time $estimatedTime"
    }

    operator fun compareTo(other: SubAssignment): Int {
        if (movable && !other.movable) {
            return 1
        }
        if (!movable && other.movable) {
            return -1
        }
        val dateComparison = date.compareTo(other.date)
        if (dateComparison != 0) {
            return dateComparison
        }
        return time.compareTo(other.time)
    }

}
