package com.ksa.scheduler

data class Data (val tasks: HashMap<Date, ArrayList<Task>>, val startTime: Time, val endTime: Time)
