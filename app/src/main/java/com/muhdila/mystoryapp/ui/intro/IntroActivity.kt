package com.muhdila.mystoryapp.ui.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.helper.ViewModelFactory
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import com.muhdila.mystoryapp.databinding.ActivityIntroBinding
import com.muhdila.mystoryapp.ui.home.HomeActivity
import com.muhdila.mystoryapp.ui.login.LoginActivity

// Define a DataStore for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Hide the ActionBar
        supportActionBar?.hide()

        // Initialize DataStore for user preferences
        val pref = UserPreferences.getInstance(dataStore)

        // Initialize UserRepository and LoginViewModel
        initUserRepository(pref)

        // Observe login status
        observeUserLoginStatus()

        // Handle "Get Started" button click
        binding.btnStarted.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initUserRepository(pref: UserPreferences) {
        userRepository = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[UserRepository::class.java]
    }

    private fun observeUserLoginStatus() {
        userRepository.getUser().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}