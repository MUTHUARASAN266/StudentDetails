package com.studentdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studentdetails.model.StudentData
import com.studentdetails.model.User
import com.studentdetails.repositry.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val signUpStatus = MutableLiveData<Boolean>()
    val loginStatus = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    private val loading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = loading
    val items: LiveData<List<StudentData>> = repository.fetchItems()

    fun signUp(user: User) {
        if (user.password != user.confirmPassword) {
            errorMessage.value = "Passwords do not match"
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(user.email, user.password).await()
                signUpStatus.postValue(true)
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            }
        }
    }

    fun login(email: String, password: String) {
        // Clear any previous error message
        errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            loginStatus.value = true
                        } else {
                            errorMessage.value = task.exception?.message
                        }
                    }
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            }
        }
    }

    fun addStudent(studentData: StudentData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudent(studentData)
        }
    }

    fun updateUser(id: String, studentData: StudentData, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(id, studentData, onComplete)
        }
    }

    fun getDataById(id: String): LiveData<StudentData?> {
        return repository.getDataById(id)
    }

    fun deleteDataById(id: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDataById(id, onComplete)
        }
    }
}