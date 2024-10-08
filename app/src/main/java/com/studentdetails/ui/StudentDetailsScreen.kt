package com.studentdetails.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.studentdetails.R
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory
import com.studentdetails.Utils.showSnackbar
import com.studentdetails.databinding.FragmentStudentDetailsScreenBinding

class StudentDetailsScreen : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentStudentDetailsScreenBinding
    private var studentId: String? = null
    private lateinit var mMap: GoogleMap
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var studentImage: String? = null
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
        binding = FragmentStudentDetailsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStudentData()
        Log.e(TAG, "longitude & latitude : $longitude $latitude")
        Log.e(TAG, "studentId: $studentId")

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnUpdate.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("update_studentId", studentId)
                latitude?.let { latitude -> bundle.putDouble("update_latitude", latitude) }
                longitude?.let { longitude -> bundle.putDouble("update_longitude", longitude) }
                bundle.putString("update_studentImage", studentImage)
                Log.e(TAG, "onViewCreated: $studentId")
                Log.e(TAG, "latitude & longitude: $latitude, $longitude")
                findNavController().navigate(
                    R.id.action_studentDetails_to_editStudentDataScreen,
                    bundle
                )
            }

            binding.btnDelete.setOnClickListener {
                deleteStudentData()
            }
        }


    }

    private fun setUpStudentMap() {
        // Initialize the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.student_map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun deleteStudentData() {
        studentId?.let { studentId ->
            studentViewModel.deleteDataById(studentId) { isSuccessful ->
                if (isSuccessful) {
                    findNavController().navigate(R.id.action_studentDetails_to_viewStudentScreen)
                    showSnackbar(
                        binding.root,
                        "Data Deleted Successfully",
                        R.color.white,
                        Snackbar.LENGTH_SHORT
                    )
                } else {
                    showSnackbar(
                        binding.root,
                        "Failed to delete data",
                        R.color.white,
                        Snackbar.LENGTH_SHORT
                    )
                }
            }
        }

    }

    private fun getStudentData() {
        binding.apply {
            studentId?.let {
                studentViewModel.getDataById(it).observe(viewLifecycleOwner) { studentData ->
                    Glide.with(binding.root.context)
                        .load(studentData?.studentImage)
                        .placeholder(R.drawable.student)
                        .into(binding.sdStudentImage)
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
                    latitude = studentData?.latitude
                    longitude = studentData?.longitude
                    studentImage = studentData?.studentImage
                    Log.e(TAG, "getStudentData: $it data of $studentData")
                    setUpStudentMap()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if latitude and longitude are not null
        if (latitude != null && longitude != null) {
            val location = LatLng(latitude!!, longitude!!)
            mMap.addMarker(MarkerOptions().position(location).title("Student Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        } else {
            // Handle the case where latitude or longitude is null
            Log.e(TAG, "Latitude or Longitude is null")
        }
    }
    companion object{
        const val TAG = "StudentDetailsScreen"
    }
}