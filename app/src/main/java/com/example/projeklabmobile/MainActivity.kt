package com.example.projeklabmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var taskCountTextView: TextView
    private lateinit var taskCount: TaskCount // Menggunakan TaskCount untuk menghitung jumlah tugas
    private lateinit var todoButton: Button
    private lateinit var doneButton: Button
    private lateinit var logoutButton: Button // Tambahkan tombol logout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ganti dengan layout yang sesuai

        // Inisialisasi TextView
        taskCountTextView = findViewById(R.id.task_count)

        // Inisialisasi objek TaskCount
        taskCount = TaskCount()

        // Ambil jumlah tugas menggunakan TaskCount
        taskCount.getTaskCount { count ->
            // Menampilkan jumlah tugas pada TextView menggunakan string resource dengan placeholder
            val taskCountMessage = getString(R.string.task_count_massage, count)
            taskCountTextView.text = taskCountMessage
        }

        // Inisialisasi tombol-tombol
        todoButton = findViewById(R.id.btn_todo)
        doneButton = findViewById(R.id.btn_done)
        logoutButton = findViewById(R.id.btn_logout) // Inisialisasi tombol logout

        // Menangani klik tombol "To-Do"
        todoButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            startActivity(intent)
        }

        // Menangani klik tombol "Done"
        doneButton.setOnClickListener {
            val intent = Intent(this, DoneActivity::class.java)
            startActivity(intent)
        }

        // Menangani klik tombol "Logout"
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Mengakhiri aktivitas saat ini
        }
    }
}
