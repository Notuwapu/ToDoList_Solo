package com.example.projeklabmobile

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DoneActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var datePickerButton: Button
    private lateinit var scrollViewDone: ScrollView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        backButton = findViewById(R.id.backButton)
        datePickerButton = findViewById(R.id.datePickerButton)
        scrollViewDone = findViewById(R.id.scrollviewdone)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        datePickerButton.setOnClickListener {
            showDatePickerDialog()
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
                val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                datePickerButton.text = selectedDate
                loadCompletedTasks(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun loadCompletedTasks(selectedDate: String) {
        val user = auth.currentUser
        if (user != null) {
            val currentTime = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

            firestore.collection("tasks")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { result ->
                    scrollViewDone.removeAllViews()

                    var taskFound = false
                    for (document in result) {
                        val task = document.toObject(TaskModel::class.java)
                        val taskDate = task.date

                        if (isTaskDone(taskDate, currentTime, selectedDate)) {
                            val taskView = createTaskView(task)
                            scrollViewDone.addView(taskView)
                            taskFound = true
                        }
                    }

                    if (!taskFound) {
                        Toast.makeText(this, "No completed tasks found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error loading tasks: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isTaskDone(taskDate: String, currentTime: String, selectedDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val taskDateParsed = dateFormat.parse(taskDate)
        val currentDateParsed = dateFormat.parse(currentTime)
        val selectedDateParsed = dateFormat.parse(selectedDate)

        return taskDateParsed != null && taskDateParsed <= currentDateParsed &&
                taskDateParsed == selectedDateParsed
    }

    private fun createTaskView(task: TaskModel): LinearLayout {
        val taskLayout = LinearLayout(this)
        taskLayout.orientation = LinearLayout.VERTICAL
        taskLayout.setPadding(16, 16, 16, 12)
        taskLayout.setBackgroundResource(R.drawable.task_background)

        val titleTextView = TextView(this)
        titleTextView.text = task.title
        titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
        titleTextView.textSize = 16f
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD)

        val descriptionTextView = TextView(this)
        descriptionTextView.text = task.description
        descriptionTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
        descriptionTextView.textSize = 14f

        val timeTextView = TextView(this)
        timeTextView.text = task.date
        timeTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
        timeTextView.textSize = 12f

        taskLayout.addView(titleTextView)
        taskLayout.addView(descriptionTextView)
        taskLayout.addView(timeTextView)

        return taskLayout
    }
}