package com.example.appinstitucional.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.appinstitucional.R
import com.example.appinstitucional.model.model
import com.example.appinstitucional.model.obtenerUsuario
import com.example.appinstitucional.ui.Administrador.AdministradorActivity
import com.example.appinstitucional.ui.Alumno.AlumnoActivity
import com.example.appinstitucional.ui.Profesor.ProfesorActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)

        login.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            // Recuperar los datos del usuario de la base de datos
            val usuario = obtenerUsuario(emailText)

            // Verificar la contraseña
            if (usuario != null && usuario.contraseña == passwordText) {
                // Navegar a la actividad correspondiente al rol del usuario
                when (usuario) {
                    is model.Alumno -> navegarAAlumnoActivity()
                    is model.Profesor -> navegarAProfesorActivity()
                    is model.Administrador -> navegarAAdministradorActivity()
                }
            } else {
                // Mostrar un mensaje de error
            }
        }
    }

    private fun navegarAAlumnoActivity() {
        val intent = Intent(this, AlumnoActivity::class.java)
        startActivity(intent)
    }

    private fun navegarAProfesorActivity() {
        val intent = Intent(this, ProfesorActivity::class.java)
        startActivity(intent)
    }

    private fun navegarAAdministradorActivity() {
        val intent = Intent(this, AdministradorActivity::class.java)
        startActivity(intent)
    }
}
