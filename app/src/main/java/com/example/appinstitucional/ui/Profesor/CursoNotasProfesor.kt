package com.example.appinstitucional.ui.Profesor

import DatabaseHelper
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstitucional.R
import com.example.appinstitucional.model.Class.Curso
import com.example.appinstitucional.ui.Administrador.AdministradorActivity
import com.example.appinstitucional.ui.Administrador.AlumnoAdmin
import com.example.appinstitucional.ui.Administrador.ProfesorAdmin
import com.google.android.material.bottomnavigation.BottomNavigationView

class CursoNotasProfesor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesor_curso_notas)

                val cursoId = intent.getIntExtra("cursoId", -1)
                if (cursoId != -1) {
                    val databaseHelper = DatabaseHelper(this)

                    val curso = databaseHelper.getCursoById(cursoId)
                    if (curso != null) {
                        val textViewCursoName = findViewById<TextView>(R.id.textViewCursoName)
                        textViewCursoName.text = curso.nombre

                        // Fetch the students of the course
                        val cursor = databaseHelper.getAlumnosPorCurso(cursoId)

                        // Make sure you have a TableLayout in your layout to display the students
                        val tableView = findViewById<TableLayout>(R.id.tableLayout)

                        // Iterate over the data in the Cursor and fill the table
                        while (cursor.moveToNext()) {
                            // Create a new row
                            val tableRow = TableRow(this)

                            // Fetch idAlumno from the cursor
                            // Inside the while loop where you are creating the table rows
                            val idAlumno = cursor.getInt(cursor.getColumnIndex("id"))
                            val notas = databaseHelper.getNotasPorCursoYAlumno(cursoId, idAlumno)

// Create a new TextView for the student's name
                            val nombreAlumno = TextView(this)
                            nombreAlumno.text = cursor.getString(cursor.getColumnIndex("nombre"))
                            tableRow.addView(nombreAlumno)

// For each grade, create a new TextView and add it to the row
                            for (nota in notas) {
                                val notaAlumno = TextView(this)
                                val params = TableRow.LayoutParams(
                                    TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                                notaAlumno.layoutParams = params

                                if (nota != null) {
                                    // If the grade exists, display it
                                    notaAlumno.text = nota.toString()
                                } else {
                                    // If the grade does not exist, display a word
                                    notaAlumno.text = "No grade"
                                }

                                tableRow.addView(notaAlumno)
                            }

// Create a new TextView for the add icon
                            val addIcon = TextView(this)
                            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_add)
                            addIcon.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                null,
                                null
                            )
                            addIcon.setOnClickListener {
                                // Open the dialog here
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Enter grades")

                                val inflater = layoutInflater
                                val dialogLayout = inflater.inflate(R.layout.dialog_new_nota, null)

                                val editTextN1 = dialogLayout.findViewById<EditText>(R.id.etN1)
                                val editTextN2 = dialogLayout.findViewById<EditText>(R.id.etN2)
                                val editTextN3 = dialogLayout.findViewById<EditText>(R.id.etN3)
                                val editTextN4 = dialogLayout.findViewById<EditText>(R.id.etN4)

                                builder.setView(dialogLayout)
                                builder.setPositiveButton("Save") { dialog, which ->
                                    val n1 = editTextN1.text.toString().toIntOrNull()
                                    val n2 = editTextN2.text.toString().toIntOrNull()
                                    val n3 = editTextN3.text.toString().toIntOrNull()
                                    val n4 = editTextN4.text.toString().toIntOrNull()

                                    val databaseHelper = DatabaseHelper(this)
                                    databaseHelper.saveNotas(
                                        cursoId,
                                        idAlumno,
                                        listOf(n1, n2, n3, n4)
                                    )
                                }
                                builder.setNegativeButton("Cancel", null)

                                val dialog = builder.create()
                                dialog.show()
                            }
                            tableRow.addView(addIcon)

// Add the row to the table
                            tableView.addView(tableRow)
                        }
                            // Don't forget to close the Cursor when you're done
                            cursor.close()
                    }
                }
        NavegacionNotaCurso()
        updateNavigationSelection()
        OnNavigationItemSelectedListener()
    }

    private fun NavegacionNotaCurso() {
        val btnNotas: Button = findViewById(R.id.btnNotas)
        btnNotas.setOnClickListener {
            val intent = Intent(this, CursoNotasProfesor::class.java)
            startActivity(intent)
        }

        val btnAsistencia: Button = findViewById(R.id.btnAsistencia)
        btnAsistencia.setOnClickListener {
            val intent = Intent(this, CursoAsistenciaProfesor::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation_menu_profesor, menu)
        return true
    }

    private fun OnNavigationItemSelectedListener() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.iconCursos -> {
                    val intent = Intent(this, ProfesorActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.iconProfesor -> {
                    val intent = Intent(this, CursoAsistenciaProfesor::class.java)
                    startActivity(intent)
                    true
                }
                R.id.iconAlumno -> {
                    val intent = Intent(this, SearchProfesorActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun updateNavigationSelection() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        when (this.javaClass) {
            AdministradorActivity::class.java -> {
                bottomNavigation.selectedItemId = R.id.iconCursos
            }
            ProfesorAdmin::class.java -> {
                bottomNavigation.selectedItemId = R.id.iconProfesor
            }
            AlumnoAdmin::class.java -> {
                bottomNavigation.selectedItemId = R.id.iconAlumno
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_repeat -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}