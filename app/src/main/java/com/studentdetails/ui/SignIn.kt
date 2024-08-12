package com.studentdetails.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
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
import com.studentdetails.databinding.FragmentSignInBinding
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignIn : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var sharedPreferences: SharedPreferences
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
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", MODE_PRIVATE)


        auth = FirebaseAuth.getInstance()
        binding.apply {
            textView2.setOnClickListener {
                findNavController().navigate(R.id.action_signIn_to_signUp)
            }
            btnLogin.setOnClickListener {
                validation()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
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
                    CoroutineScope(Dispatchers.Main).launch {
                        progressCircular.visibility = View.VISIBLE
                    }
                    studentViewModel.login(userName, password)
                    userAuth()
                    clearText()
                }

            }

        }
    }

    private fun clearText() {
        binding.apply {
            edEmail.text?.clear()
            edPassword.text?.clear()
        }
    }

    private fun saveLoginState() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun userAuth() {
        studentViewModel.loginStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Utils.showSnackbar(
                    binding.root,
                    "Login Successful",
                    R.color.white,
                    Snackbar.LENGTH_SHORT
                )
                binding.progressCircular.visibility = View.GONE
                Log.e(TAG, "userAuth: Login Successful")
                saveLoginState()
                startActivity(Intent(requireActivity(), DashboardScreen::class.java))
            }
        }

        studentViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.progressCircular.visibility = View.GONE
            message?.let {
                Utils.showSnackbar(binding.root, it, R.color.white, Snackbar.LENGTH_SHORT)
            }
        })
    }


    private fun showError(message: String) {
        Utils.showSnackbar(binding.root, message, R.color.white, Snackbar.LENGTH_SHORT)
    }

    companion object {
        const val TAG = "SignIn"
    }
}