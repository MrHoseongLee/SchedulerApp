package com.ksa.scheduler

data class Time (val hour: Int, val minute: Int) {

    operator fun minus(time: Time): Time {
        val minutes = (hour - time.hour) * 60 + (minute - time.minute)
        return Time (minutes / 60, minutes % 60)
    }

}
