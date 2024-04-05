package com.example.appinstitucional.model.Class

data class Curso(
    val id: Int,
    val nombre: String,
    val horario_inicio: String,
    val horario_fin: String,
    val dia: String,
    val aula: String,
    val seccion_nombre: String,
    val grado_nombre: String,
    val nivel_nombre: String
)