package com.ksa.scheduler

data class Meeting (var name: String, var date: Date, var startTime: Time, var endTime: Time,
                    var color: String, var id: Int = -1) {
    init {
        if (id == -1) {
            id = numberOfMeetings++
        }
    }

    companion object {
        var numberOfMeetings = 0
    }
}
