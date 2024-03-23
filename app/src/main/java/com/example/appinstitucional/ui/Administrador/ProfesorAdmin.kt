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
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfesorAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profesor)

        fillTable()

        dialog()



        updateNavigationSelection()
        OnNavigationItemSelectedListener()

    }

    @SuppressLint("MissingInflatedId")
    private fun dialog() {
        val btnAgregar: ImageButton = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_profesor, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nuevo Profesor")
            val alertDialog = builder.show()

            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            val btnCreateProfesor: Button = dialogView.findViewById(R.id.btnCreateCourse)
            btnCreateProfesor.setOnClickListener {
                val nombre = dialogView.findViewById<EditText>(R.id.etNombre).text.toString()
                val apellido = dialogView.findViewById<EditText>(R.id.etApellidos).text.toString()
                val dni = dialogView.findViewById<EditText>(R.id.etDni).text.toString()
                val telefono = dialogView.findViewById<EditText>(R.id.etTelefono).text.toString()
                val correo = dialogView.findViewById<EditText>(R.id.etCorreo).text.toString()
                val contraseña = dialogView.findViewById<EditText>(R.id.etContrasena).text.toString()

                val dbHelper = DatabaseHelper(this)
                dbHelper.createProfesorAndUser(nombre, apellido, dni, telefono, correo, contraseña)

                fillTable()
                alertDialog.dismiss()
            }
        }
    }

    @SuppressLint("Range")
    private fun fillTable() {
        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getProfesor()

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
            val apellido = cursor.getString(cursor.getColumnIndex("apellido"))
            val dni = cursor.getString(cursor.getColumnIndex("dni"))
            val telefono = cursor.getString(cursor.getColumnIndex("telefono"))
            val correo = cursor.getString(cursor.getColumnIndex("correo"))

            // Crea un TextView para cada campo
            val textViewId = TextView(this)
            textViewId.text = id.toString()
            textViewId.gravity = Gravity.CENTER  // Centra el texto

            val textViewNombre = TextView(this)
            textViewNombre.text = nombre
            textViewNombre.gravity = Gravity.CENTER  // Centra el texto

            val textViewApellido = TextView(this)
            textViewApellido.text = apellido
            textViewApellido.gravity = Gravity.CENTER  // Centra el texto

            val textViewDni = TextView(this)
            textViewDni.text = dni
            textViewDni.gravity = Gravity.CENTER  // Centra el texto

            val textViewTelefono = TextView(this)
            textViewTelefono.text = telefono
            textViewTelefono.gravity = Gravity.CENTER  // Centra el texto

            val textViewCorreo = TextView(this)
            textViewCorreo.text = correo
            textViewCorreo.gravity = Gravity.CENTER  // Centra el texto

            // Agrega los TextViews a la fila
            tableRow.addView(textViewId)
            tableRow.addView(textViewNombre)
            tableRow.addView(textViewApellido)
            tableRow.addView(textViewDni)
            tableRow.addView(textViewTelefono)
            tableRow.addView(textViewCorreo)

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
}