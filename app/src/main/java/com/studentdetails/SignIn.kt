package com.studentdetails

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.studentdetails.databinding.FragmentSignInBinding

class SignIn : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textView2.setOnClickListener {
                findNavController().navigate(R.id.action_signIn_to_signUp)
            }
            btnLogin.setOnClickListener {
                validation()
            }

        }

    }
    private fun validation() {
        binding.apply {
            val userName = edEmail.text.toString()
            val password = edPassword.text.toString()
            when {
                userName.isEmpty() -> showError("Username is empty")
                password.isEmpty() -> showError("password is empty")
                else -> {
                    startActivity(Intent(requireActivity(),DashboardScreen::class.java))

                }

            }

        }
    }



    private fun showError(message: String) {
        Utils.showSnackbar(binding.root, message, R.color.white, Snackbar.LENGTH_SHORT)
    }
}