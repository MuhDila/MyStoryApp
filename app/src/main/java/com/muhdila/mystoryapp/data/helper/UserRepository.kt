package com.muhdila.mystoryapp.data.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.muhdila.mystoryapp.data.pref.UserModel
import com.muhdila.mystoryapp.data.pref.UserPreferences
import kotlinx.coroutines.launch

class UserRepository(private val pref: UserPreferences) : ViewModel() {

    // Get the user data as LiveData
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    // Save user data using a coroutine
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    // Sign out the user using a coroutine
    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}