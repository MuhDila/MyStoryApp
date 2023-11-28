package com.muhdila.mystoryapp.ui.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.helper.ViewModelFactory
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.ui.register.RegisterViewModel.RegistrationResult
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import com.muhdila.mystoryapp.databinding.ActivityRegisterBinding
import com.muhdila.mystoryapp.ui.login.LoginActivity

// Define a DataStore for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Hide the ActionBar
        supportActionBar?.hide()

        // Initialize DataStore for user preferences
        val pref = UserPreferences.getInstance(dataStore)

        // Initialize ViewModel
        initRegisterViewModel(pref)

        observeRegisterViewModel()
        setOnClickListeners()
    }

    private fun initRegisterViewModel(pref: UserPreferences) {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[RegisterViewModel::class.java]
    }

    private fun observeRegisterViewModel() {
        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        registerViewModel.registrationResult.observe(this) { registrationResult ->
            when (registrationResult) {
                is RegistrationResult.Success -> {
                    // Registration was successful
                    showToast(getString(R.string.register_success))
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is RegistrationResult.Error -> {
                    // Registration failed
                    showToast(getString(R.string.register_failed))
                }

                is RegistrationResult.NetworkError -> {
                    // Network error
                    showToast(getString(R.string.register_failed))
                }
            }
        }
    }

    private fun setOnClickListeners() {
        // Handle "Sign Up" button click
        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Trigger the registration process in the ViewModel
            registerViewModel.register(name, email, password)
        }

        // Handle "Sign In" button click
        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Function to show or hide loading indicator
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
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}