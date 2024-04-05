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
        viewAdapter = MyAdapter(myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    class MyAdapter(private val myDataset: List<Curso>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cursoprofesor, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val curso = myDataset[position]
            holder.view.findViewById<TextView>(R.id.tvNombreCurso).text = curso.nombre
            holder.view.findViewById<TextView>(R.id.tvNivel).text = curso.nivel_nombre
            holder.view.findViewById<TextView>(R.id.tvGrado).text = curso.grado_nombre
            holder.view.findViewById<TextView>(R.id.tvSeccion).text = curso.seccion_nombre
            holder.view.findViewById<TextView>(R.id.tvNumeroAula).text = curso.aula
            holder.view.findViewById<TextView>(R.id.tvHoraInicio).text = curso.horario_inicio
            holder.view.findViewById<TextView>(R.id.tvHoraFin).text = curso.horario_fin

        }

        override fun getItemCount() = myDataset.size
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation_menu_profesor, menu)
        return true
    }
}