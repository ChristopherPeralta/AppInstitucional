package com.example.appinstitucional.ui

import DatabaseHelper
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Login.adaptador.IntroductionPagerAdapter

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = IntroductionPagerAdapter(this)

        Rol()
        Usuario_Rol()
    }

    private fun Usuario_Rol() {
        val db = DatabaseHelper(this).writableDatabase

        val usuarios = arrayOf("admin", "profesor", "alumno")
        val password = "123"

        for (usuario in usuarios) {
            // Verifica si el usuario ya existe en la tabla "usuario"
            val cursorUsuarioExistente = db.rawQuery("SELECT * FROM usuario WHERE correo = ?", arrayOf(usuario))
            if (cursorUsuarioExistente.count == 0) {
                // Inserta el usuario en la tabla "usuario"
                db.execSQL("INSERT INTO usuario (correo, contraseña) VALUES ('$usuario', '$password')")

                // Obtiene el ID del nuevo usuario
                val cursorUsuario = db.rawQuery("SELECT id FROM usuario WHERE correo = ?", arrayOf(usuario))
                val idUsuario = if (cursorUsuario.moveToFirst()) cursorUsuario.getInt(0) else null
                cursorUsuario.close()

                // Obtiene el ID del rol
                val cursorRol = db.rawQuery("SELECT id FROM rol WHERE nombre = ?", arrayOf(usuario))
                val idRol = if (cursorRol.moveToFirst()) cursorRol.getInt(0) else null
                cursorRol.close()

                // Asocia el usuario con su rol en la tabla "usuario_rol"
                if (idUsuario != null && idRol != null) {
                    db.execSQL("INSERT INTO usuario_rol (id_usuario, rol_id) VALUES (?, ?)", arrayOf(idUsuario, idRol))
                }
            }
            cursorUsuarioExistente.close()
        }
    }

    private fun Rol() {
        val db = DatabaseHelper(this).writableDatabase

        val roles = arrayOf("admin", "profesor", "alumno")

        for (rol in roles) {
            val cursor = db.rawQuery("SELECT * FROM rol WHERE nombre = ?", arrayOf(rol))
            if (cursor.count == 0) {
                db.execSQL("INSERT INTO rol (nombre) VALUES ('$rol')")
            }
            cursor.close()
        }
    }
}