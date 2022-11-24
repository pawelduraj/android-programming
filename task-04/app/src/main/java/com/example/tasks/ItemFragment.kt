package com.example.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

class ItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        if (view is RecyclerView) with(view) {
            val taskViewModel: TaskViewModel by viewModels()
            val dbHelper = DBHelper(context, null)
            initDatabase(dbHelper, taskViewModel)
            layoutManager = LinearLayoutManager(context)
            adapter = MyItemRecyclerViewAdapter(taskViewModel, view)
        }
        return view
    }

    @SuppressLint("Range")
    private fun initDatabase(dbHelper: DBHelper, taskViewModel: TaskViewModel) {
        var tasksExists = false
        val cursor = dbHelper.getTasks()
        cursor!!.moveToFirst()
        while (!cursor.isAfterLast) {
            tasksExists = true
            taskViewModel.createTask(
                cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COL)),
                cursor.getString(cursor.getColumnIndex(DBHelper.DESC_COL)),
                cursor.getString(cursor.getColumnIndex(DBHelper.DATE_COL)),
                cursor.getString(cursor.getColumnIndex(DBHelper.STATUS_COL))
            )
            cursor.moveToNext()
        }
        cursor.close()
        if (!tasksExists) {
            dbHelper.addTask("Task 1 Docker", "ubuntu, python, java, kotlin, gradle, docker-compose", "2022-10-31", "DONE")
            taskViewModel.createTask("Task 1 Docker", "ubuntu, python, java, kotlin, gradle, docker-compose", "2022-10-31", "DONE")
            dbHelper.addTask("Task 2 Ktor", "ngrok, crud, rest", "2022-11-03", "DONE")
            taskViewModel.createTask("Task 2 Ktor", "ngrok, crud, rest", "2022-11-03", "DONE")
            dbHelper.addTask("Task 3 Calculator", "", "2022-11-16", "DONE")
            taskViewModel.createTask("Task 3 Calculator", "", "2022-11-16", "DONE")
            dbHelper.addTask("Task 4 ToDo List", "fragments, sqlite, data binding, swipe", "2022-11-24", "DONE")
            taskViewModel.createTask("Task 4 ToDo List", "fragments, sqlite, data binding, swipe", "2022-11-24", "DONE")
        }
    }
}
