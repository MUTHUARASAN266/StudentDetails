package com.studentdetails.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.studentdetails.R
import com.studentdetails.databinding.FragmentStudentDashboardBinding

class StudentDashboard : Fragment() {
    private lateinit var binding: FragmentStudentDashboardBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentStudentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            btnAdd.setOnClickListener {
                findNavController().navigate(R.id.action_studentDashboard_to_addStudentScreen)
            }
            btnViewStudent.setOnClickListener {
                findNavController().navigate(R.id.action_studentDashboard_to_viewStudentScreen)
            }

            btnMap.setOnClickListener {
                findNavController().navigate(R.id.action_studentDashboard_to_studentMapScreen)
            }
            btnProfileImage.setOnClickListener {
                showLogoutDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Logout")

        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            performLogout()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }

    private fun performLogout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        startActivity(Intent(requireContext(), OnboardingScreen::class.java))
        activity?.finish()
    }

}