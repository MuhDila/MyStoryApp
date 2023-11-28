package com.muhdila.mystoryapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.data.helper.loadImage
import com.muhdila.mystoryapp.data.pref.StoryModel
import com.muhdila.mystoryapp.databinding.ActivityDetailBinding
import com.muhdila.mystoryapp.ui.home.HomeActivity


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Set title action bar
        setTitle(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Retrieve the StoryModel from the intent
        val story = intent.getParcelableExtra<StoryModel>(DETAIL_STORY) as StoryModel

        // Load the image, name, and description from the StoryModel and set them to the UI components
        with(binding) {
            image.loadImage(story.image.toString()) // Load and display the image
            binding.name.text = story.name // Set the story name
            binding.description.text = story.description // Set the story description
        }
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    companion object {
        const val DETAIL_STORY = "detail_story" // Constant key for passing StoryModel in intent
    }
}
