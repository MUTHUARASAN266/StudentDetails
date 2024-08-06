package com.studentdetails.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.studentdetails.R
import com.studentdetails.Utils
import com.studentdetails.databinding.FragmentSignUpBinding
import com.studentdetails.model.User
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory


class SignUp : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private val studentViewModel: StudentViewModel by viewModels {
        StudentViewModelFactory(StudentRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding.apply {
            textView2.setOnClickListener {
                findNavController().navigate(R.id.action_signUp_to_signIn)
            }

            btnSignUp.setOnClickListener {
                validation()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }
    }

    private fun validation() {
        binding.apply {
            val userName = edUsername.text.toString()
//            val phoneNumber = edPhoneNumber.text.toString()
            val email = edPhoneNumber.text.toString()
            val password = edSignupPassword.text.toString()
            val confirmPassword = edSignupConfirmPassword.text.toString()

            when {
                userName.isEmpty() -> showError("Username is empty")
                email.isEmpty() -> showError("email is empty")
                // !isValidPhoneNumber(phoneNumber) -> showError("Invalid phone number")
                password.isEmpty() -> showError("Password is empty")
                !isStrongPassword(password) -> showError("Password is too weak, it must be 8 letters")
                confirmPassword.isEmpty() -> showError("Confirm password is empty")
                password != confirmPassword -> showError("Password and confirm password do not match")
                else -> {
                    studentViewModel.signUp(
                        User(
                            name = userName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    )
                    binding.progressCircular.visibility = View.VISIBLE
                    // userSignUp(userName, phoneNumber, password, confirmPassword)
                    userAuth()
                    Log.e(TAG, "validation: Student data saved successfully")
                    clearText()
                }
            }
        }
    }

    private fun clearText() {
        binding.apply {
            edUsername.text?.clear()
            edPhoneNumber.text?.clear()
            edSignupPassword.text?.clear()
            edSignupConfirmPassword.text?.clear()
        }
    }

    private fun userAuth() {
        studentViewModel.signUpStatus.observe(requireActivity(), Observer { success ->
            if (success) {
                Utils.showSnackbar(
                    binding.root,
                    "Sign Up Successful",
                    R.color.white,
                    Snackbar.LENGTH_SHORT
                )
                binding.progressCircular.visibility = View.GONE
                // Navigate to next activity
                findNavController().navigate(R.id.action_signUp_to_signIn)

            }
        })

        studentViewModel.errorMessage.observe(requireActivity(), Observer { message ->
            message?.let {
                binding.progressCircular.visibility = View.GONE
                Utils.showSnackbar(
                    binding.root,
                    it,
                    R.color.white,
                    Snackbar.LENGTH_SHORT
                )
            }
        })
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

    companion object {
        const val TAG = "SignUp"
    }
}