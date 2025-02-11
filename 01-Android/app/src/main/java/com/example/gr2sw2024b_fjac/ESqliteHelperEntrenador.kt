package com.example.gr2sw2024b_fjac

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ESqliteHelperEntrenador(contexto: Context?) : SQLiteOpenHelper(contexto, "moviles", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaEntrenador =
            """
                CREATE TABLE ENTRENADOR(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    descripcion VARCHAR(50)
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaEntrenador)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        // Implementar si es necesario
    }

    fun crearEntrenador(nombre: String, descripcion: String): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("descripcion", descripcion)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "ENTRENADOR", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarEntrenador(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "ENTRENADOR", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parametros
            )
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarEntrenador(nombre: String, descripcion: String, id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAAactualizar = ContentValues()
        valoresAAactualizar.put("nombre", nombre)
        valoresAAactualizar.put("descripcion", descripcion)
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "ENTRENADOR", // tabla
                valoresAAactualizar, // valores
                "id=?", // id=1
                parametrosConsultaActualizar // [1]
            )
        baseDatosEscritura.close()
        return resultadoActualizar != -1
    }

    fun consultarEntrenadorPorId(id: Int): BEntrenador? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM ENTRENADOR WHERE ID = ?
    """.trimIndent()
        val parametrosConsultaLectura = arrayOf(id.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(
                scriptConsultaLectura,
                parametrosConsultaLectura
            )
        val existeAlMenosUno = resultadoConsultaLectura.moveToFirst()
        return if (existeAlMenosUno) {
            val entrenador = BEntrenador(
                resultadoConsultaLectura.getInt(0),  // 0 = id
                resultadoConsultaLectura.getString(1),  // 1 = nombre
                resultadoConsultaLectura.getString(2)  // 2 = descripcion
            )
            resultadoConsultaLectura.close()
            entrenador
        } else {
            resultadoConsultaLectura.close()
            null
        }
    }
}