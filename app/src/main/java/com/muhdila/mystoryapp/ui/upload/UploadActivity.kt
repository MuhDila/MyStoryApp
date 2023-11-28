package com.muhdila.mystoryapp.ui.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.helper.ViewModelFactory
import com.muhdila.mystoryapp.data.helper.rotateBitmap
import com.muhdila.mystoryapp.data.helper.uriToFile
import com.muhdila.mystoryapp.data.pref.UserPreferences
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import com.muhdila.mystoryapp.databinding.ActivityUploadBinding
import com.muhdila.mystoryapp.ui.camera.CameraActivity
import com.muhdila.mystoryapp.ui.home.HomeActivity
import java.io.File

// Define a DataStore for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var userRepository: UserRepository
    private lateinit var uploadViewModel: UploadViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Set title action bar
        setTitle(R.string.upload_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize DataStore for user preferences
        val pref = UserPreferences.getInstance(dataStore)

        // Initialize UserRepository and UploadViewModel
        initUserRepository(pref)
        initUploadViewModel(pref)

        // Request permissions if not granted
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Set click listeners for buttons
        setOnClickListeners()

        // Observe the upload ViewModel for results and loading status
        observeUploadViewModel()
    }

    // Action bar function
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUserRepository(pref: UserPreferences) {
        userRepository = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[UserRepository::class.java]
    }

    private fun initUploadViewModel(pref: UserPreferences) {
        uploadViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), pref, UserRepository(pref))
        )[UploadViewModel::class.java]
    }

    private fun setOnClickListeners() {
        // Start the camera activity
        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        // Open the image gallery
        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        // Upload the selected image
        binding.uploadBtn.setOnClickListener {
            uploadStory()
        }
    }

    private fun observeUploadViewModel() {
        // Observe the upload result and loading status
        uploadViewModel.uploadResult.observe(this) { result ->
            when (result) {
                is UploadViewModel.UploadResult.Success -> {
                    showToast(getString(R.string.upload_success))
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is UploadViewModel.UploadResult.Error -> {
                    showToast(getString(R.string.upload_fail))
                }
                is UploadViewModel.UploadResult.NetworkError -> {
                    showToast(getString(R.string.upload_fail))
                }
            }
        }

        uploadViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun startCamera() {
        // Start the camera activity using ActivityResultContracts
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        // Open the image gallery using ActivityResultContracts
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        if (getFile != null) {
            // Trigger the upload process through the ViewModel
            uploadViewModel.uploadStory(getFile, binding.tfDescription.text.toString())
        } else {
            showToast(getString(R.string.select_image_first))
        }
    }

    // Handle the result from the camera activity
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.image.setImageBitmap(result)
        }
    }

    // Handle the result from the image gallery
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding.image.setImageURI(selectedImg)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun showLoading(isLoading: Boolean) = binding.progressBar.isVisible == isLoading

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}