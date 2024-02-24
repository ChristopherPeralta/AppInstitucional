package com.example.appinstitucional.model

import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// ...




fun obtenerUsuario(email: String): model.Usuario? {
    val obtenerUsuarioTask = object : AsyncTask<String, Void, model.Usuario?>() {
        override fun doInBackground(vararg params: String): model.Usuario? {
            val url = URL("http://tu-api.com/usuarios?email=${params[0]}")//USAR XAMP Y CAMBIAR ESTO
            val connection = url.openConnection() as HttpURLConnection

            try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                val jsonObject = JSONObject(response)

                // Aquí debes adaptar el código a la estructura de tu JSON
                val nombre = jsonObject.getString("nombre")
                val apellido = jsonObject.getString("apellido")
                val email = jsonObject.getString("email")
                val contraseña = jsonObject.getString("contraseña")
                val rol = jsonObject.getString("rol")

                return when (rol) {
                    "alumno" -> model.Alumno(nombre, apellido, email, contraseña)
                    "profesor" -> model.Profesor(nombre, apellido, email, contraseña)
                    "administrador" -> model.Administrador(nombre, apellido, email, contraseña)
                    else -> null
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    return obtenerUsuarioTask.execute(email).get()
}