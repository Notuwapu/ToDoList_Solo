    package com.example.projeklabmobile

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import android.widget.LinearLayout
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import java.text.SimpleDateFormat
    import java.util.*

    class MainActivity : AppCompatActivity() {

        private lateinit var taskCountTextView: TextView
        private lateinit var taskCount: TaskCount
        private lateinit var todoButton: Button
        private lateinit var doneButton: Button
        private lateinit var logoutButton: Button
        private lateinit var greetingMessage: TextView
        private lateinit var taskListLayout: LinearLayout

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            taskCountTextView = findViewById(R.id.task_count)
            greetingMessage = findViewById(R.id.greeting_message)
            taskListLayout = findViewById(R.id.task_list)

            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val email = it.email ?: getString(R.string.email_not_available) // Ganti dengan default string jika tidak ada email
                greetingMessage.text = getString(R.string.greeting_message, email)
            }


            taskCount = TaskCount()
            taskCount.getTaskCount { count ->
                val taskCountMessage = getString(R.string.task_count_message, count)
                taskCountTextView.text = taskCountMessage
            }

            fetchTodaysTasks()

            todoButton = findViewById(R.id.btn_todo)
            doneButton = findViewById(R.id.btn_done)
            logoutButton = findViewById(R.id.btn_logout)

            todoButton.setOnClickListener {
                startActivity(Intent(this, ToDoActivity::class.java))
            }
            doneButton.setOnClickListener {
                startActivity(Intent(this, DoneActivity::class.java))
            }
            logoutButton.setOnClickListener {
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(this)
                    finish()
                }
            }
        }

        private fun fetchTodaysTasks() {
            val today = SimpleDateFormat("d-M-yyyy", Locale.getDefault()).format(Date())
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val db = FirebaseFirestore.getInstance()
            db.collection("tasks")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", today)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tasks = querySnapshot.documents.mapNotNull { it.toObject(TaskModel::class.java) }
                    if (tasks.isNotEmpty()) {
                        displayTasks(tasks)
                    } else {
                        displayNoTasksMessage()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting tasks: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }

        private fun displayTasks(tasks: List<TaskModel>) {
            taskListLayout.removeAllViews()
            tasks.forEach { task ->
                val taskView = layoutInflater.inflate(R.layout.task_item, taskListLayout, false)
                taskView.findViewById<TextView>(R.id.taskTitle).text = task.title
                taskView.findViewById<TextView>(R.id.taskDescription).text = task.description
                taskView.findViewById<TextView>(R.id.taskTime).text = task.date
                taskListLayout.addView(taskView)
            }
        }

        private fun displayNoTasksMessage() {
            taskListLayout.removeAllViews()
            val noTaskTextView = TextView(this)
            noTaskTextView.text = getString(R.string.no_tasks_message)
            taskListLayout.addView(noTaskTextView)
        }
    }