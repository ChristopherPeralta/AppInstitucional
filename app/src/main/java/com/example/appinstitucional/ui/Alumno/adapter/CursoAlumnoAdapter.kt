package com.example.appinstitucional.ui.Alumno.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstitucional.R
import com.example.appinstitucional.model.Class.Curso
import com.example.appinstitucional.ui.Alumno.AlumnoCursoNotas
import com.example.appinstitucional.ui.Profesor.CursoNotasProfesor

class CursoAlumnoAdapter(private val cursos: List<Curso>, private val alumnoId: Int) : RecyclerView.Adapter<CursoAlumnoAdapter.CursoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cursoprofesor, parent, false)
        return CursoViewHolder(view, alumnoId)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = cursos[position]
        holder.tvNombreCurso.text = curso.nombre
        holder.tvNivel.text = curso.nivel_nombre
        holder.tvGrado.text = curso.grado_nombre
        holder.tvSeccion.text = curso.seccion_nombre
        holder.tvNumeroAula.text = curso.aula
        holder.tvHoraInicio.text = curso.horario_inicio
        holder.tvHoraFin.text = curso.horario_fin
    }
    override fun getItemCount() = cursos.size

    inner class CursoViewHolder(val view: View, private val alumnoId: Int) : RecyclerView.ViewHolder(view) {

        val tvNombreCurso: TextView = view.findViewById(R.id.tvNombreCurso)
        val tvNivel: TextView = view.findViewById(R.id.tvNivel)
        val tvGrado: TextView = view.findViewById(R.id.tvGrado)
        val tvSeccion: TextView = view.findViewById(R.id.tvSeccion)
        val tvNumeroAula: TextView = view.findViewById(R.id.tvNumeroAula)
        val tvHoraInicio: TextView = view.findViewById(R.id.tvHoraInicio)
        val tvHoraFin: TextView = view.findViewById(R.id.tvHoraFin)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val curso = cursos[position]
                    val intent = Intent(itemView.context, AlumnoCursoNotas::class.java)
                    intent.putExtra("cursoId", curso.id)
                    intent.putExtra("alumnoId", alumnoId)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}