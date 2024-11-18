package vn.edu.hust.studentman

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

  private val students = mutableListOf(
    StudentModel("Nguyễn Văn An", "SV001"),
    StudentModel("Trần Thị Bảo", "SV002"),
    StudentModel("Lê Hoàng Cường", "SV003")
  )
  private lateinit var studentAdapter: StudentAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_students)
    studentAdapter = StudentAdapter(
      students,
      onEditClick = { student, position -> showEditStudentDialog(student, position) },
      onRemoveClick = { student, position -> showDeleteConfirmationDialog(student, position) }
    )
    recyclerView.adapter = studentAdapter
    recyclerView.layoutManager = LinearLayoutManager(this)

    findViewById<Button>(R.id.btn_add_new).setOnClickListener {
      showAddStudentDialog()
    }
  }

  private fun showAddStudentDialog() {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_student, null)
    AlertDialog.Builder(this)
      .setTitle("Add New Student")
      .setView(dialogView)
      .setPositiveButton("Add") { _, _ ->
        val name = dialogView.findViewById<EditText>(R.id.edit_student_name).text.toString()
        val id = dialogView.findViewById<EditText>(R.id.edit_student_id).text.toString()
        if (name.isNotEmpty() && id.isNotEmpty()) {
          students.add(StudentModel(name, id))
          studentAdapter.notifyItemInserted(students.size - 1)
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  private fun showEditStudentDialog(student: StudentModel, position: Int) {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_student, null)
    val nameField = dialogView.findViewById<EditText>(R.id.edit_student_name)
    val idField = dialogView.findViewById<EditText>(R.id.edit_student_id)
    nameField.setText(student.studentName)
    idField.setText(student.studentId)

    AlertDialog.Builder(this)
      .setTitle("Edit Student")
      .setView(dialogView)
      .setPositiveButton("Save") { _, _ ->
        val newName = nameField.text.toString()
        val newId = idField.text.toString()
        if (newName.isNotEmpty() && newId.isNotEmpty()) {
          students[position] = StudentModel(newName, newId)
          studentAdapter.notifyItemChanged(position)
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  private fun showDeleteConfirmationDialog(student: StudentModel, position: Int) {
    AlertDialog.Builder(this)
      .setTitle("Delete Student")
      .setMessage("Are you sure you want to delete ${student.studentName}?")
      .setPositiveButton("Delete") { _, _ ->
        val deletedStudent = students.removeAt(position)
        studentAdapter.notifyItemRemoved(position)
        showUndoSnackbar(deletedStudent, position)
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  private fun showUndoSnackbar(deletedStudent: StudentModel, position: Int) {
    Snackbar.make(findViewById(android.R.id.content), "${deletedStudent.studentName} deleted", Snackbar.LENGTH_LONG)
      .setAction("Undo") {
        students.add(position, deletedStudent)
        studentAdapter.notifyItemInserted(position)
      }
      .show()
  }
}
