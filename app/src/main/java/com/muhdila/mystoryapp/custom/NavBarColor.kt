package com.muhdila.mystoryapp.custom

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.muhdila.mystoryapp.R

object NavBarColor {
    @Suppress("DEPRECATION")
    fun setStatusBarAndNavBarColors(activity: AppCompatActivity) {
        val theme = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (theme == Configuration.UI_MODE_NIGHT_NO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }

            val statusBarColor = ContextCompat.getColor(activity, R.color.md_theme_light_background)
            val navigatorBarColor = ContextCompat.getColor(activity, R.color.md_theme_light_background)
            val actionBar = activity.supportActionBar

            activity.window.statusBarColor = statusBarColor
            activity.window.navigationBarColor = navigatorBarColor
            actionBar?.setBackgroundDrawable(ColorDrawable(activity.resources.getColor(R.color.md_theme_light_background)))
        } else {
            // For dark theme
            val statusBarColor = ContextCompat.getColor(activity, R.color.md_theme_dark_background)
            val navigatorBarColor = ContextCompat.getColor(activity, R.color.md_theme_dark_background)

            activity.window.statusBarColor = statusBarColor
            activity.window.navigationBarColor = navigatorBarColor
        }
    }
}