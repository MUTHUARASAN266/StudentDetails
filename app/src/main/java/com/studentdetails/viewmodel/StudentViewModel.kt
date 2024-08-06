package com.studentdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.studentdetails.model.StudentData
import com.studentdetails.model.User
import com.studentdetails.repositry.StudentRepository

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val signUpStatus = MutableLiveData<Boolean>()
    val loginStatus = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val items: LiveData<List<StudentData>> = repository.fetchItems()

    fun signUp(user: User) {
        if (user.password != user.confirmPassword) {
            errorMessage.value = "Passwords do not match"
            return
        }

        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signUpStatus.value = true
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginStatus.value = true
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    fun addStudent(studentData: StudentData) {
        repository.addStudent(studentData)
    }

    fun updateUser(id: String, studentData: StudentData, onComplete: (Boolean) -> Unit) {
        repository.updateUser(id, studentData, onComplete)
    }

    fun getDataById(id: String): LiveData<StudentData?> {
        return repository.getDataById(id)
    }

    fun deleteDataById(id: String, onComplete: (Boolean) -> Unit) {
        repository.deleteDataById(id, onComplete)
    }
}