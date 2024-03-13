package com.example.appinstitucional.ui.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Administrador.AdministradorActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            val intent = Intent(this, AdministradorActivity::class.java)
            startActivity(intent)
        }

        val olvideContrasena = findViewById<TextView>(R.id.olvidasteContrasena)
        olvideContrasena.setOnClickListener {
            val intent = Intent(this, OlvideContrasena::class.java)
            startActivity(intent)
        }

    }
}
