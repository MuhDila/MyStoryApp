package com.muhdila.mystoryapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.helper.ViewModelFactory
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import com.muhdila.mystoryapp.databinding.ActivityHomeBinding
import com.muhdila.mystoryapp.ui.intro.IntroActivity
import com.muhdila.mystoryapp.ui.maps.MapsActivity
import com.muhdila.mystoryapp.ui.upload.UploadActivity

// Define a DataStore for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userRepository: UserRepository
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModel.ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Action bar
        supportActionBar?.setLogo(R.drawable.ic_logo_mini)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        // Initialize DataStore for user preferences
        val pref = UserPreferences.getInstance(dataStore)

        // Initialize UserRepository and StoryViewModel
        initUserRepository(pref)

        // Set up the UI components
        setupUI()

        // Handle floating button click
        binding.fltUpload.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    // Initialize action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    // Function action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
            }
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return true
    }

    // Initialize UserRepository
    private fun initUserRepository(pref: UserPreferences) {
        userRepository = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[UserRepository::class.java]
    }

    private fun setupUI() {
        // Set up RecyclerView and its adapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        // Initialize and display the list of stories
        getStory()
    }

    private fun getStory() {
        // Initialize and set up the RecyclerView adapter
        val adapter = StoryAdapter()

        binding.rvStory.adapter = adapter

        // Observe user authentication status
        userRepository.getUser().observe(this) { userAuth ->
            if (userAuth != null) {
                // Fetch and display stories using StoryViewModel
                storyViewModel.stories("Bearer " + userAuth.token).observe(this) { stories ->
                    adapter.submitData(lifecycle, stories)
                }
            }
        }
    }

    private fun logout() {
        // Log out the user and navigate to the LoginActivity
        userRepository.logout()
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    // Dialog
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirm_logout_title))
            .setMessage(getString(R.string.confirm_logout_message))
            .setPositiveButton(getString(R.string.confirm_logout_yes)) { _, _ ->
                logout()
            }
            .setNegativeButton(getString(R.string.confirm_logout_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private var doubleBackToExitPressedOnce = false

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000) // Reset the flag after 2 seconds
    }

    override fun onPause() {
        super.onPause()
        finish() // Finish the activity when it goes into the background
    }
}