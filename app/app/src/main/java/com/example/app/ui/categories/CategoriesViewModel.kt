package com.example.app.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.dao.Database
import com.example.app.models.Category

class CategoriesViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>().apply {
        value = Database.getCategories()
    }
    val categories: LiveData<List<Category>> = _categories
}
