package com.studentdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.studentdetails.model.StudentData
import com.studentdetails.repositry.StudentRepository

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    private val dataBase = Firebase.database.reference
    private val _user = MutableLiveData<List<StudentData>>()
    private val user: LiveData<List<StudentData>> get() = _user


    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    fun addStudent(studentData: StudentData) {
        repository.addStudent(studentData)
    }

    fun updateUser(id: String, studentData: StudentData, onComplete: (Boolean) -> Unit) {
        repository.updateUser(id, studentData, onComplete)
    }

    fun getDataById(id: String): LiveData<StudentData?> {
        return repository.getDataById(id)
    }

    val items: LiveData<List<StudentData>> = repository.fetchItems()
    fun deleteDataById(id: String, onComplete: (Boolean) -> Unit) {
        repository.deleteDataById(id, onComplete)
    }
}