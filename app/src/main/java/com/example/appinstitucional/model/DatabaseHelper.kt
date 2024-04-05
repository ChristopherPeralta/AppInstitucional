import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appinstitucional.model.Class.Curso

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
            "CREATE TABLE alumno(id INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, dni TEXT, id_usuario INTEGER, seccion_id INTEGER, FOREIGN KEY(id_usuario) REFERENCES usuario(id), FOREIGN KEY(seccion_id) REFERENCES seccion(id))"
        val CREATE_CURSO_TABLE =
            "CREATE TABLE curso(id INTEGER PRIMARY KEY, nombre TEXT, horario_inicio TEXT, horario_fin TEXT, dia TEXT, aula TEXT, seccion_id INTEGER, FOREIGN KEY(seccion_id) REFERENCES seccion(id))"
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

    fun getIdGradoPorNombre(nombre: String, nombreNivel: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT grado.id FROM grado INNER JOIN nivel ON grado.id_nivel = nivel.id WHERE grado.nombre = ? AND nivel.nombre = ?", arrayOf(nombre, nombreNivel))
        val idGrado = if (cursor.moveToFirst()) cursor.getInt(0) else -1
        cursor.close()
        return idGrado
    }

    fun getSeccionesConNivelYGrado(gradoSeleccionado: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT seccion.* FROM seccion INNER JOIN grado ON seccion.id_grado = grado.id WHERE grado.nombre = ?", arrayOf(gradoSeleccionado))
    }

    fun getSecciones(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT seccion.id, seccion.nombre, nivel.nombre AS nivel_nombre, grado.nombre AS grado_nombre FROM seccion INNER JOIN grado ON seccion.id_grado = grado.id INNER JOIN nivel ON grado.id_nivel = nivel.id", null)
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
            val idRol = 2
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
        val query = """
    SELECT alumno.id, alumno.nombre, alumno.apellido, seccion.nombre AS seccion_nombre, grado.nombre AS grado_nombre, nivel.nombre AS nivel_nombre, alumno.dni, usuario.correo
    FROM alumno
    INNER JOIN seccion ON alumno.seccion_id = seccion.id
    INNER JOIN grado ON seccion.id_grado = grado.id
    INNER JOIN nivel ON grado.id_nivel = nivel.id
    INNER JOIN usuario ON alumno.id_usuario = usuario.id
"""
        return db.rawQuery(query, null)
    }

    fun createAlumnoAndUser(nombre: String, apellido: String, dni: String, correo: String, contraseña: String, idSeccion: Int) {
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
            val CREATE_ALUMNO = "INSERT INTO alumno (nombre, apellido, dni, id_usuario, seccion_id) VALUES ('$nombre', '$apellido', '$dni', $idUsuario, $idSeccion)"
            db.execSQL(CREATE_ALUMNO)

            // Obtiene el ID del rol de profesor
            val cursorRol = db.rawQuery("SELECT id FROM rol WHERE nombre = ?", arrayOf("profesor"))
            val idRol = 3
            cursorRol.close()

            // Inserta el rol de profesor para el nuevo usuario
            if (idRol != null) {
                val CREATE_USUARIO_ROL = "INSERT INTO usuario_rol (id_usuario, rol_id) VALUES ($idUsuario, $idRol)"
                db.execSQL(CREATE_USUARIO_ROL)
            }
        }
    }

    fun getCursos(): Cursor {
        val db = this.readableDatabase
        val query = """
    SELECT curso.id, curso.nombre, curso.horario_inicio, curso.horario_fin, curso.dia, curso.aula, seccion.nombre AS seccion_nombre, grado.nombre AS grado_nombre, nivel.nombre AS nivel_nombre, profesor.nombre AS profesor_nombre
    FROM curso
    INNER JOIN seccion ON curso.seccion_id = seccion.id
    INNER JOIN grado ON seccion.id_grado = grado.id
    INNER JOIN nivel ON grado.id_nivel = nivel.id
    INNER JOIN profesor_curso ON curso.id = profesor_curso.id_curso
    INNER JOIN profesor ON profesor_curso.id_profesor = profesor.id
"""
        return db.rawQuery(query, null)
    }

    fun createCursoAndAssignProfesor(nombre: String, horario_inicio: String, horario_fin: String, dia: String, aula: String, seccion_id: Int, id_profesor: Int) {
        val db = this.writableDatabase

        // Insert the new course
        val CREATE_CURSO = "INSERT INTO curso (nombre, horario_inicio, horario_fin, dia, aula, seccion_id) VALUES (?, ?, ?, ?, ?, ?)"
        db.execSQL(CREATE_CURSO, arrayOf(nombre, horario_inicio, horario_fin, dia, aula, seccion_id))

        val cursor = db.rawQuery(
            "SELECT id FROM curso WHERE nombre = ? AND horario_inicio = ? AND horario_fin = ? AND dia = ? AND aula = ? AND seccion_id = ?",
            arrayOf(nombre, horario_inicio, horario_fin, dia, aula, seccion_id.toString())
        )
        val id_curso = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()

        // Insert the new record into the profesor_curso table
        if (id_curso != null) {
            val CREATE_PROFESOR_CURSO = "INSERT INTO profesor_curso (id_profesor, id_curso) VALUES (?, ?)"
            db.execSQL(CREATE_PROFESOR_CURSO, arrayOf(id_profesor, id_curso))
        }
    }

    fun getProfesores(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM profesor", null)
    }

    fun getSeccionIdByName(seccionNombre: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT id FROM seccion WHERE nombre = ?", arrayOf(seccionNombre))
    }

    fun getProfesorIdByName(profesorNombre: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT id FROM profesor WHERE nombre = ?", arrayOf(profesorNombre))
    }

    fun getCursosPorProfesor(idProfesor: Int): List<Curso> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
        SELECT curso.*, seccion.nombre AS seccion_nombre, grado.nombre AS grado_nombre, nivel.nombre AS nivel_nombre
        FROM curso
        INNER JOIN seccion ON curso.seccion_id = seccion.id
        INNER JOIN grado ON seccion.id_grado = grado.id
        INNER JOIN nivel ON grado.id_nivel = nivel.id
        INNER JOIN profesor_curso ON curso.id = profesor_curso.id_curso
        WHERE profesor_curso.id_profesor = ?
    """, arrayOf(idProfesor.toString()))
        val cursos = mutableListOf<Curso>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            val horario_inicio = cursor.getString(cursor.getColumnIndex("horario_inicio"))
            val horario_fin = cursor.getString(cursor.getColumnIndex("horario_fin"))
            val dia = cursor.getString(cursor.getColumnIndex("dia"))
            val aula = cursor.getString(cursor.getColumnIndex("aula"))
            val seccion_nombre = cursor.getString(cursor.getColumnIndex("seccion_nombre"))
            val grado_nombre = cursor.getString(cursor.getColumnIndex("grado_nombre"))
            val nivel_nombre = cursor.getString(cursor.getColumnIndex("nivel_nombre"))
            cursos.add(Curso(id, nombre, horario_inicio, horario_fin, dia, aula, seccion_nombre, grado_nombre, nivel_nombre))
        }
        cursor.close()
        return cursos
    }

    fun getIdProfesor(correo: String, contraseña: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
        SELECT profesor.id 
        FROM profesor 
        INNER JOIN usuario ON profesor.id_usuario = usuario.id 
        WHERE usuario.correo = ? AND usuario.contraseña = ?
    """, arrayOf(correo, contraseña))
        var idProfesor = -1
        if (cursor.moveToFirst()) {
            idProfesor = cursor.getInt(cursor.getColumnIndex("id"))
        }
        cursor.close()
        return idProfesor
    }

}

