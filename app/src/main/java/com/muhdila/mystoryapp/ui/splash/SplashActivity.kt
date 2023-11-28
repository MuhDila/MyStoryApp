package com.muhdila.mystoryapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.muhdila.mystoryapp.R
import com.muhdila.mystoryapp.custom.NavBarColor
import com.muhdila.mystoryapp.ui.intro.IntroActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)

        // Hide the ActionBar
        supportActionBar?.hide()

        val delayMillis = 3000L

        // Animation
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish()
        }, delayMillis)

        // Setting color status bar and navigator bar
        NavBarColor.setStatusBarAndNavBarColors(this)
    }
}