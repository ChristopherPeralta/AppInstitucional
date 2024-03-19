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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class GradoAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_grado)

        fillTable()
        dialog()

        navigationCourse()
        updateNavigationSelection()
        OnNavigationItemSelectedListener()

    }

    private fun dialog() {
        val btnAgregarCurso: ImageButton = findViewById(R.id.btnAgregar)
        btnAgregarCurso.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_grado, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nuevo Grado")
            val alertDialog = builder.show()


            val spinner: Spinner = dialogView.findViewById(R.id.spinnerCourseLevel)
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
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }.start()


            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel) // Busca el botón btnCancel en dialogView
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            val btnCreateCourse: Button = dialogView.findViewById(R.id.btnCreateCourse)
            btnCreateCourse.setOnClickListener {
                // Recoge el nombre del grado del diálogo
                val nombreGrado = dialogView.findViewById<EditText>(R.id.etName).text.toString()

                // Recoge el ID del nivel del Spinner
                val idNivel = spinner.selectedItemPosition + 1  // Asume que los IDs de nivel comienzan en 1

                // Inserta el grado en la base de datos
                dbHelper.insertGrados(nombreGrado, idNivel)

                fillTable()
                alertDialog.dismiss() // Cierra el diálogo
            }
        }
    }

    private fun fillTable() {
        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getGrados()

        val tableLayout: TableLayout = findViewById(R.id.tableLayout)
        val headerIndex = tableLayout.indexOfChild(findViewById(R.id.tableHeader))

        // Elimina todas las filas excepto la primera
        for (i in tableLayout.childCount - 1 downTo headerIndex + 1) {
            tableLayout.removeViewAt(i)
        }

        while (cursor.moveToNext()) {
            // Crea una nueva fila
            val tableRow = TableRow(this)

            // Obtiene los datos de la fila actual
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            val nivelNombre = cursor.getString(cursor.getColumnIndex("nivel_nombre"))

            // Crea un TextView para cada campo
            val textViewId = TextView(this)
            textViewId.text = id.toString()
            textViewId.gravity = Gravity.CENTER  // Centra el texto

            val textViewNombre = TextView(this)
            textViewNombre.text = nombre
            textViewNombre.gravity = Gravity.CENTER  // Centra el texto

            val textViewNivelNombre = TextView(this)
            textViewNivelNombre.text = nivelNombre
            textViewNivelNombre.gravity = Gravity.CENTER  // Centra el texto


            // Agrega los TextViews a la fila
            tableRow.addView(textViewId)
            tableRow.addView(textViewNivelNombre)
            tableRow.addView(textViewNombre)

            // Agrega la fila al TableLayout
            tableLayout.addView(tableRow)
        }
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