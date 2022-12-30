package com.example.app.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.dao.Database
import com.example.app.models.User

class AccountViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>().apply {
        value = Database.getUser()
    }
    val user: LiveData<User?> = _user
}
