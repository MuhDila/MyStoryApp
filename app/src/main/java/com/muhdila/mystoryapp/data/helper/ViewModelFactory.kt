package com.muhdila.mystoryapp.data.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.data.remote.retforit.ApiService
import com.muhdila.mystoryapp.ui.login.LoginViewModel
import com.muhdila.mystoryapp.ui.maps.MapsViewModel
import com.muhdila.mystoryapp.ui.register.RegisterViewModel
import com.muhdila.mystoryapp.ui.upload.UploadViewModel

// Factory class for creating ViewModels with dependencies
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val apiService: ApiService,
    private val pref: UserPreferences,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(apiService, userRepository) as T
            }
            modelClass.isAssignableFrom(UserRepository::class.java) -> {
                UserRepository(pref) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(apiService, userRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                // Create and return MapViewModel
                MapsViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}