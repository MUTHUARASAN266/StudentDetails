package com.studentdetails.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.studentdetails.R
import com.studentdetails.Utils.getCircularBitmap
import com.studentdetails.databinding.FragmentStudentMapScreenBinding
import com.studentdetails.model.StudentData
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
        binding = FragmentStudentMapScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Initialize the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadStudentLocations()
    }

    private fun loadStudentLocations() {
        studentViewModel.items.observe(viewLifecycleOwner) { students ->

            try {
                for (student in students) {
                    val location = LatLng(student.latitude!!, student.longitude!!)
                    addingImageToMap(student, mMap, location)
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
            } catch (e: Exception) {
                Log.e(TAG, "loadStudentLocations: ${e.message}")
            }
        }
    }

    private fun defaultLocation() {
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
    }

    private fun addingImageToMap(student: StudentData, mMap: GoogleMap, location: LatLng) {
        // Load image and add marker
        Glide.with(this)
            .asBitmap()
            .load(student.studentImage)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val size = 150 // Adjust the size as needed (e.g., 100px)
                    val circularBitmap = getCircularBitmap(resource, size)
                    val icon = BitmapDescriptorFactory.fromBitmap(circularBitmap)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(student.studentName)
                            .icon(icon)
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e(TAG, "onLoadCleared")
                }
            })

    }

    companion object {
        const val TAG = "StudentMapScreen"
    }
}