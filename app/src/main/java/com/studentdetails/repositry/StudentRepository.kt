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
    private val studentRef = database.getReference("students")

    fun addStudent(studentData: StudentData) {
        // Generate a unique key for the student
        val key = studentRef.push().key
        if (key != null) {
            // Assign the generated key as the studentId
            val updatedStudentData = studentData.copy(studentId = key)
            studentRef.child(key).setValue(updatedStudentData)
                .addOnSuccessListener {
                    Log.e("TAG", "addStudent:Success ")
                    Log.e("Key_database_key", "$key")
                }
                .addOnFailureListener { exception ->
                    Log.e("TAG", "addStudent: ${exception.message}")

                }
        }

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            Log.d("Auth", "User is signed in: ${currentUser.uid}")
        } else {
            Log.d("Auth", "No user is signed in")
        }


    }

    fun fetchItems(): LiveData<List<StudentData>> {
        val itemsLiveData = MutableLiveData<List<StudentData>>()

        studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<StudentData>()
                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(StudentData::class.java)
                    if (item != null) {
                        items.add(item)
                    }
                }
                itemsLiveData.value = items
                Log.e("TAG_fetchItems", "addStudent: $items")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG_fetchItems", "addStudent: ${error.message}")
            }
        })

        return itemsLiveData
    }

    fun updateUser(studentId: String, studentData: StudentData, onComplete: (Boolean) -> Unit) {
        studentRef.child(studentId).setValue(studentData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
                Log.e("TAG_updateUser", "updateUser: isSuccessful")
            }
            .addOnFailureListener { exception ->

                Log.e("TAG_updateUser", "updateUser: ${exception.message}")
            }
    }

    fun getDataById(id: String): LiveData<StudentData?> {
        val data = MutableLiveData<StudentData?>()

        studentRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.value = snapshot.getValue(StudentData::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                data.value = null
            }
        })

        return data
    }

    fun deleteDataById(id: String, onComplete: (Boolean) -> Unit) {
        studentRef.child(id).removeValue().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
            Log.e("TAG_deleteDataById", "deleteDataById: isSuccessful")
        }
            .addOnFailureListener { exception ->
                Log.e("TAG_deleteDataById", "deleteDataById: ${exception.message}")
            }
    }
}

