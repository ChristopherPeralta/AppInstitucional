package com.example.appinstitucional.ui.Administrador

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import com.example.appinstitucional.model.Class.Grado
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlumnoAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_alumno)

        fillTable()

        dialog()

        updateNavigationSelection()
        OnNavigationItemSelectedListener()

    }

    private fun dialog() {
        val btnAgregar: ImageButton = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_alumno, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nuevo Alumno")
            val alertDialog = builder.show()

            // Configura los Spinners
            val spinnerCourseLevel: Spinner = dialogView.findViewById(R.id.spinnerCourseLevel)
            val spinnerCourseGrade: Spinner = dialogView.findViewById(R.id.spinnerCourseGrade)
            val spinnerCourseSection: Spinner = dialogView.findViewById(R.id.spinnerCourseSection)


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
                                    this@AlumnoAdmin,
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
                                    this@AlumnoAdmin,
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
            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel) // Busca el botón btnCancel en dialogView
            btnCancel.setOnClickListener {
                alertDialog?.dismiss()
            }

            val btnCreateAlumno: Button = dialogView.findViewById(R.id.btnCreateCourse)
            btnCreateAlumno.setOnClickListener {
                // Recoge los datos del alumno del diálogo
                val nombre = dialogView.findViewById<EditText>(R.id.etName).text.toString()
                val apellido = dialogView.findViewById<EditText>(R.id.etApellidos).text.toString()
                val dni = dialogView.findViewById<EditText>(R.id.etDNI).text.toString()
                val correo = dialogView.findViewById<EditText>(R.id.etCorreo).text.toString()
                val contraseña = dialogView.findViewById<EditText>(R.id.etContrasena).text.toString()
                val nivelSeleccionado = spinnerCourseLevel.selectedItem.toString()
                val gradoSeleccionado = spinnerCourseGrade.selectedItem.toString()

                // Obtiene el id de la sección seleccionada
                val idSeccion = spinnerCourseSection.selectedItemPosition + 1

                // Inserta el alumno en la base de datos
                dbHelper.createAlumnoAndUser(nombre, apellido, dni, correo, contraseña, idSeccion)

                fillTable()
                alertDialog?.dismiss() // Cierra el diálogo
            }
        }
    }

    private fun fillTable() {
        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getAlumnos()

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
            val apellido = cursor.getString(cursor.getColumnIndex("apellido"))
            val seccion_nombre = cursor.getString(cursor.getColumnIndex("seccion_nombre"))
            val grado_nombre = cursor.getString(cursor.getColumnIndex("grado_nombre"))
            val nivel_nombre = cursor.getString(cursor.getColumnIndex("nivel_nombre"))
            val dni = cursor.getString(cursor.getColumnIndex("dni"))
            val correo = cursor.getString(cursor.getColumnIndex("correo"))

            // Create a TextView for each field
            val textViewId = TextView(this)
            textViewId.text = id.toString()
            textViewId.gravity = Gravity.CENTER  // Center the text

            val textViewNombre = TextView(this)
            textViewNombre.text = nombre
            textViewNombre.gravity = Gravity.CENTER  // Center the text

            val textViewApellido = TextView(this)
            textViewApellido.text = apellido
            textViewApellido.gravity = Gravity.CENTER  // Center the text

            val textViewSeccionNombre = TextView(this)
            textViewSeccionNombre.text = seccion_nombre
            textViewSeccionNombre.gravity = Gravity.CENTER  // Center the text

            val textViewGradoNombre = TextView(this)
            textViewGradoNombre.text = grado_nombre
            textViewGradoNombre.gravity = Gravity.CENTER  // Center the text

            val textViewNivelNombre = TextView(this)
            textViewNivelNombre.text = nivel_nombre
            textViewNivelNombre.gravity = Gravity.CENTER  // Center the text

            val textViewDni = TextView(this)
            textViewDni.text = dni
            textViewDni.gravity = Gravity.CENTER  // Center the text

            val textViewCorreo = TextView(this)
            textViewCorreo.text = correo
            textViewCorreo.gravity = Gravity.CENTER  // Center the text

            // Add the TextViews to the row
            tableRow.addView(textViewId)
            tableRow.addView(textViewNombre)
            tableRow.addView(textViewApellido)
            tableRow.addView(textViewNivelNombre)
            tableRow.addView(textViewGradoNombre)
            tableRow.addView(textViewSeccionNombre)
            tableRow.addView(textViewDni)
            tableRow.addView(textViewCorreo)

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
                // Aquí puedes manejar la acción de clic en el elemento de repetición
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}