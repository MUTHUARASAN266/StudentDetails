package com.studentdetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studentdetails.OnItemClickListener
import com.studentdetails.Utils.loadImage
import com.studentdetails.databinding.StudentListBinding
import com.studentdetails.model.StudentData

class StudentAdapter(
    private var dataList: List<StudentData>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: StudentListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StudentData) {

            binding.studentImage.loadImage(data.studentImage)
            binding.studentNameText.text = data.studentName
            binding.studentSchoolText.text = data.studentSchoolName
            binding.btnStudentViewData.setOnClickListener {
                listener.onItemClick(data)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StudentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
    fun updateData(newDataList: List<StudentData>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}
