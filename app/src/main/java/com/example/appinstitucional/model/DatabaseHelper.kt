import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bd_colegio"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USER_TABLE =
            "CREATE TABLE usuario(id INTEGER PRIMARY KEY, correo TEXT, contraseña TEXT)"
        val CREATE_ROL_TABLE =
            "CREATE TABLE rol(id INTEGER PRIMARY KEY, nombre TEXT CHECK( nombre IN ('admin', 'profesor', 'alumno')))"
        val CREATE_USUARIO_ROL_TABLE =
            "CREATE TABLE usuario_rol(id_usuario INTEGER, rol_id INTEGER, FOREIGN KEY(id_usuario) REFERENCES usuario(id), FOREIGN KEY(rol_id) REFERENCES rol(id))"
        val CREATE_PROFESOR_TABLE =
            "CREATE TABLE profesor(id INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, dni TEXT, telefono TEXT, id_usuario INTEGER, FOREIGN KEY(id_usuario) REFERENCES usuario(id))"
        val CREATE_NIVEL_TABLE =
            "CREATE TABLE nivel(id INTEGER PRIMARY KEY, nombre TEXT CHECK( nombre IN ('inicial', 'primaria', 'secundaria')))"
        val CREATE_GRADO_TABLE =
            "CREATE TABLE grado(id INTEGER PRIMARY KEY, nombre TEXT, id_nivel INTEGER, FOREIGN KEY(id_nivel) REFERENCES nivel(id))"
        val CREATE_SECCION_TABLE =
            "CREATE TABLE seccion(id INTEGER PRIMARY KEY, nombre TEXT, id_grado INTEGER, FOREIGN KEY(id_grado) REFERENCES grado(id))"
        val CREATE_ALUMNO_TABLE =
            "CREATE TABLE alumno(id INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, dni TEXT, correo TEXT, id_usuario INTEGER, nivel_id INTEGER, FOREIGN KEY(id_usuario) REFERENCES usuario(id), FOREIGN KEY(nivel_id) REFERENCES nivel(id))"
        val CREATE_CURSO_TABLE =
            "CREATE TABLE curso(id INTEGER PRIMARY KEY, nombre TEXT, horario_inicio TEXT, horario_fin TEXT, dia TEXT, aula TEXT, nivel_id INTEGER, FOREIGN KEY(nivel_id) REFERENCES nivel(id))"
        val CREATE_PROFESOR_CURSO_TABLE =
            "CREATE TABLE profesor_curso(id_profesor INTEGER, id_curso INTEGER, FOREIGN KEY(id_profesor) REFERENCES profesor(id), FOREIGN KEY(id_curso) REFERENCES curso(id))"

        db.execSQL(CREATE_USER_TABLE)
        db.execSQL(CREATE_ROL_TABLE)
        db.execSQL(CREATE_USUARIO_ROL_TABLE)
        db.execSQL(CREATE_PROFESOR_TABLE)
        db.execSQL(CREATE_NIVEL_TABLE)
        db.execSQL(CREATE_GRADO_TABLE)
        db.execSQL(CREATE_SECCION_TABLE)
        db.execSQL(CREATE_ALUMNO_TABLE)
        db.execSQL(CREATE_CURSO_TABLE)
        db.execSQL(CREATE_PROFESOR_CURSO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS rol")
        db.execSQL("DROP TABLE IF EXISTS usuario_rol")
        db.execSQL("DROP TABLE IF EXISTS profesor")
        db.execSQL("DROP TABLE IF EXISTS nivel")
        db.execSQL("DROP TABLE IF EXISTS grado")
        db.execSQL("DROP TABLE IF EXISTS seccion")
        db.execSQL("DROP TABLE IF EXISTS alumno")
        db.execSQL("DROP TABLE IF EXISTS curso")
        db.execSQL("DROP TABLE IF EXISTS profesor_curso")

        onCreate(db)
    }

    fun getRol(correo: String, contraseña: String): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT rol.nombre FROM usuario INNER JOIN usuario_rol ON usuario.id = usuario_rol.id_usuario INNER JOIN rol ON usuario_rol.rol_id = rol.id WHERE usuario.correo = ? AND usuario.contraseña = ?", arrayOf(correo, contraseña))
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            ""
        }.also {
            cursor.close()
        }
    }

    fun getNiveles(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM nivel", null)
    }

    fun insertNivel(nombre: String) {
        val db = this.writableDatabase
        val CREATE_NIVEL = "INSERT INTO nivel (nombre) VALUES ('$nombre')"
        db.execSQL(CREATE_NIVEL)
    }

    fun getGrados(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT grado.id, grado.nombre, nivel.nombre AS nivel_nombre FROM grado INNER JOIN nivel ON grado.id_nivel = nivel.id", null)
    }

    fun insertGrados(nombre: String, idNivel: Int) {
        val db = this.writableDatabase
        val CREATE_GRADO = "INSERT INTO grado (nombre, id_nivel) VALUES ('$nombre', $idNivel)"
        db.execSQL(CREATE_GRADO)
    }

    fun getGradosPorNivel(nivel: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT grado.* FROM grado INNER JOIN nivel ON grado.id_nivel = nivel.id WHERE nivel.nombre = ?", arrayOf(nivel))
    }

    fun getSeccionesConNivelYGrado(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT seccion.id, seccion.nombre, grado.nombre AS grado_nombre, nivel.nombre AS nivel_nombre FROM seccion INNER JOIN grado ON seccion.id_grado = grado.id INNER JOIN nivel ON grado.id_nivel = nivel.id", null)
    }

    fun insertSeccion(nombreSeccion: String, idGrado: Int) {
        val db = this.writableDatabase
        db.execSQL("INSERT INTO seccion (nombre, id_grado) VALUES (?, ?)", arrayOf(nombreSeccion, idGrado))
    }

    fun getProfesor(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT profesor.id, profesor.nombre, profesor.apellido, profesor.dni, profesor.telefono, usuario.correo FROM profesor INNER JOIN usuario ON profesor.id_usuario = usuario.id", null)
    }

    fun createProfesorAndUser(nombre: String, apellido: String, dni: String, telefono: String, correo: String, contraseña: String) {
        val db = this.writableDatabase

        // Inserta el nuevo usuario
        val CREATE_USER = "INSERT INTO usuario (correo, contraseña) VALUES ('$correo', '$contraseña')"
        db.execSQL(CREATE_USER)

        // Obtiene el ID del nuevo usuario
        val cursor = db.rawQuery("SELECT id FROM usuario WHERE correo = ?", arrayOf(correo))
        val idUsuario = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()

        // Inserta el nuevo profesor
        if (idUsuario != null) {
            val CREATE_PROFESOR = "INSERT INTO profesor (nombre, apellido, dni, telefono, id_usuario) VALUES ('$nombre', '$apellido', '$dni', '$telefono', $idUsuario)"
            db.execSQL(CREATE_PROFESOR)

            // Obtiene el ID del rol de profesor
            val cursorRol = db.rawQuery("SELECT id FROM rol WHERE nombre = ?", arrayOf("profesor"))
            val idRol = if (cursorRol.moveToFirst()) cursorRol.getInt(0) else null
            cursorRol.close()

            // Inserta el rol de profesor para el nuevo usuario
            if (idRol != null) {
                val CREATE_USUARIO_ROL = "INSERT INTO usuario_rol (id_usuario, rol_id) VALUES ($idUsuario, $idRol)"
                db.execSQL(CREATE_USUARIO_ROL)
            }
        }
    }

    fun getAlumnos(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM alumno", null)
    }

    fun createAlumnoAndUser(nombre: String, apellido: String, dni: String, correo: String, contraseña: String, idNivel: Int) {
        val db = this.writableDatabase

        // Inserta el nuevo usuario
        val CREATE_USER = "INSERT INTO usuario (correo, contraseña) VALUES ('$correo', '$contraseña')"
        db.execSQL(CREATE_USER)

        // Obtiene el ID del nuevo usuario
        val cursor = db.rawQuery("SELECT id FROM usuario WHERE correo = ?", arrayOf(correo))
        val idUsuario = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()

        // Inserta el nuevo alumno
        if (idUsuario != null) {
            val CREATE_ALUMNO = "INSERT INTO alumno (nombre, apellido, dni, correo, id_usuario, nivel_id) VALUES ('$nombre', '$apellido', '$dni', '$correo', $idUsuario, $idNivel)"
            db.execSQL(CREATE_ALUMNO)

            // Obtiene el ID del rol de alumno
            val cursorRol = db.rawQuery("SELECT id FROM rol WHERE nombre = ?", arrayOf("alumno"))
            val idRol = if (cursorRol.moveToFirst()) cursorRol.getInt(0) else null
            cursorRol.close()

            // Inserta el rol de alumno para el nuevo usuario
            if (idRol != null) {
                val CREATE_USUARIO_ROL = "INSERT INTO usuario_rol (id_usuario, rol_id) VALUES ($idUsuario, $idRol)"
                db.execSQL(CREATE_USUARIO_ROL)
            }
        }
    }
}

