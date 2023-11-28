package com.muhdila.mystoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muhdila.mystoryapp.data.helper.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.muhdila.mystoryapp.data.remote.response.LoginResponse
import com.muhdila.mystoryapp.data.remote.retforit.ApiService
import com.muhdila.mystoryapp.data.pref.UserModel

class LoginViewModel(
    private val apiService: ApiService,
    private val userRepository: UserRepository
) : ViewModel() {
    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to observe the login result
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // Sealed class to represent different login results
    sealed class LoginResult {
        data class Success(val token: String) : LoginResult()
        data object Error : LoginResult()
        data object NetworkError : LoginResult()
    }

    // Function to initiate the login process
    fun login(email: String, password: String) {
        _isLoading.value = true
        apiService.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    val token = responseBody?.loginResult?.token
                    if (token != null) {
                        userRepository.saveUser(UserModel(token, true))
                        _loginResult.value = LoginResult.Success(token)
                    } else {
                        _loginResult.value = LoginResult.Error
                    }
                } else {
                    _loginResult.value = LoginResult.Error
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _loginResult.value = LoginResult.NetworkError
            }
        })
    }
}