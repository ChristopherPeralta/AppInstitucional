package com.example.appinstitucional.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.appinstitucional.R
import org.w3c.dom.Text

class OlvideContrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olvide_contrasena)

        val email = findViewById<EditText>(R.id.email)

        val btnEnviar = findViewById<Button>(R.id.btnRecuperar)
        btnEnviar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Recuperación de contraseña")
                .setMessage("El código de recuperación ha sido enviado a su correo electrónico.")
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        }

        val inciarSesion = findViewById<TextView>(R.id.inciarSesion)
        inciarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}