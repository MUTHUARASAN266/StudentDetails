package com.studentdetails.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.studentdetails.R
import com.studentdetails.databinding.FragmentStudentMapScreenBinding
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory


class StudentMapScreen : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentStudentMapScreenBinding
    private lateinit var mMap: GoogleMap
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
        binding = FragmentStudentMapScreenBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Initialize the map
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadStudentLocations()
    }
    private fun loadStudentLocations() {
        studentViewModel.items.observe(viewLifecycleOwner) { students ->
            for (student in students) {
                val location = LatLng(student.latitude!!, student.longitude!!)
                mMap.addMarker(MarkerOptions().position(location).title(student.studentName))
            }
            if (students.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.Builder()
                for (student in students) {
                    val location = LatLng(student.latitude!!, student.longitude!!)
                    boundsBuilder.include(location)
                }
                val bounds = boundsBuilder.build()
                val padding = 100 // padding around the map edges
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            }
        }
    }
}