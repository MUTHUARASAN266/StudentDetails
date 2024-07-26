package com.studentdetails

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.studentdetails.databinding.ActivitySplashBinding

class DemoScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLoginScreen.setOnClickListener {
                startActivity(Intent(this@DemoScreen,OnboardingScreen::class.java))
            }
        }
    }
}