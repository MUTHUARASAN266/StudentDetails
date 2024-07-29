package com.studentdetails.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.studentdetails.R
import com.studentdetails.databinding.FragmentStudentDashboardBinding

class StudentDashboard : Fragment() {
private lateinit var binding: FragmentStudentDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentStudentDashboardBinding.inflate(inflater,container,false)

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
        }





        return binding.root
    }

 }