package com.muhdila.mystoryapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.ViewModelFactory
import com.muhdila.mystoryapp.databinding.ActivityLoginBinding
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import com.muhdila.mystoryapp.ui.register.RegisterActivity
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.ui.home.HomeActivity
import com.muhdila.mystoryapp.ui.intro.IntroActivity

// Define a DataStore for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Hide the ActionBar
        supportActionBar?.hide()

        // Initialize DataStore for user preferences
        val pref = UserPreferences.getInstance(dataStore)

        // Initialize UserRepository and LoginViewModel
        initUserRepository(pref)
        initLoginViewModel(pref)

        observeUserLoginStatus()
        observeLoginViewModel()

        setOnClickListeners()
    }

    private fun initUserRepository(pref: UserPreferences) {
        userRepository = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[UserRepository::class.java]
    }

    private fun initLoginViewModel(pref: UserPreferences) {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[LoginViewModel::class.java]
    }

    private fun observeUserLoginStatus() {
        userRepository.getUser().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun observeLoginViewModel() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        loginViewModel.loginResult.observe(this) { loginResult ->
            when (loginResult) {
                is LoginViewModel.LoginResult.Success -> {
                    // Login was successful
                    showToast(getString(R.string.login_success))
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is LoginViewModel.LoginResult.Error -> {
                    // Login failed
                    showToast(getString(R.string.login_failed))
                }
                is LoginViewModel.LoginResult.NetworkError -> {
                    // Network error
                    showToast(getString(R.string.login_failed))
                }
            }
        }
    }

    private fun setOnClickListeners() {
        // Handle "Sign In" button click
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            // Trigger the login process in the LoginViewModel
            loginViewModel.login(email, pass)
        }

        // Handle "Sign Up" button click
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    // Function to show or hide the loading indicator
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Function to display a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }
}