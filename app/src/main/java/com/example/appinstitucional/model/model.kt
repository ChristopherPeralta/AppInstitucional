package com.example.appinstitucional.model

class model {

    //Roles
    open class Usuario(val nombre: String, val apellido: String, val email: String, val contraseña: String) {
        open fun login() {
            // Implementar lógica de inicio de sesión
        }
    }

    class Alumno(nombre: String, apellido: String, email: String, contraseña: String) : Usuario(nombre, apellido, email, contraseña) {
        override fun login() {
            // Implementar lógica de inicio de sesión para Alumno
        }

        fun verNotas() {
            // Implementar lógica para ver notas
        }
    }

    class Profesor(nombre: String, apellido: String, email: String, contraseña: String) : Usuario(nombre, apellido, email, contraseña) {
        override fun login() {
            // Implementar lógica de inicio de sesión para Profesor
        }

        fun asignarNotas() {
            // Implementar lógica para asignar notas
        }
    }

    class Administrador(nombre: String, apellido: String, email: String, contraseña: String) : Usuario(nombre, apellido, email, contraseña) {
        override fun login() {
            // Implementar lógica de inicio de sesión para Administrador
        }

        fun crearCurso() {
            // Implementar lógica para crear cursos
        }
    }


    class Curso(val nombre: String, val nivel: String, val grado: Int, val profesor: Profesor)

    class Nota(val curso: Curso, val alumno: Alumno, val notas: List<Double>) {
        fun promedio(): Double {
            return notas.sum() / notas.size
        }
    }

    class Asistencia(val curso: Curso, val alumno: Alumno, val fecha: String)
}