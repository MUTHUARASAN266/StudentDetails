package com.studentdetails.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.studentdetails.databinding.ActivitySplashBinding

class DemoScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            handler.postDelayed(Runnable {
                startActivity(Intent(this@DemoScreen, OnboardingScreen::class.java))
                finish()
            }, 2000)
        }
    }
}