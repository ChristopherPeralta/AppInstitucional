package com.example.appinstitucional.ui.Login

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Administrador.AdministradorActivity
import com.example.appinstitucional.ui.Alumno.AlumnoActivity
import com.example.appinstitucional.ui.Profesor.ProfesorActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        val olvideContrasena = findViewById<TextView>(R.id.olvidasteContrasena)
        olvideContrasena.setOnClickListener {
            val intent = Intent(this, OlvideContrasena::class.java)
            startActivity(intent)
        }

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            val correo = email.text.toString()
            val contraseña = password.text.toString()

            val dbHelper = DatabaseHelper(this)
            val rol = dbHelper.getRol(correo, contraseña)

            val intent = when (rol) {
                "admin" -> Intent(this, AdministradorActivity::class.java)
                "alumno" -> Intent(this, AlumnoActivity::class.java)
                "profesor" -> Intent(this, ProfesorActivity::class.java)
                else -> null
            }

            if (intent != null) {
                startActivity(intent)
            } else {
                // Mostrar un mensaje de error al usuario
            }
        }


    }
}
