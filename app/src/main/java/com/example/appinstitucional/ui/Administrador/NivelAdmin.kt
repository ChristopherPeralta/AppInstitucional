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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NivelAdmin : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_nivel)

        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getNiveles()

        fillTable()

        //BOTON AGREGAR NIVEL
        val btnAgregarNivel: ImageButton = findViewById(R.id.btnAgregar)
        btnAgregarNivel.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_nivel, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nuevo Nivel")
            val alertDialog = builder.show()

            val btnCreateCourse: Button = dialogView.findViewById(R.id.btnCreateCourse)
            btnCreateCourse.setOnClickListener {
                // Recoge el nombre del nivel del diálogo
                val nombreNivel = dialogView.findViewById<EditText>(R.id.etName).text.toString()

                // Comprueba si el nombre del nivel es válido
                if (nombreNivel in listOf("inicial", "primaria", "secundaria")) {
                    // Inserta el nivel en la base de datos
                    dbHelper.insertNivel(nombreNivel)
                    fillTable() // Actualiza la tabla
                    alertDialog.dismiss() // Cierra el diálogo
                } else {
                    // Muestra un mensaje de error al usuario
                    Toast.makeText(this, "Nombre de nivel no válido", Toast.LENGTH_SHORT).show()
                }
            }

            val btnCancel: Button =
                dialogView.findViewById(R.id.btnCancel) // Busca el botón btnCancel en dialogView
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        

        navigationCourse()
        updateNavigationSelection()
        OnNavigationItemSelectedListener()

    }

    private fun fillTable() {
        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getNiveles()

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

            // Create a TextView for each field
            val textViewId = TextView(this)
            textViewId.text = id.toString()
            textViewId.gravity = Gravity.CENTER  // Center the text

            val textViewNombre = TextView(this)
            textViewNombre.text = nombre
            textViewNombre.gravity = Gravity.CENTER  // Center the text

            // Add the TextViews to the row
            tableRow.addView(textViewId)
            tableRow.addView(textViewNombre)

            // Add the row to the TableLayout
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