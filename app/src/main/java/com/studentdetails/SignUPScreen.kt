package com.studentdetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.studentdetails.databinding.ActivitySignupscreenBinding

class SignUPScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySignupscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            textView2.setOnClickListener {
                startActivity(Intent(this@SignUPScreen, LoginScreen::class.java))
            }
            btnLogin.setOnClickListener {
                validation()
            }
        }

    }

    private fun validation() {
        binding.apply {
            val userName = edUsername.text.toString()
            val phoneNumber = edPhoneNumber.text.toString()
            val password = edSignupPassword.text.toString()
            val confirmPassword = edSignupConfirmPassword.text.toString()

            when {
                userName.isEmpty() -> showError("Username is empty")
                phoneNumber.isEmpty() -> showError("Phone number is empty")
                !isValidPhoneNumber(phoneNumber) -> showError("Invalid phone number")
                password.isEmpty() -> showError("Password is empty")
                !isStrongPassword(password) -> showError("Password is too weak")
                confirmPassword.isEmpty() -> showError("Confirm password is empty")
                password != confirmPassword -> showError("Password and confirm password do not match")
                else -> {
                    Utils.showSnackbar(binding.root, "Student data saved successfully", R.color.white, Snackbar.LENGTH_SHORT)
                    Log.e("TAG", "validation: Student data saved successfully")
                }
            }
        }
    }

    private fun showError(message: String) {
        Utils.showSnackbar(binding.root, message, R.color.white, Snackbar.LENGTH_SHORT)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length == 10
    }

    private fun isStrongPassword(password: String): Boolean {

        return password.length >= 8
    }

}