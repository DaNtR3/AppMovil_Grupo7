package com.proyecto.sistemaventaspropat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyecto.sistemaventaspropat.models.User

class UserViewModel : ViewModel() {

    // LiveData to observe the user data
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    // Method to set or update the user data
    fun setUser(user: User) {
        _user.value = user
    }


    // Additional methods related to user operations can be added here
}
