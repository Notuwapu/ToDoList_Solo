package com.example.projeklabmobile

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var datePickerButton: Button

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        titleEditText = findViewById(R.id.editTextTaskTitle)
        descriptionEditText = findViewById(R.id.editTextTaskNote)
        submitButton = findViewById(R.id.submitButton)
        datePickerButton = findViewById(R.id.datePickerButton)

        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        submitButton.setOnClickListener {
            addTask()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                selectedDate = date
                datePickerButton.text = date
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun addTask() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user != null) {
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val task = TaskModel(
                title = title,
                description = description,
                time = "$selectedDate $time",
                userId = user.uid
            )

            firestore.collection("tasks")
                .add(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error adding task: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}