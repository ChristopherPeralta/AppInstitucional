package com.example.appinstitucional.ui.Alumno

import DatabaseHelper
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Administrador.AdministradorActivity
import com.example.appinstitucional.ui.Administrador.AlumnoAdmin
import com.example.appinstitucional.ui.Administrador.ProfesorAdmin
import com.example.appinstitucional.ui.Alumno.adapter.CursoAlumnoAdapter
import com.example.appinstitucional.ui.Profesor.CursoAsistenciaProfesor
import com.example.appinstitucional.ui.Profesor.CursoNotasProfesor
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlumnoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        // Get the ID of the logged-in student from the shared preferences
        val sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val idAlumno = sharedPreferences.getInt("idAlumno", -1)
        Log.println(Log.INFO, "idAlumno", idAlumno.toString())

        // Fetch the courses of the student from the database
        val databaseHelper = DatabaseHelper(this)
        val cursos = databaseHelper.getCursosPorAlumno(idAlumno)

        viewManager = LinearLayoutManager(this)
        viewAdapter = CursoAlumnoAdapter(cursos) // Usa CursoAdapter en lugar de MyAdapter

        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        updateNavigationSelection()
        OnNavigationItemSelectedListener()
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
                    val intent = Intent(this, AlumnoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.iconProfesor -> {
                    val intent = Intent(this, AlumnoCursoAsistencia::class.java)
                    startActivity(intent)
                    true
                }
                R.id.iconAlumno -> {
                    val intent = Intent(this, AlumnoBusqueda::class.java)
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