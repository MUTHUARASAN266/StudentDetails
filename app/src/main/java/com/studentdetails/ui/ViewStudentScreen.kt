package com.studentdetails.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.studentdetails.OnItemClickListener
import com.studentdetails.R
import com.studentdetails.adapter.StudentAdapter
import com.studentdetails.model.StudentData
import com.studentdetails.repositry.StudentRepository
import com.studentdetails.viewmodel.StudentViewModel
import com.studentdetails.viewmodel.StudentViewModelFactory
import com.studentdetails.databinding.FragmentViewStudentScreenBinding

class ViewStudentScreen : Fragment(), OnItemClickListener {
    lateinit var binding: FragmentViewStudentScreenBinding
    private lateinit var studentAdapter: StudentAdapter
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
        binding = FragmentViewStudentScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressCircular.visibility = View.VISIBLE
        studentAdapter = StudentAdapter(emptyList(), this)
        binding.recyclerview.adapter = studentAdapter

        binding.toolbar.setNavigationOnClickListener {
            //   findNavController().navigateUp()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.studentDashboard, inclusive = true)
                .build()

            findNavController().navigate(R.id.action_viewStudentScreen_to_studentDashboard, null, navOptions)

        }
        studentViewModel.items.observe(viewLifecycleOwner) { items ->
            studentAdapter.updateData(items)
            binding.progressCircular.visibility = View.GONE
        }
    }

    override fun onItemClick(data: StudentData) {
        val bundle = Bundle()
        bundle.putString("studentId", data.studentId.toString())
        Log.e(TAG, "onItemClick: studentId ${data.studentId}")
        findNavController().navigate(R.id.action_viewStudentScreen_to_studentDetails,bundle)
    }
    companion object{
        const val TAG = "ViewStudentScreen"
    }
}