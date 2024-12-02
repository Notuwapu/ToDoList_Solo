package com.example.projeklabmobile

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ToDoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<TaskModel>()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(tasks)
        recyclerView.adapter = taskAdapter
        loadTasks()

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<Button>(R.id.addTaskButton).setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.datePickerButton).setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                findViewById<Button>(R.id.datePickerButton).text = selectedDate
                loadTasks(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun loadTasks(selectedDate: String = "") {
        val user = auth.currentUser
        if (user != null) {
            var query = firestore.collection("tasks").whereEqualTo("userId", user.uid)

            if (selectedDate.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedSelectedDate = try {
                    val date = dateFormat.parse(selectedDate)
                    date?.let { dateFormat.format(it) }
                } catch (e: Exception) {
                    Log.e("ToDoActivity", "Error parsing date: $selectedDate", e)
                    null
                }

                if (formattedSelectedDate != null) {
                    query = query.whereEqualTo("date", formattedSelectedDate)
                } else {
                    Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            query.get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        Toast.makeText(this, "No tasks found for this date", Toast.LENGTH_SHORT).show()
                    }

                    val newTasks = mutableListOf<TaskModel>()
                    for (document in result) {
                        val task = document.toObject(TaskModel::class.java)
                        newTasks.add(task)
                    }

                    if (newTasks.isNotEmpty()) {
                        tasks.clear()
                        tasks.addAll(newTasks)
                        taskAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error loading tasks: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}