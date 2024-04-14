package com.example.appinstitucional.ui.Alumno

import DatabaseHelper
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Administrador.AdministradorActivity
import com.example.appinstitucional.ui.Administrador.AlumnoAdmin
import com.example.appinstitucional.ui.Administrador.ProfesorAdmin
import com.example.appinstitucional.ui.Profesor.CursoAsistenciaProfesor
import com.example.appinstitucional.ui.Profesor.CursoNotasProfesor
import com.example.appinstitucional.ui.Profesor.ProfesorActivity
import com.example.appinstitucional.ui.Profesor.SearchProfesorActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlumnoCursoNotas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno_curso_notas)

        val cursoId = intent.getIntExtra("cursoId", -1)
        val alumnoId = intent.getIntExtra("alumnoId", -1) // Retrieve the student's ID

        if (cursoId != -1 && alumnoId != -1) {
            val databaseHelper = DatabaseHelper(this)
            val curso = databaseHelper.getCursoById(cursoId)
            if (curso != null) {
                val textViewCursoName = findViewById<TextView>(R.id.textViewCursoName)
                textViewCursoName?.text = curso.nombre

                val grades = databaseHelper.getNotasPorCursoYAlumno(cursoId, alumnoId)

                val average = grades.filterNotNull().average()

                val tvN1 = findViewById<TextView>(R.id.tvN1)
                val tvN2 = findViewById<TextView>(R.id.tvN2)
                val tvN3 = findViewById<TextView>(R.id.tvN3)
                val tvN4 = findViewById<TextView>(R.id.tvN4)
                val tvPromedio = findViewById<TextView>(R.id.tvPromedio)

                tvN1.text = grades.getOrNull(0)?.toString() ?: "N/A"
                tvN2.text = grades.getOrNull(1)?.toString() ?: "N/A"
                tvN3.text = grades.getOrNull(2)?.toString() ?: "N/A"
                tvN4.text = grades.getOrNull(3)?.toString() ?: "N/A"
                tvPromedio.text = average.toString()

                if (average >= 12) {
                    tvPromedio.setTextColor(ContextCompat.getColor(this, R.color.Verde500))
                } else {
                    tvPromedio.setTextColor(ContextCompat.getColor(this, R.color.Naranja500))
                }
            }


        }



        NavegacionNotaCurso()
        updateNavigationSelection()
        OnNavigationItemSelectedListener()
    }

    private fun NavegacionNotaCurso() {
        val btnNotas: Button = findViewById(R.id.btnNotas)
        btnNotas.setOnClickListener {
            val intent = Intent(this, AlumnoCursoNotas::class.java)
            startActivity(intent)
        }

        val btnAsistencia: Button = findViewById(R.id.btnAsistencia)
        btnAsistencia.setOnClickListener {
            val intent = Intent(this, AlumnoCursoAsistencia::class.java)
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