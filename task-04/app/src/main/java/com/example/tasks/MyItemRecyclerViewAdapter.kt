package com.example.tasks

import android.annotation.SuppressLint
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.databinding.FragmentItemBinding

class MyItemRecyclerViewAdapter(
    private val taskViewModel: TaskViewModel,
    private val parentView: RecyclerView
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = taskViewModel.getTask(position)
        holder.statusView.text = item.status
        holder.dateView.text = item.date
        holder.nameView.text = item.name
        holder.descView.text = item.desc

        holder.rootView.setOnTouchListener(object : OnSwipeTouchListener(holder.rootView.context) {
            override fun onSwipeRight() {
                taskViewModel.removeTask(holder.absoluteAdapterPosition)
                parentView.adapter?.notifyItemRemoved(holder.absoluteAdapterPosition)
                val dbHelper = DBHelper(holder.rootView.context, null)
                dbHelper.removeTask(item.name, item.desc, item.date, item.status)
            }

            override fun onSwipeLeft() {
                taskViewModel.removeTask(holder.absoluteAdapterPosition)
                parentView.adapter?.notifyItemRemoved(holder.absoluteAdapterPosition)
                val dbHelper = DBHelper(holder.rootView.context, null)
                dbHelper.removeTask(item.name, item.desc, item.date, item.status)
            }
        })
    }

    override fun getItemCount(): Int = taskViewModel.getSize()

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val rootView = binding.root
        val statusView: TextView = binding.status
        val dateView: TextView = binding.date
        val nameView: TextView = binding.name
        val descView: TextView = binding.desc
    }
}
