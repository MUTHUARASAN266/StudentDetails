package com.studentdetails.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.studentdetails.databinding.ActivitySplashBinding

class DemoScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        binding.apply {
            handler.postDelayed(Runnable {
                checkLoginState()
                finish()
            }, 2000)
        }
    }

    private fun checkLoginState() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            startActivity(Intent(this, DashboardScreen::class.java))
            finish()
        } else {
            startActivity(Intent(this, OnboardingScreen::class.java))
            finish()
        }

    }
}