package com.example.a03_deber

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ESqliteHelperArtistaObra(contexto: Context?) : SQLiteOpenHelper(contexto, "artistas_obras", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaArtista =
            """
                CREATE TABLE ARTISTA(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    descripcion VARCHAR(50)
                )
            """.trimIndent()

        val scriptSQLCrearTablaObra =
            """
                CREATE TABLE OBRA(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo VARCHAR(50),
                    descripcion VARCHAR(50),
                    artistaId INTEGER,
                    FOREIGN KEY(artistaId) REFERENCES ARTISTA(id)
                )
            """.trimIndent()

        db?.execSQL(scriptSQLCrearTablaArtista)
        db?.execSQL(scriptSQLCrearTablaObra)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Implementar si es necesario
    }

    // CRUD para Artista
    fun consultarArtistas(): ArrayList<Artista> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM ARTISTA
    """.trimIndent()
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)
        val artistas = ArrayList<Artista>()
        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val artista = Artista(
                    resultadoConsultaLectura.getInt(0),  // 0 = id
                    resultadoConsultaLectura.getString(1),  // 1 = nombre
                    resultadoConsultaLectura.getString(2)  // 2 = descripcion
                )
                artistas.add(artista)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        return artistas
    }

    fun crearArtista(nombre: String, descripcion: String): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("descripcion", descripcion)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "ARTISTA", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarArtista(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "ARTISTA", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parametros
            )
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarArtista(nombre: String, descripcion: String, id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAAactualizar = ContentValues()
        valoresAAactualizar.put("nombre", nombre)
        valoresAAactualizar.put("descripcion", descripcion)
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "ARTISTA", // tabla
                valoresAAactualizar, // valores
                "id=?", // id=1
                parametrosConsultaActualizar // [1]
            )
        baseDatosEscritura.close()
        return resultadoActualizar != -1
    }

    fun consultarArtistaPorId(id: Int): Artista? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM ARTISTA WHERE ID = ?
    """.trimIndent()
        val parametrosConsultaLectura = arrayOf(id.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(
                scriptConsultaLectura,
                parametrosConsultaLectura
            )
        val existeAlMenosUno = resultadoConsultaLectura.moveToFirst()
        return if (existeAlMenosUno) {
            val artista = Artista(
                resultadoConsultaLectura.getInt(0),  // 0 = id
                resultadoConsultaLectura.getString(1),  // 1 = nombre
                resultadoConsultaLectura.getString(2)  // 2 = descripcion
            )
            resultadoConsultaLectura.close()
            artista
        } else {
            resultadoConsultaLectura.close()
            null
        }
    }

    // CRUD para Obra
    fun consultarObras(): List<Obra> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM OBRA
    """.trimIndent()
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)
        val obras = mutableListOf<Obra>()
        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val obra = Obra(
                    resultadoConsultaLectura.getInt(0),  // 0 = id
                    resultadoConsultaLectura.getString(1),  // 1 = titulo
                    resultadoConsultaLectura.getString(2),  // 2 = descripcion
                    resultadoConsultaLectura.getInt(3)  // 3 = artistaId
                )
                obras.add(obra)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        return obras
    }
    fun crearObra(titulo: String, descripcion: String, artistaId: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("titulo", titulo)
        valoresGuardar.put("descripcion", descripcion)
        valoresGuardar.put("artistaId", artistaId)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "OBRA", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarObra(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "OBRA", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parametros
            )
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarObra(titulo: String, descripcion: String, id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAAactualizar = ContentValues()
        valoresAAactualizar.put("titulo", titulo)
        valoresAAactualizar.put("descripcion", descripcion)
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "OBRA", // tabla
                valoresAAactualizar, // valores
                "id=?", // id=1
                parametrosConsultaActualizar // [1]
            )
        baseDatosEscritura.close()
        return resultadoActualizar != -1
    }

    fun consultarObraPorId(id: Int): Obra? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM OBRA WHERE ID = ?
    """.trimIndent()
        val parametrosConsultaLectura = arrayOf(id.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(
                scriptConsultaLectura,
                parametrosConsultaLectura
            )
        val existeAlMenosUno = resultadoConsultaLectura.moveToFirst()
        return if (existeAlMenosUno) {
            val obra = Obra(
                resultadoConsultaLectura.getInt(0),  // 0 = id
                resultadoConsultaLectura.getString(1),  // 1 = titulo
                resultadoConsultaLectura.getString(2),  // 2 = descripcion
                resultadoConsultaLectura.getInt(3)  // 3 = artistaId
            )
            resultadoConsultaLectura.close()
            obra
        } else {
            resultadoConsultaLectura.close()
            null
        }
    }

    fun consultarObrasPorArtista(artistaId: Int): List<Obra> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM OBRA WHERE artistaId = ?
    """.trimIndent()
        val parametrosConsultaLectura = arrayOf(artistaId.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(
                scriptConsultaLectura,
                parametrosConsultaLectura
            )
        val obras = mutableListOf<Obra>()
        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val obra = Obra(
                    resultadoConsultaLectura.getInt(0),  // 0 = id
                    resultadoConsultaLectura.getString(1),  // 1 = titulo
                    resultadoConsultaLectura.getString(2),  // 2 = descripcion
                    resultadoConsultaLectura.getInt(3)  // 3 = artistaId
                )
                obras.add(obra)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        return obras
    }
}