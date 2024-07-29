package com.studentdetails

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.studentdetails.databinding.FragmentAddStudentScreenBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddStudentScreen : Fragment() {
    private lateinit var binding: FragmentAddStudentScreenBinding
    private var studentGender: String? = null
    private var studentimage: String? = null

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

    private fun checkPermissionAndPickImage() {
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

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun handleImageUri(uri: Uri) {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val base64Image = encodeImageToBase64(bitmap)
//        saveImageToDatabase(base64Image)
        studentimage = base64Image
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
                    handleImageUri(uri)
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
                "TAG student data",
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
                    storeDataInFirebase(
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
                        "Student data saved successfully",
                        R.color.white,
                        Snackbar.LENGTH_SHORT
                    )
                    Log.e("TAG", "validation: Student data saved successfully")
                }
            }


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
        studentEmergencyContactNumber: String
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
            studentImage = studentimage
        )
        studentViewModel.addStudent(studentData)
    }

    private fun showMessage(message: String) {
        Utils.showSnackbar(binding.root, message, R.color.white, Snackbar.LENGTH_SHORT)
    }
}