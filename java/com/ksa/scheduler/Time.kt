package com.ksa.scheduler

data class Time (val hour: Int, val minute: Int) {

    operator fun plus (time: Int): Time {
        return Time(hour + (minute + time) / 60, (minute + time) % 60)
    }

    operator fun plus (time: Time): Time {
        val minutesToAdd = time.hour * 60 + time.minute
        return this + minutesToAdd
    }

    operator fun minus(time: Time): Time {
        val minutes = (hour - time.hour) * 60 + (minute - time.minute)
        return Time (minutes / 60, minutes % 60)
    }

    override fun toString(): String {
        return if (minute >= 10) {
            "${hour}:${minute}"
        } else {
            "${hour}:0${minute}"
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Time) {
            hour == other.hour && minute == other.minute
        } else {
            super.equals(other)
        }
    }

    operator fun compareTo (other: Time): Int {
        val thisCompareSize  = this.hour * 100 + this.minute
        val otherCompareSize = other.hour * 100 + other.minute
        return when {
            thisCompareSize > otherCompareSize ->  1
            thisCompareSize < otherCompareSize -> -1
            else -> 0
        }
    }

    override fun hashCode(): Int {
        var result = hour
        result = 60 * result + minute
        return result
    }

    val time: Int
        get() = hour * 60 + minute
}
