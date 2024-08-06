package com.studentdetails.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.studentdetails.model.StudentData

class StudentRepository {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun addStudent(studentData: StudentData) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = database.getReference("users").child(currentUser.uid).child("students")
            val key = userRef.push().key
            if (key != null) {
                val updatedStudentData = studentData.copy(studentId = key)
                userRef.child(key).setValue(updatedStudentData)
                    .addOnSuccessListener {
                        Log.e(TAG, "addStudent: Success")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "addStudent: ${exception.message}")
                    }
            }
        } else {
            Log.d("$TAG Auth", "No user is signed in")
        }
    }

    fun fetchItems(): LiveData<List<StudentData>> {
        val itemsLiveData = MutableLiveData<List<StudentData>>()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userRef = database.getReference("users").child(currentUser.uid).child("students")
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<StudentData>()
                    for (dataSnapshot in snapshot.children) {
                        val item = dataSnapshot.getValue(StudentData::class.java)
                        if (item != null) {
                            items.add(item)
                        }
                    }
                    itemsLiveData.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "addStudent: ${error.message}")
                }
            })
        }

        return itemsLiveData
    }

    fun updateUser(studentId: String, studentData: StudentData, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = database.getReference("users").child(currentUser.uid).child("students")
            userRef.child(studentId).setValue(studentData)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "updateUser: ${exception.message}")
                }
        }
    }

    fun getDataById(id: String): LiveData<StudentData?> {
        val data = MutableLiveData<StudentData?>()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userRef = database.getReference("users").child(currentUser.uid).child("students")
            userRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    data.value = snapshot.getValue(StudentData::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    data.value = null
                }
            })
        }

        return data
    }

    fun deleteDataById(id: String, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = database.getReference("users").child(currentUser.uid).child("students")
            userRef.child(id).removeValue().addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "deleteDataById: ${exception.message}")
                }
        }
    }

    companion object {
        const val TAG = "StudentRepository"
    }
}


