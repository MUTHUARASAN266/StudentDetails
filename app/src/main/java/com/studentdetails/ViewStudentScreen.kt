package com.studentdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

        studentAdapter = StudentAdapter(emptyList(), this)
        binding.recyclerview.adapter = studentAdapter

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        studentViewModel.items.observe(viewLifecycleOwner) { items ->
            studentAdapter.updateData(items)
        }
    }

    override fun onItemClick(data: StudentData) {
        val bundle = Bundle()
        bundle.putString("studentId", data.studentId.toString())
        findNavController().navigate(R.id.action_viewStudentScreen_to_studentDetails,bundle)
    }
}