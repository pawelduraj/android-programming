package com.example.app.ui.categories

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.app.R
import com.example.app.databinding.FragmentCategoriesBinding
import com.example.app.models.Category

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val categoriesListView: ListView = binding.listViewCategories
        val categoriesAdapter =
            CategoriesAdapter(this.requireContext(), categoriesViewModel.categories.value!!)
        categoriesListView.adapter = categoriesAdapter
        categoriesViewModel.categories.observe(viewLifecycleOwner) {
            categoriesAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CategoriesAdapter(context: Context, categories: List<Category>) :
    ArrayAdapter<Category>(context, R.layout.list_item_categories, categories) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val category = getItem(position) as Category

        val rowView = inflater.inflate(R.layout.list_item_categories, parent, false)

        val nameTextView = rowView.findViewById(R.id.name) as TextView
        nameTextView.text = category.name

        return rowView
    }
}
