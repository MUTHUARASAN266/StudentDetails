package com.studentdetails.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.studentdetails.R
import com.studentdetails.Utils
import com.studentdetails.Utils.loadImage
import com.studentdetails.databinding.FragmentEditStudentDataScreenBinding
import com.studentdetails.model.StudentData
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory
import java.util.Calendar
import java.util.UUID

class EditStudentDataScreen : Fragment() {
    lateinit var binding: FragmentEditStudentDataScreenBinding
    private var studentId: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var studentImage: String? = null
    private var studentGender: String? = null
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentId = it.getString("update_studentId")
            studentImage = it.getString("update_studentImage")
            latitude = it.getDouble("update_latitude")
            longitude = it.getDouble("update_longitude")
            Log.e(TAG, "studentId: $it")

        }
    }

    private val studentViewModel: StudentViewModel by viewModels {
        StudentViewModelFactory(StudentRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditStudentDataScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.setNavigationOnClickListener {
                //   findNavController().navigateUp()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.viewStudentScreen, inclusive = true)
                    .build()

                findNavController().navigate(
                    R.id.action_editStudentDataScreen_to_viewStudentScreen,
                    null,
                    navOptions
                )

            }

            setupSpinner()
            dobDatePicker()
            setData()
            Log.e(TAG, "studentId: $studentId")
            btnUpdate.setOnClickListener {
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
        binding.textInputLayoutEdDob.setEndIconOnClickListener {
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
                    binding.editDob.setText(selectedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

    }

    private fun setData() {
        binding.apply {
            studentId?.let {
                studentViewModel.getDataById(it).observe(viewLifecycleOwner) { studentData ->
                    editName.setText(studentData?.studentName)
                    editSchoolName.setText(studentData?.studentSchoolName)
                    editDob.setText(studentData?.studentDob)
                    editBlood.setText(studentData?.studentBloodGroup)
                    editFatherName.setText(studentData?.studentFatherName)
                    editMotherName.setText(studentData?.studentMotherName)
                    editParentContactNo.setText(studentData?.studentParentContactNumber)
                    editAddress.setText(studentData?.studentAddress)
                    editCity.setText(studentData?.studentCity)
                    editState.setText(studentData?.studentState)
                    editZip.setText(studentData?.studentZip)
                    editEmergencyNumber.setText(studentData?.studentEmergencyContactNumber)
                    binding.editStudentProfileImage.loadImage(studentData?.studentImage)
                }
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
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    binding.editStudentProfileImage.setImageURI(uri)
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
        binding.editSpinnerStudentClass.adapter = classAdapter

        val sectionAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sections)
        sectionAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.editSpinnerStudentSection.adapter = sectionAdapter

        binding.editSpinnerStudentClass.setSelection(0) // Set default selection to first item
        binding.editSpinnerStudentSection.setSelection(0) // Set default selection to first item
    }

    private fun spinnerData(): String {
        var spinnerClass = binding.editSpinnerStudentClass.selectedItem.toString()
        var spinnerSection = binding.editSpinnerStudentSection.selectedItem.toString()


        binding.editSpinnerStudentClass.onItemSelectedListener =
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

        binding.editSpinnerStudentSection.onItemSelectedListener =
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

            val studentName = editName.text.toString()
            val studentSchoolName = editSchoolName.text.toString()
            val studentDob = editDob.text.toString()
            val studentBloodGroup = editBlood.text.toString()
            val studentFatherName = editFatherName.text.toString()
            val studentMotherName = editMotherName.text.toString()
            val studentParentContactNumber = editParentContactNo.text.toString()
            val studentAddress = editAddress.text.toString()
            val studentCity = editCity.text.toString()
            val studentState = editState.text.toString()
            val studentZip = editZip.text.toString()
            val studentEmergencyContactNumber = editEmergencyNumber.text.toString()


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
                    binding.progressCircular.visibility = View.VISIBLE
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
                binding.progressCircular.visibility = View.GONE
                showMessage("Failed to upload image")
            }
        } ?: run {
            binding.progressCircular.visibility = View.GONE
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
            studentId = studentId,
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
        studentId?.let {
            studentViewModel.updateUser(it, studentData) { isSuccessful ->
                if (isSuccessful) {
                    showMessage("User updated successfully")
                    setData()
                    Log.e(TAG, "studentId: $it")
                    Log.e(
                        TAG,
                        "studentId: $it,studentData: $studentData"
                    )
                    binding.progressCircular.visibility = View.GONE
                    findNavController().navigate(R.id.action_editStudentDataScreen_to_viewStudentScreen)
                } else {
                    binding.progressCircular.visibility = View.GONE
                    showMessage("Failed to update user")
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Utils.showSnackbar(binding.root, message, R.color.white, Snackbar.LENGTH_SHORT)
    }

    companion object {
        const val TAG = "EditStudentDataScreen"
    }
}