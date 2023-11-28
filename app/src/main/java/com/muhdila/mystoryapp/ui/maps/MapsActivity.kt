package com.muhdila.mystoryapp.ui.maps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.helper.ViewModelFactory
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import com.muhdila.mystoryapp.databinding.ActivityMapsBinding
import com.muhdila.mystoryapp.ui.home.HomeActivity

// Create a DataStore for user preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Appbar and Navbar
        NavBarColor.setStatusBarAndNavBarColors(this)
        supportActionBar?.title = getString(R.string.maps_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize the UserPreferences DataStore
        val pref = UserPreferences.getInstance(dataStore)

        // Create the UserRepository with ViewModelFactory
        userRepository = ViewModelProvider(this, ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref)))[UserRepository::class.java]

        // Create the MapViewModel using ViewModelFactory
        val mapsViewModelFactory = ViewModelFactory(ApiConfig.getApiService(), pref, userRepository)
        mapsViewModel = ViewModelProvider(this, mapsViewModelFactory)[MapsViewModel::class.java]

        // Get the Google Map from the layout
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Fetch user stories using the ViewModel
        mapsViewModel.fetchUserStories()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Callback when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        // Default location (e.g., Jakarta)
        val jakarta = LatLng(-6.23, 106.76)

        // Observe the list of stories and add markers to the map
        mapsViewModel.listStoriesLocation.observe(this) { stories ->
            stories.forEach { story ->
                val location = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions().position(location)
                        .title(getString(R.string.story_by) + story.name)
                )
            }

            // Animate the camera to the default location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 2f))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}