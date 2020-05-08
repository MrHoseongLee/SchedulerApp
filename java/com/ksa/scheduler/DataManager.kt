package com.ksa.scheduler

import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception

class DataManager {

    companion object {
        fun loadData(): Data {
            val floatingTasks : HashMap<Date, ArrayList<Task>> = HashMap()
            try {
                val floatingTasksInputStream: FileInputStream =
                    MainActivity.appContext.openFileInput("floatingTasksData.txt")
                val inputRead = InputStreamReader(floatingTasksInputStream)
                val contents: List<String> = inputRead.readLines()
                for(content in contents) {
                    val splitData: List<String> = content.split(" ")
                    val splitInts: Array<Int> = Array(7) { i -> splitData[i].toInt()}
                    val dueDate = Date(splitInts[0], splitInts[1], splitInts[2])
                    val dueTime = Time(splitInts[3], splitInts[4])
                    val estimatedTime = Time(splitInts[5], splitInts[6])
                    floatingTasks.getOrPut(dueDate){ ArrayList() }.add(Task(dueDate, dueTime, estimatedTime))
                }
                inputRead.close()
            } catch (e:Exception) {
                e.printStackTrace()
            }
            return Data(floatingTasks, Time(7, 0), Time(24, 0))
        }

        fun saveData() {
            try {
                val floatingTasksOutputStream: FileOutputStream =
                    MainActivity.appContext.openFileOutput("floatingTasksData.txt", MODE_PRIVATE)
                val outputWriter = OutputStreamWriter(floatingTasksOutputStream)
                outputWriter.write("2020 5 7 10 0 12 0\n")
                outputWriter.write("2020 5 8 11 0 10 0\n")
                outputWriter.write("2020 5 9 10 0 8 0\n")
                outputWriter.write("2020 5 10 15 0 5 0\n")
                outputWriter.write("2020 5 11 12 0 5 0\n")
                outputWriter.close()

                Toast.makeText(MainActivity.appContext, "File saved successfully!",
                    Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}