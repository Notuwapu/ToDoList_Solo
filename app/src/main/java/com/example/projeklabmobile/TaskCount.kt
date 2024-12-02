package com.example.projeklabmobile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot

class TaskCount {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getTaskCount(callback: (Int) -> Unit) {
        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            firestore.collection("tasks")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { result: QuerySnapshot ->
                    val taskCount = result.size()
                    callback(taskCount)
                }
                .addOnFailureListener { _ ->
                    callback(0)
                }
        } else {
            callback(0)
        }
    }
}
