package com.studentdetails.ui

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.studentdetails.R
import com.studentdetails.Utils
import com.studentdetails.databinding.FragmentAddStudentScreenBinding
import com.studentdetails.model.StudentData
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddStudentScreen : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentAddStudentScreenBinding
    private var studentGender: String? = null
    private var studentImage: String? = null
    private var imageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var latitude: Double? = null
    private var longitude: Double? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    //    private val studentViewModel: StudentViewModel by viewModels()
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
        binding = FragmentAddStudentScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        checkLocationPermission()
        setUpStudentMap()
        dobDatePicker()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.apply {
            setupSpinner()
            btnSubmite.setOnClickListener {
                validation()

            }
            btnImage.setOnClickListener {

                checkPermissionAndPickImage()
            }
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1) { // Ensure a valid RadioButton is checked
                    val radioButton: RadioButton = group.findViewById(checkedId)
                    studentGender = radioButton.text.toString()
                }
            }


        }
    }

    private fun dobDatePicker() {
        binding.textInputLayoutDob.setEndIconOnClickListener {
            // Open the date picker dialog
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the selected date and set it to the EditText
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.edDob.setText(selectedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

    }

    private fun clearText() {
        binding.apply {
            edName.text?.clear()
            edSchoolName.text?.clear()
            edDob.text?.clear()
            edBlood.text?.clear()
            edFatherName.text?.clear()
            edMotherName.text?.clear()
            edParentContactNo.text?.clear()
            edAddress.text?.clear()
            edCity.text?.clear()
            edState.text?.clear()
            edZip.text?.clear()
            edEmergencyNumber.text?.clear()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()

        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    latitude = location.latitude
                    longitude = location.longitude
                    mMap.addMarker(MarkerOptions().position(latLng).title("You are here"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    binding.btnSetlocation.setOnClickListener {
                        getAddressFromLocation(location.latitude, location.longitude)
                        Log.e(
                            TAG,
                            "getAddressFromLocation : ${location.latitude} ${location.longitude}"
                        )
                    }
                }
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val addressText = StringBuilder()
            for (i in 0..address.maxAddressLineIndex) {
                addressText.append(address.getAddressLine(i)).append("\n")
            }
            showMessage("Address: $addressText")
            binding.txtStudentLocation.text = addressText
        }
    }

    private fun setUpStudentMap() {
        // Initialize the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.student_map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                showMessage("Location permission denied")
            }
        }
    }

    private fun checkPermissionAndPickImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13 (API level 33) and above
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }

                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
                        showMessage("Media access is required to pick an image.")
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }

                    else -> {
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10 (API level 29) to 12 (API level 31)
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }

                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        showMessage("Storage access is required to pick an image.")
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    else -> {
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

            else -> {
                // For Android 9 (API level 28) and below, no special permissions needed
                openImagePicker()
            }
        }
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openImagePicker()
        } else {
            showMessage("Permission denied. Cannot pick images.")
        }
    }
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    binding.studentProfileImage.setImageURI(uri)
                    imageUri = uri
                }
            }
        }

    private fun setupSpinner() {
        // List of classes
        val classes = listOf(
            "1st Standard",
            "2nd Standard",
            "3rd Standard",
            "4th Standard",
            "5th Standard",
            "6th Standard",
            "7th Standard",
            "8th Standard",
            "9th Standard",
            "10th Standard",
            "11th Standard",
            "12th Standard"
        )

        // List of sections
        val sections = listOf("A Section", "B Section", "C Section", "D Section", "E Section")

        val classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, classes)
        classAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.spinnerStudentClass.adapter = classAdapter

        val sectionAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sections)
        sectionAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.spinnerStudentSection.adapter = sectionAdapter

        binding.spinnerStudentClass.setSelection(0) // Set default selection to first item
        binding.spinnerStudentSection.setSelection(0) // Set default selection to first item
    }

    private fun spinnerData(): String {
        var spinnerClass = binding.spinnerStudentClass.selectedItem.toString()
        var spinnerSection = binding.spinnerStudentSection.selectedItem.toString()


        binding.spinnerStudentClass.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: android.view.View,
                    position: Int,
                    id: Long
                ) {
                    spinnerClass = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    showMessage("NothingSelected")
                }
            }

        binding.spinnerStudentSection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: android.view.View,
                    position: Int,
                    id: Long
                ) {
                    spinnerSection = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    showMessage("NothingSelected")
                }
            }

        return "$spinnerClass $spinnerSection"
    }

    private fun validation() {
        binding.apply {

            val studentName = edName.text.toString()
            val studentSchoolName = edSchoolName.text.toString()
            val studentDob = edDob.text.toString()
            val studentBloodGroup = edBlood.text.toString()
            val studentFatherName = edFatherName.text.toString()
            val studentMotherName = edMotherName.text.toString()
            val studentParentContactNumber = edParentContactNo.text.toString()
            val studentAddress = edAddress.text.toString()
            val studentCity = edCity.text.toString()
            val studentState = edState.text.toString()
            val studentZip = edZip.text.toString()
            val studentEmergencyContactNumber = edEmergencyNumber.text.toString()


            Log.e(
                TAG,
                "student data: $studentName $studentSchoolName $studentDob $studentBloodGroup" +
                        "$studentMotherName $studentParentContactNumber $studentAddress $studentCity"
                        + "$studentState $studentZip $studentEmergencyContactNumber $studentGender ${spinnerData()}"
            )

            when {
                studentName.isEmpty() -> showMessage("studentName is empty")
                spinnerData().isEmpty() -> showMessage("Please select teh class and section")
                studentSchoolName.isEmpty() -> showMessage("studentSchoolName is empty")
                studentDob.isEmpty() -> showMessage("studentDob is empty")
                studentBloodGroup.isEmpty() -> showMessage("studentBloodGroup is empty")
                studentFatherName.isEmpty() -> showMessage("studentFatherName is empty")
                studentMotherName.isEmpty() -> showMessage("studentMotherName is empty")
                studentParentContactNumber.isEmpty() -> showMessage("studentParentContactNumber is empty")
                studentAddress.isEmpty() -> showMessage("studentAddress is empty")
                studentCity.isEmpty() -> showMessage("studentCity is empty")
                studentState.isEmpty() -> showMessage("studentState is empty")
                studentZip.isEmpty() -> showMessage("studentZip is empty")
                studentEmergencyContactNumber.isEmpty() -> showMessage("studentEmergencyContactNumber is empty")


                else -> {
                    //uploadImageToDatabase()
                    progressCircular.visibility = View.VISIBLE
                    uploadImageToDatabase(
                        studentName,
                        spinnerData(),
                        studentSchoolName,
                        studentGender,
                        studentDob,
                        studentBloodGroup,
                        studentFatherName,
                        studentMotherName,
                        studentParentContactNumber,
                        studentAddress,
                        studentCity,
                        studentState,
                        studentZip,
                        studentEmergencyContactNumber,
                    )
                    Utils.showSnackbar(
                        binding.root,
                        "wait few second your data storing to database",
                        R.color.white,
                        Snackbar.LENGTH_SHORT
                    )
                    Log.e(TAG, "validation: Student data saved successfully")
                }
            }


        }
    }

    private fun uploadImageToDatabase(
        studentName: String,
        spinnerData: String,
        studentSchoolName: String,
        studentGender: String?,
        studentDob: String,
        studentBloodGroup: String,
        studentFatherName: String,
        studentMotherName: String,
        studentParentContactNumber: String,
        studentAddress: String,
        studentCity: String,
        studentState: String,
        studentZip: String,
        studentEmergencyContactNumber: String
    ) {
        imageUri?.let { uri ->
            val fileReference = FirebaseStorage.getInstance().reference
                .child("student_images/${UUID.randomUUID()}.jpg")

            fileReference.putFile(uri).addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                    studentImage = downloadUrl.toString()
                    storeDataInFirebase(
                        studentName,
                        spinnerData,
                        studentSchoolName,
                        studentGender,
                        studentDob,
                        studentBloodGroup,
                        studentFatherName,
                        studentMotherName,
                        studentParentContactNumber,
                        studentAddress,
                        studentCity,
                        studentState,
                        studentZip,
                        studentEmergencyContactNumber,
                        studentImage
                    )
                }
            }.addOnFailureListener {
                showMessage("Failed to upload image")
            }
        } ?: run {
            showMessage("Image not selected")
        }
    }

    private fun storeDataInFirebase(
        studentName: String,
        spinnerData: String,
        studentSchoolName: String,
        studentGender: String?,
        studentDob: String,
        studentBloodGroup: String,
        studentFatherName: String,
        studentMotherName: String,
        studentParentContactNumber: String,
        studentAddress: String,
        studentCity: String,
        studentState: String,
        studentZip: String,
        studentEmergencyContactNumber: String,
        studentImage: String?
    ) {
        val studentData = StudentData(
            studentName = studentName,
            studentClassAndStudentSection = spinnerData,
            studentSchoolName = studentSchoolName,
            studentGender = studentGender,
            studentDob = studentDob,
            studentBloodGroup = studentBloodGroup,
            studentFatherName = studentFatherName,
            studentMotherName = studentMotherName,
            studentParentContactNumber = studentParentContactNumber,
            studentAddress = studentAddress,
            studentCity = studentCity,
            studentState = studentState,
            studentZip = studentZip,
            studentEmergencyContactNumber = studentEmergencyContactNumber,
            studentImage = studentImage,
            latitude = latitude,
            longitude = longitude
        )
        studentViewModel.addStudent(studentData)
        Utils.showSnackbar(
            binding.root,
            "Student data saved successfully",
            R.color.white,
            Snackbar.LENGTH_SHORT
        )
        Log.e(TAG, "storeDataInFirebase: $studentData")
        clearText()
        findNavController().navigate(R.id.action_addStudentScreen_to_studentDashboard)
        binding.progressCircular.visibility = View.GONE
    }


    private fun showMessage(message: String) {
        Utils.showSnackbar(binding.root, message, R.color.white, Snackbar.LENGTH_SHORT)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLocation()
    }

    companion object {
        const val TAG = "AddStudentScreen"
    }

}