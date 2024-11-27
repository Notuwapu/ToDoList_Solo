package com.example.projeklabmobile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot

class TaskCount {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Fungsi untuk mengambil jumlah tugas berdasarkan user yang sedang login
     */
    fun getTaskCount(callback: (Int) -> Unit) {
        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            // Mengambil data tugas berdasarkan userId
            firestore.collection("tasks")
                .whereEqualTo("userId", user.uid) // Pastikan ada field userId di setiap tugas
                .get()
                .addOnSuccessListener { result: QuerySnapshot ->
                    // Menghitung jumlah tugas
                    val taskCount = result.size()
                    callback(taskCount) // Mengembalikan jumlah tugas melalui callback
                }
                .addOnFailureListener { _ ->
                    // Menangani kesalahan jika gagal mendapatkan data
                    callback(0)
                }
        } else {
            callback(0) // Jika user tidak terdaftar
        }
    }
}
