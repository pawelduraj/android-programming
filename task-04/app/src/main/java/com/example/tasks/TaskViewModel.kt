package com.example.tasks

import androidx.lifecycle.ViewModel

data class Task(
    val name: String,
    val desc: String,
    val date: String,
    val status: String,
)

class TaskViewModel : ViewModel() {
    private val _tasks = ArrayList<Task>()

    fun createTask(name: String, desc: String, date: String, status: String) {
        _tasks.add(Task(name, desc, date, status))
    }

    fun removeTask(id: Int) {
        _tasks.removeAt(id)
    }

    fun getTask(id: Int): Task {
        return _tasks[id]
    }

    fun getSize(): Int {
        return _tasks.size
    }
}
