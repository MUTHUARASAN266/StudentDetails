package com.studentdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.studentdetails.Utils.showSnackbar
import com.studentdetails.databinding.FragmentStudentDetailsScreenBinding

class StudentDetailsScreen : Fragment() {
    lateinit var binding: FragmentStudentDetailsScreenBinding
    private var studentId: String? = null
    private val studentViewModel: StudentViewModel by viewModels {
        StudentViewModelFactory(StudentRepository())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentId = it.getString("studentId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentDetailsScreenBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStudentData()
        deleteStudentData()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun deleteStudentData() {
        binding.btnDelete.setOnClickListener {

            studentId?.let { it1 ->
                studentViewModel.deleteDataById(it1) { isSuccessful ->
                    if (isSuccessful) {
                        findNavController().navigate(R.id.action_studentDetails_to_viewStudentScreen)
                    } else {
                        showSnackbar(binding.root, "Failed to delete data", R.color.white, Snackbar.LENGTH_SHORT)
                    }
                }
            }
        }
    }

    private fun getStudentData() {
        binding.apply {
            studentId?.let {
                studentViewModel.getDataById(it).observe(viewLifecycleOwner){ studentData->
                    sdStudentName.text = studentData?.studentName
                    sdStudentGrade.text = studentData?.studentClassAndStudentSection
                    sdStudentSchool.text = studentData?.studentSchoolName
                    sdGenderTxt.text = studentData?.studentGender
                    sdDobTxt.text = studentData?.studentDob
                    sdBloodTxt.text = studentData?.studentBloodGroup
                    sdFatherNameTxt.text = studentData?.studentFatherName
                    sdMotherNameTxt.text = studentData?.studentMotherName
                    sdContactNo.text = studentData?.studentParentContactNumber
                    sdEmergencyNumber.text = studentData?.studentEmergencyContactNumber
                    sdAddressTxt.text = studentData?.studentAddress
                    sdCityTxt.text = studentData?.studentCity
                    sdStateTxt.text = studentData?.studentState
                    sdZipTxt.text = studentData?.studentZip


                }
            }
        }
    }
}