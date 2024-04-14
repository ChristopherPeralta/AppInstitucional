package com.example.appinstitucional.ui.Profesor

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstitucional.R
import com.example.appinstitucional.model.Class.Curso
import com.example.appinstitucional.ui.Administrador.AdministradorActivity
import com.example.appinstitucional.ui.Administrador.AlumnoAdmin
import com.example.appinstitucional.ui.Administrador.ProfesorAdmin
import com.example.appinstitucional.ui.Profesor.adapter.CursoAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

    class ProfesorActivity : AppCompatActivity() {
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profesor)

            val sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
            val idProfesor = sharedPreferences.getInt("idProfesor", -1)

            val databaseHelper = DatabaseHelper(this)
            val myDataset = databaseHelper.getCursosPorProfesor(idProfesor)

            viewManager = LinearLayoutManager(this)
            viewAdapter = CursoAdapter(myDataset) // Usa CursoAdapter en lugar de MyAdapter

            recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }



        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.bottom_navigation_menu_profesor, menu)
            return true
        }
    }
