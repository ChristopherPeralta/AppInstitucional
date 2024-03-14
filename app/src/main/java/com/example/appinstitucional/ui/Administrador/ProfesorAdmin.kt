package com.example.appinstitucional.ui.Administrador

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfesorAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profesor)

        val btnAgregar: ImageButton = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_profesor, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nuevo Profesor")
            val alertDialog = builder.show()

            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel) // Busca el botón btnCancel en dialogView
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            val btnCreateCourse: Button = dialogView.findViewById(R.id.btnCreateCourse)
            btnCreateCourse.setOnClickListener {
                // Aquí es donde recoges los datos ingresados por el usuario y creas el nuevo curso

                alertDialog.dismiss() // Cierra el diálogo
            }
        }

        updateNavigationSelection()
        OnNavigationItemSelectedListener()

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