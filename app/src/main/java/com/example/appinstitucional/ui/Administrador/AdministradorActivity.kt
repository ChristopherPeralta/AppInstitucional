package com.example.appinstitucional.ui.Administrador

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Profesor.SearchProfesorActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdministradorActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrador)

        fillTable()

        dialog()


        navigationCourse()

        updateNavigationSelection()
        OnNavigationItemSelectedListener()
    }

    private fun dialog() {
        val btnAgregarCurso: ImageButton = findViewById(R.id.btnAgregar)
        btnAgregarCurso.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_course, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nuevo curso")
            val alertDialog = builder.show()

            // Configure the Spinners
            val spinnerCourseLevel: Spinner = dialogView.findViewById(R.id.spinnerCourseLevel)
            val spinnerCourseGrade: Spinner = dialogView.findViewById(R.id.spinnerCourseGrade)
            val spinnerCourseSection: Spinner = dialogView.findViewById(R.id.spinnerCourseSection)
            val spinnerCourseProfesor: Spinner = dialogView.findViewById(R.id.spinnerCourseTeacher)

            val dbHelper = DatabaseHelper(this)

            Thread {
                val cursor = dbHelper.getNiveles()
                val niveles = mutableListOf<String>()
                val nombreIndex = cursor.getColumnIndex("nombre")

                if (nombreIndex != -1) {
                    while (cursor.moveToNext()) {
                        niveles.add(cursor.getString(nombreIndex))
                    }
                } else {
                    // Handle the case where the column does not exist
                }

                // Actualizar la interfaz de usuario en el hilo principal
                runOnUiThread {
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles.toList())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCourseLevel.adapter = adapter
                }
            }.start()

            // Agrega un OnItemSelectedListener al Spinner de niveles
            spinnerCourseLevel.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val nivelSeleccionado = parent.getItemAtPosition(position).toString()

                        Thread {
                            val cursorGrado = dbHelper.getGradosPorNivel(nivelSeleccionado)
                            val grados = mutableListOf<String>()  // Cambia a una lista de Strings
                            while (cursorGrado.moveToNext()) {
                                val nombre =
                                    cursorGrado.getString(cursorGrado.getColumnIndex("nombre"))
                                grados.add(nombre)  // Agrega solo el nombre a la lista
                            }

                            // Actualizar la interfaz de usuario en el hilo principal
                            runOnUiThread {
                                val adapterGrado = ArrayAdapter(
                                    this@AdministradorActivity,
                                    android.R.layout.simple_spinner_item,
                                    grados
                                )
                                adapterGrado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerCourseGrade.adapter = adapterGrado
                            }
                        }.start()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // No se seleccionó nada
                    }
                }

            // Agrega un OnItemSelectedListener al Spinner de grados
            spinnerCourseGrade.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val gradoSeleccionado = parent.getItemAtPosition(position).toString()

                        Thread {
                            val cursorSeccion =
                                dbHelper.getSeccionesConNivelYGrado(gradoSeleccionado)
                            val secciones =
                                mutableListOf<String>()  // Cambia a una lista de Strings
                            while (cursorSeccion.moveToNext()) {
                                val nombre =
                                    cursorSeccion.getString(cursorSeccion.getColumnIndex("nombre"))
                                secciones.add(nombre)  // Agrega solo el nombre a la lista
                            }

                            // Actualizar la interfaz de usuario en el hilo principal
                            runOnUiThread {
                                val adapterSeccion = ArrayAdapter(
                                    this@AdministradorActivity,
                                    android.R.layout.simple_spinner_item,
                                    secciones
                                )
                                adapterSeccion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerCourseSection.adapter = adapterSeccion
                            }
                        }.start()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // No se seleccionó nada
                    }
                }
            // Populate the spinner with the names of the professors
            val cursorProfesor = dbHelper.getProfesores()
            val profesores = ArrayList<String>()
            while (cursorProfesor.moveToNext()) {
                profesores.add(cursorProfesor.getString(cursorProfesor.getColumnIndex("nombre")))
            }
            cursorProfesor.close()

            val adapterProfesor = ArrayAdapter(this, android.R.layout.simple_spinner_item, profesores)
            adapterProfesor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourseProfesor.adapter = adapterProfesor

            // Define the input fields
            val inputNombre: EditText = dialogView.findViewById(R.id.etCourseName)
            val inputHorarioInicio: EditText = dialogView.findViewById(R.id.etHorarioInicio)
            val inputHorarioFin: EditText = dialogView.findViewById(R.id.etHorarioFin)
            val inputDia: EditText = dialogView.findViewById(R.id.etDia)
            val inputAula: EditText = dialogView.findViewById(R.id.etCourseClassroom)


            val btnCreateCourse: Button = dialogView.findViewById(R.id.btnCreateCourse)
            btnCreateCourse.setOnClickListener {
                // Get the input values
                val nombre = inputNombre.text.toString()
                val horarioInicio = inputHorarioInicio.text.toString()
                val horarioFin = inputHorarioFin.text.toString()
                val dia = inputDia.text.toString()
                val aula = inputAula.text.toString()
                val nivelNombre = spinnerCourseLevel.selectedItem.toString()
                val gradoNombre = spinnerCourseGrade.selectedItem.toString()
                val seccionNombre = spinnerCourseSection.selectedItem.toString()
                val profesorNombre = spinnerCourseProfesor.selectedItem.toString()

                // Get the ID of the selected section
                val cursorSeccionId = dbHelper.getSeccionIdByName(seccionNombre)
                val idSeccion = if (cursorSeccionId.moveToFirst()) cursorSeccionId.getInt(cursorSeccionId.getColumnIndex("id")) else null
                cursorSeccionId.close()

                // Get the ID of the selected professor
                val cursorProfesorId = dbHelper.getProfesorIdByName(profesorNombre)
                val idProfesor = if (cursorProfesorId.moveToFirst()) cursorProfesorId.getInt(cursorProfesorId.getColumnIndex("id")) else null
                cursorProfesorId.close()

                // Create the new course and assign the professor
                if (idSeccion != null && idProfesor != null) {
                    dbHelper.createCursoAndAssignProfesor(nombre, horarioInicio, horarioFin, dia, aula, idSeccion, idProfesor)
                }
                fillTable()
                alertDialog.dismiss()
            }

            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }


    }

    private fun fillTable() {
        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getCursos()

        val tableLayout: TableLayout = findViewById(R.id.tableLayout)
        val headerIndex = tableLayout.indexOfChild(findViewById(R.id.tableHeader))

        // Remove all rows except the first one
        for (i in tableLayout.childCount - 1 downTo headerIndex + 1) {
            tableLayout.removeViewAt(i)
        }

        while (cursor.moveToNext()) {
            // Create a new row
            val tableRow = TableRow(this)

            // Get the data from the current row
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            val horarioInicio = cursor.getString(cursor.getColumnIndex("horario_inicio"))
            val horarioFin = cursor.getString(cursor.getColumnIndex("horario_fin"))
            val dia = cursor.getString(cursor.getColumnIndex("dia"))
            val aula = cursor.getString(cursor.getColumnIndex("aula"))
            val seccionNombre = cursor.getString(cursor.getColumnIndex("seccion_nombre"))
            val gradoNombre = cursor.getString(cursor.getColumnIndex("grado_nombre"))
            val nivelNombre = cursor.getString(cursor.getColumnIndex("nivel_nombre"))
            val profesorNombre = cursor.getString(cursor.getColumnIndex("profesor_nombre"))

            // Create a TextView for each field
            val textViewId = TextView(this)
            textViewId.text = id.toString()
            textViewId.gravity = Gravity.CENTER  // Center the text

            val textViewNombre = TextView(this)
            textViewNombre.text = nombre
            textViewNombre.gravity = Gravity.CENTER  // Center the text

            val textViewHorarioInicio = TextView(this)
            textViewHorarioInicio.text = horarioInicio
            textViewHorarioInicio.gravity = Gravity.CENTER  // Center the text

            val textViewHorarioFin = TextView(this)
            textViewHorarioFin.text = horarioFin
            textViewHorarioFin.gravity = Gravity.CENTER  // Center the text

            val textViewDia = TextView(this)
            textViewDia.text = dia
            textViewDia.gravity = Gravity.CENTER  // Center the text

            val textViewAula = TextView(this)
            textViewAula.text = aula
            textViewAula.gravity = Gravity.CENTER  // Center the text

            val textViewSeccionNombre = TextView(this)
            textViewSeccionNombre.text = seccionNombre
            textViewSeccionNombre.gravity = Gravity.CENTER  // Center the text

            val textViewGradoNombre = TextView(this)
            textViewGradoNombre.text = gradoNombre
            textViewGradoNombre.gravity = Gravity.CENTER  // Center the text

            val textViewNivelNombre = TextView(this)
            textViewNivelNombre.text = nivelNombre
            textViewNivelNombre.gravity = Gravity.CENTER  // Center the text

            val textViewProfesorNombre = TextView(this)
            textViewProfesorNombre.text = profesorNombre
            textViewProfesorNombre.gravity = Gravity.CENTER  // Center the text

            // Add the TextViews to the row
            tableRow.addView(textViewId)
            tableRow.addView(textViewNombre)
            tableRow.addView(textViewGradoNombre)
            tableRow.addView(textViewNivelNombre)
            tableRow.addView(textViewSeccionNombre)
            tableRow.addView(textViewHorarioInicio)
            tableRow.addView(textViewHorarioFin)
            tableRow.addView(textViewDia)
            tableRow.addView(textViewAula)
            tableRow.addView(textViewProfesorNombre)

            // Add the row to the TableLayout
            tableLayout.addView(tableRow)
        }
        cursor.close()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_administrador, menu)
        return true
    }

    private fun OnNavigationItemSelectedListener() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.iconCursos -> {
                    val intent = Intent(this, AdministradorActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.iconProfesor -> {
                    val intent = Intent(this, ProfesorAdmin::class.java)
                    startActivity(intent)
                    true
                }
                R.id.iconAlumno -> {
                    val intent = Intent(this, AlumnoAdmin::class.java)
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

    private fun navigationCourse() {
        val btnNivel = findViewById<Button>(R.id.btnNivel)
        btnNivel.setOnClickListener {
            val intent = Intent(this, NivelAdmin::class.java)
            startActivity(intent)
        }

        val btnGrado = findViewById<Button>(R.id.btnGrado)
        btnGrado.setOnClickListener {
            val intent = Intent(this, GradoAdmin::class.java)
            startActivity(intent)
        }

        val btnSeccion = findViewById<Button>(R.id.btnSeccion)
        btnSeccion.setOnClickListener {
            val intent = Intent(this, SeccionAdmin::class.java)
            startActivity(intent)
        }
    }
}