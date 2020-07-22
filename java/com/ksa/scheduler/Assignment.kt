package com.ksa.scheduler

data class Assignment (var name: String, var dueDate: Date, var dueTime: Time, var estimatedTime: Time,
                       var priority: Int, var subAssignments: ArrayList<SubAssignment>,
                       var color: String, var id: Int=-1) {

    init {
        if (id == -1) {
            id = numberOfAssignments++
        }
    }

    operator fun compareTo (other: Assignment): Int {
        val dueDateComparison = this.dueDate.compareTo(other.dueDate)
        return when {
            dueDateComparison != 0 -> dueDateComparison
            else -> {
                val dueTimeComparison = this.dueTime.compareTo(other.dueTime)
                when {
                    dueTimeComparison != 0 -> dueTimeComparison
                    else -> this.priority.compareTo(other.priority)
                }
            }
        }
    }

    override fun toString(): String {
        return "$name until $dueDate $dueTime"
    }

    companion object {
        var numberOfAssignments = 0
    }
}
