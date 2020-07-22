package com.ksa.scheduler

import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DataManager {

    lateinit var classes: HashMap<String, ArrayList<Class>>
    lateinit var classColors: HashMap<String, String>
    lateinit var classByDayOfWeek: Array<ArrayList<Class>>
    lateinit var startTime: Time
    lateinit var endTime: Time
    lateinit var meetings: HashMap<Int, Meeting>
    lateinit var meetingByDate: HashMap<Date, ArrayList<Meeting>>
    lateinit var assignments: HashMap<Int, Assignment>
    lateinit var assignmentByDate: HashMap<Date, ArrayList<SubAssignment>>
    lateinit var sortedAssignment: ArrayList<Assignment>

    private lateinit var mainActivity: MainActivity

    val timeConverter = arrayOf(Time(8, 50), Time(9, 50), Time(10, 50), Time(11, 50),
        Time(13, 40), Time(14, 40), Time(15, 40), Time(16, 40),
        Time(17, 40), Time(19, 30), Time(20, 30))

    fun setMainActivity (mainActivity: MainActivity) {
        this.mainActivity = mainActivity
    }

    fun loadData() {

        classes = HashMap()
        try {
            val classesInputStream: FileInputStream =
                MainActivity.appContext.openFileInput("classesData.txt")
            val inputRead = InputStreamReader(classesInputStream)
            val contents: List<String> = inputRead.readLines()
            for(content in contents) {
                val splitData: List<String> = content.split("`")
                val dayOfWeek: Int = splitData[1].toInt()
                val startPeriod: Int = splitData[2].toInt()
                val isDoubleClass: Boolean = splitData[3].toBoolean()
                classes.getOrPut(splitData[0]){ ArrayList() }
                    .add(Class(splitData[0], dayOfWeek, startPeriod, isDoubleClass))
            }
            inputRead.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        classColors = HashMap()
        try {
            val classColorsInputStream: FileInputStream =
                MainActivity.appContext.openFileInput("classColorsData.txt")
            val inputRead = InputStreamReader(classColorsInputStream)
            val contents: List<String> = inputRead.readLines()
            for(content in contents) {
                val splitData: List<String> = content.split("`")
                classColors[splitData[0]] = splitData[1]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        classByDayOfWeek = arrayOf(ArrayList(), ArrayList(), ArrayList(), ArrayList(),
            ArrayList(), ArrayList(), ArrayList())

        for ((_, value) in classes) {
            for (classData in value) {
                classByDayOfWeek[classData.dayOfWeek].add(classData)
            }
        }

        meetings = HashMap()
        meetingByDate = HashMap()
        var lastMeetingID = 0
        try {
            val meetingsInputStream: FileInputStream =
                MainActivity.appContext.openFileInput("meetingsData.txt")
            val inputRead = InputStreamReader(meetingsInputStream)
            val contents: List<String> = inputRead.readLines()
            for(content in contents) {
                val splitData: List<String> = content.split("`")
                val id: Int = splitData[0].toInt()
                lastMeetingID = lastMeetingID.coerceAtLeast(id)
                val name = splitData[1]
                val date = Date(splitData[2].toInt(), splitData[3].toInt(), splitData[4].toInt())
                val startTime = Time(splitData[5].toInt(), splitData[6].toInt())
                val endTime = Time(splitData[7].toInt(), splitData[8].toInt())
                val color = splitData[9]
                meetings[id] = Meeting(name, date, startTime, endTime, color, id)
                meetingByDate.getOrPut(date){ ArrayList() }.add(meetings[id]!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        assignments = HashMap()
        assignmentByDate = HashMap()
        sortedAssignment = ArrayList()
        var lastAssignmentID = 0

        try {
            val assignmentsInputStream: FileInputStream =
                MainActivity.appContext.openFileInput("assignmentsData.txt")
            val inputRead = InputStreamReader(assignmentsInputStream)
            val contents: List<String> = inputRead.readLines()
            for(content in contents) {
                val splitData: List<String> = content.split("`")
                val id = splitData[0].toInt()
                lastAssignmentID = lastAssignmentID.coerceAtLeast(id)
                val name = splitData[1]
                val dueDate = Date(splitData[2].toInt(), splitData[3].toInt(), splitData[4].toInt())
                val dueTime = Time(splitData[5].toInt(), splitData[6].toInt())
                val estimatedTime = Time(splitData[7].toInt(), splitData[8].toInt())
                val priority = splitData[9].toInt()
                val color = splitData[10]
                assignments[id] = Assignment(name, dueDate, dueTime, estimatedTime, priority, ArrayList(), color, id)
                sortedAssignment.add(assignments[id]!!)
                sort()
                val count = splitData[11].toInt()
                for (i in 0 until count) {
                    val date = Date(splitData[12 + 8*i].toInt(), splitData[13 + 8*i].toInt(), splitData[14 + 8*i].toInt())
                    val time = Time(splitData[15 + 8*i].toInt(), splitData[16 + 8*i].toInt())
                    val forTime = Time(splitData[17 + 8*i].toInt(), splitData[18 + 8*i].toInt())
                    val movable = splitData[19 + 8*i].toBoolean()
                    val subAssignment = SubAssignment(date, time, forTime, movable, assignments[id]!!)
                    assignments[id]!!.subAssignments.add(subAssignment)
                    assignmentByDate.getOrPut(date){ ArrayList() }.add(subAssignment)
                }
            }
            inputRead.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Meeting.numberOfMeetings = lastMeetingID + 1
        Assignment.numberOfAssignments = lastAssignmentID + 1

        startTime = Time(8, 0)
        endTime = Time(24, 0)

    }

    fun saveData () {

        try {
            val classesOutputStream: FileOutputStream =
                MainActivity.appContext.openFileOutput("classesData.txt", MODE_PRIVATE)
            val classesOutputWriter = OutputStreamWriter(classesOutputStream)

            for ((key, value) in classes) {
                for (classObject in value) {
                    classesOutputWriter.write(
                        "$key`${classObject.dayOfWeek}`${classObject.startPeriod}`${classObject.isDoubleClass}\n")
                }
            }

            classesOutputWriter.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val classColorsOutputStream: FileOutputStream =
                MainActivity.appContext.openFileOutput("classColorsData.txt", MODE_PRIVATE)
            val classColorsOutputWriter = OutputStreamWriter(classColorsOutputStream)

            for ((key, value) in classColors) {
                classColorsOutputWriter.write("$key`$value\n")
            }

            classColorsOutputWriter.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val meetingsOutputStream: FileOutputStream =
                MainActivity.appContext.openFileOutput("meetingsData.txt", MODE_PRIVATE)
            val meetingsOutputWriter = OutputStreamWriter(meetingsOutputStream)

            for ((_, value) in meetings) {
                meetingsOutputWriter.write(
                    "${value.id}`${value.name}`" +
                            "${value.date.year}`${value.date.month}`${value.date.day}`" +
                            "${value.startTime.hour}`${value.startTime.minute}`" +
                            "${value.endTime.hour}`${value.endTime.minute}`${value.color}\n")
            }

            meetingsOutputWriter.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val assignmentsOutputStream: FileOutputStream =
                MainActivity.appContext.openFileOutput("assignmentsData.txt", MODE_PRIVATE)
            val assignmentsOutputWriter = OutputStreamWriter(assignmentsOutputStream)

            for ((_, value) in assignments) {

                var text: String = "${value.id}`${value.name}`" +
                            "${value.dueDate.year}`${value.dueDate.month}`${value.dueDate.day}`" +
                            "${value.dueTime.hour}`${value.dueTime.minute}`" +
                            "${value.estimatedTime.hour}`${value.estimatedTime.minute}`" +
                            "${value.priority}`"
                text += "${value.color}`"
                text += "${value.subAssignments.size}`"
                for (subAssignment in value.subAssignments) {
                    text += "${subAssignment.date.year}`${subAssignment.date.month}`${subAssignment.date.day}`"
                    text += "${subAssignment.time.hour}`${subAssignment.time.minute}`"
                    text += "${subAssignment.estimatedTime.hour}`${subAssignment.estimatedTime.minute}`"
                    text += "${subAssignment.movable}`"
                }
                text += "\n"

                assignmentsOutputWriter.write(text)
            }

            assignmentsOutputWriter.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isFree (date: Date, time: Time): Boolean {
        for (classObj in classByDayOfWeek[date.getDayOfWeek()]) {
            if (classObj.startPeriod != 11) {
                if (isBetween(timeConverter[classObj.startPeriod - 1], time, timeConverter[classObj.startPeriod])) {
                    return false
                }
            }
        }
        if (meetingByDate[date] !== null) {
            for (meeting in meetingByDate[date]!!) {
                if (isBetween(meeting.startTime, time, meeting.endTime)) {
                    return false
                }
            }
        }
        if (assignmentByDate[date] !== null) {
            for (assignment in assignmentByDate[date]!!) {
                if (isBetween(assignment.time, time, assignment.time + assignment.estimatedTime)) {
                    return false
                }
            }
        }
        return true
    }

    private fun isBetween (a: Time, b: Time, c: Time): Boolean {
        if (a <= b && b <= c) {
            return true
        }
        return false
    }

    fun totalTimeInDay (): Int {
        return (endTime - startTime).hour
    }

    fun classNameExists (className: String): Boolean {
        return classes.containsKey(className)
    }

    fun addClass (className: String, classObj: Class) {
        classes.getOrPut(className) { ArrayList() }.add(classObj)
        classByDayOfWeek[classObj.dayOfWeek].add(classObj)
    }

    fun addMeeting (meetingObj: Meeting) {
        meetings[meetingObj.id] = meetingObj
        meetingByDate.getOrPut(meetingObj.date) { ArrayList() }.add(meetingObj)
    }

    fun addAssignment (assignment: Assignment) {
        assignments[assignment.id] = assignment
        sortedAssignment.add(assignment)
        sort()
        schedule()
    }

    fun removeClass (className: String) {
        val classObjs = MainActivity.dataManager.classes[className]!!
        for (classObj in classObjs) {
            classByDayOfWeek[classObj.dayOfWeek].remove(classObj)
        }
        classes.remove(className)
    }

    fun removeClassObj (classObj: Class) {
        classByDayOfWeek[classObj.dayOfWeek].remove(classObj)
        classes[classObj.className]!!.remove(classObj)
    }

    fun removeMeeting (meetingObj: Meeting) {
        meetings.remove(meetingObj.id)
        meetingByDate[meetingObj.date]!!.remove(meetingObj)
    }

    fun removeAssignment (assignment: Assignment, change: Boolean=true) {
        assignments.remove(assignment.id)
        sortedAssignment.remove(assignment)
        for (subAssignment in assignment.subAssignments) {
            assignmentByDate[subAssignment.date]?.remove(subAssignment)
        }
        if (change) {
            schedule()
        }
    }

    fun changeClassColor (className: String, classColor: String) {
        classColors[className] = classColor
    }

    fun changeClass (className: String, position: Int, dayOfWeek: Int, startPeriod: Int, isDoubleClass: Boolean) {
        val classObj: Class = classes[className]!![position]
        classByDayOfWeek[classObj.dayOfWeek].remove(classObj)
        classObj.dayOfWeek = dayOfWeek
        classObj.startPeriod = startPeriod
        classObj.isDoubleClass = isDoubleClass
        classByDayOfWeek[dayOfWeek].add(classObj)
    }

    fun changeClassKey (oldKey: String, newKey: String) {
        classes[newKey] = classes.remove(oldKey)!!
        classColors[newKey] = classColors.remove(oldKey)!!
    }

    fun editMeeting (id: Int, name: String, date: Date, startTime: Time, endTime: Time, color: String) {
        meetings[id]!!.name = name
        meetings[id]!!.date = date
        meetings[id]!!.startTime = startTime
        meetings[id]!!.endTime = endTime
        meetings[id]!!.color = color
    }

    fun editAssignment (id: Int, name: String, dueDate: Date, dueTime: Time, estimatedTime: Time, priority: Int, color: String) {
        val subAssignments = assignments[id]!!.subAssignments
        removeAssignment(assignments[id]!!)
        addAssignment(Assignment(name, dueDate, dueTime, estimatedTime, priority, subAssignments, color, id))
    }

    private fun sort () {
        for (i in sortedAssignment.lastIndex downTo 0) {
            if (sortedAssignment.last() > sortedAssignment[i]) {
                Collections.swap(sortedAssignment, i, sortedAssignment.lastIndex)
                break
            }
        }
    }

    private fun createDummy (date: Date): ArrayList<Dummy> {
        val todaySchedule = ArrayList<Dummy>()

        /*
        fun sortDummy (dummy: Dummy) {
            if (todaySchedule.isEmpty()) {
                todaySchedule.add(dummy)
                for (temp in todaySchedule) {
                    Log.d("Test", temp.toString())
                }
                Log.d("Test", "0")
            } else {
                if (dummy.startTime > todaySchedule.last().startTime) {
                    todaySchedule.add(dummy)
                    for (temp in todaySchedule) {
                        Log.d("Test", temp.toString())
                    }
                    Log.d("Test", "1")
                    return
                }
                for (i in todaySchedule.lastIndex downTo 0) {
                    if (dummy.startTime < todaySchedule[i].startTime) {
                        todaySchedule.add(i, dummy)
                        for (temp in todaySchedule) {
                            Log.d("Test", temp.toString() + "$i")
                        }
                        Log.d("Test", "2")
                        return
                    }
                }
            }
        }
         */

        for (classObj in classByDayOfWeek[date.getDayOfWeek()]) {
            if (classObj.isDoubleClass) {
                todaySchedule.add(Dummy(timeConverter[classObj.startPeriod - 1],
                    timeConverter[classObj.startPeriod - 1] + 120))
            } else {
                todaySchedule.add(Dummy(timeConverter[classObj.startPeriod - 1],
                    timeConverter[classObj.startPeriod - 1] + 60))
            }
        }
        if (meetingByDate[date] !== null) {
            for (meeting in meetingByDate[date]!!) {
                todaySchedule.add(Dummy(meeting.startTime, meeting.endTime))
            }
        }
        if (assignmentByDate[date] !== null) {
            for (assignment in assignmentByDate[date]!!) {
                todaySchedule.add(Dummy(assignment.time, assignment.time + assignment.estimatedTime))
            }
        }
        todaySchedule.add(Dummy(Time(12, 40), Time(13, 40)))
        todaySchedule.add(Dummy(Time(18, 30), Time(19, 30)))
        todaySchedule.sortWith(Comparator { o1, o2 ->
            when {
                o1.startTime > o2.startTime -> 1
                o1.startTime < o2.startTime -> -1
                else -> 0
            }
        })
        return todaySchedule
    }

    fun schedule () {
        val c = Calendar.getInstance()
        var date = Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
        var time = Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
        if (time < startTime) {
            time = startTime.copy()
        }
        if (!isFree(date, time)) {
            time = createDummy(date).first().startTime
        }
        assignmentByDate.clear()
        for (assignment in sortedAssignment) {
            assignment.subAssignments =
                assignment.subAssignments.filter { subAssignment -> !subAssignment.movable } as ArrayList<SubAssignment>
            for (temp in assignment.subAssignments) {
                assignmentByDate.getOrPut(temp.date){ ArrayList() }.add(temp)
            }
        }
        for (assignment in sortedAssignment) {
            var timeLeft = assignment.estimatedTime.time
            for (left in assignment.subAssignments) {
                timeLeft -= left.estimatedTime.time
            }
            while (timeLeft > 0) {
                val todaySchedule = createDummy(date)
                var indexOfToday = 0
                while (todaySchedule.size > indexOfToday && todaySchedule[indexOfToday].startTime < time) {
                    indexOfToday += 1
                }
                while (todaySchedule.size > indexOfToday &&
                    (todaySchedule[indexOfToday].startTime - time).time < 30) {
                    time = todaySchedule[indexOfToday].endTime
                    indexOfToday += 1
                }
                if (date >= assignment.dueDate && time > assignment.dueTime) {
                    Toast.makeText(MainActivity.appContext, "Impossible!", Toast.LENGTH_LONG).show()
                    return
                }
                if (todaySchedule.size > indexOfToday) {
                    val nextTask = todaySchedule[indexOfToday]
                    if ((nextTask.startTime - time).time < timeLeft) {
                        assignment.subAssignments.add(SubAssignment(date, time, (nextTask.startTime - time), true, assignment))
                        timeLeft -= (nextTask.startTime - time).time
                    } else {
                        val forTime = Time(timeLeft / 60, timeLeft % 60)
                        assignment.subAssignments.add(SubAssignment(date, time, forTime, true, assignment))
                        timeLeft = 0
                    }
                    assignmentByDate.getOrPut(date){ ArrayList() }.add(assignment.subAssignments.last())
                    indexOfToday += 1
                    time = nextTask.endTime
                }
                if (todaySchedule.size <= indexOfToday){
                    if ((endTime - time).time > 30) {
                        if ((endTime - time).time < timeLeft) {
                            assignment.subAssignments.add(SubAssignment(date, time, (endTime - time), true, assignment))
                            timeLeft -= (endTime - time).time
                        } else {
                            val forTime = Time(timeLeft / 60, timeLeft % 60)
                            assignment.subAssignments.add(SubAssignment(date, time, forTime, true, assignment))
                            timeLeft = 0
                        }
                        assignmentByDate.getOrPut(date){ ArrayList() }.add(assignment.subAssignments.last())
                    }
                    if (timeLeft > 0) {
                        date += 1
                        time = Time(8, 50)
                    }
                }
            }
        }
        mainActivity.rvAdapter.notifyDataSetChanged()
    }

}