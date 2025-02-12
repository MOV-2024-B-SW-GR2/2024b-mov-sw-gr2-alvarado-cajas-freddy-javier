package com.example.gr2sw_deber_ksgt

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SqliteHelper(
    context: Context?
): SQLiteOpenHelper(
    context,
    "moviles",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys = ON;")
        val scriptSQLCrearTablaSuperhero =
            """
                CREATE TABLE SUPERHERO(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    isActive BOOLEAN,
                    debutDate TEXT,
                    popularity DECIMAL(3, 1),
                    latitude REAL,
                    longitude REAL
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaSuperhero)

        val scriptSQLCrearTablaPower =
            """
                CREATE TABLE POWER(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name VARCHAR(50),
                    description VARCHAR(50),
                    isOffensive BOOLEAN,
                    effectiveness DECIMAL(3, 1),
                    idSuperhero INTEGER,
                    FOREIGN KEY(idSuperhero) REFERENCES SUPERHERO(id) ON DELETE CASCADE
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaPower)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    //Superhero
    fun obtenerTodosLosSuperheroes(): List<SuperHero> {
        val listaSuperheroes = mutableListOf<SuperHero>()
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM SUPERHERO"
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val id = resultadoConsultaLectura.getInt(0)
                val nombre = resultadoConsultaLectura.getString(1)
                val isActive = resultadoConsultaLectura.getInt(2) == 1
                val debutDateString = resultadoConsultaLectura.getString(3)
                val popularity = resultadoConsultaLectura.getDouble(4)
                val latitude = resultadoConsultaLectura.getDouble(5)
                val longitude = resultadoConsultaLectura.getDouble(6)

                val debutDate = LocalDate.parse(debutDateString)

                val superhero = SuperHero(
                    id,
                    nombre,
                    isActive,
                    debutDate,
                    popularity,
                    latitude,
                    longitude
                )
                listaSuperheroes.add(superhero)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return listaSuperheroes
    }


    fun crearSuperhero(
        nombre: String,
        isActive: Boolean,
        debutDate: LocalDate,
        popularity: Double,
        latitude: Double,
        longitude: Double
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("isActive", isActive)
        valoresGuardar.put("debutDate", debutDate.format(DateTimeFormatter.ISO_DATE))
        valoresGuardar.put("popularity", popularity)
        valoresGuardar.put("latitude", latitude)
        valoresGuardar.put("longitude", longitude)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "SUPERHERO", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }

    fun eliminarSuperhero(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase

        val parametrosConsultaDeletePowers = arrayOf(id.toString())
        baseDatosEscritura.delete("POWER", "idSuperhero=?", parametrosConsultaDeletePowers)

        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "SUPERHERO", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parametros
            )

        baseDatosEscritura.close()
        return if (resultadoEliminar.toInt() == -1) false else true
    }

    fun actualizarSuperhero(
        nombre: String, isActive: Boolean,
        debutDate: LocalDate, popularity: Double,
        id: Int, latitude: Double, longitude: Double
    ): Boolean {

        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("nombre", nombre)
        valoresAActualizar.put("isActive", isActive)
        valoresAActualizar.put("debutDate", debutDate.format(DateTimeFormatter.ISO_DATE))
        valoresAActualizar.put("popularity", popularity)
        valoresAActualizar.put("latitude", latitude)
        valoresAActualizar.put("longitude", longitude)
        val parametrosConsultaUpdate = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "SUPERHERO",
                valoresAActualizar,
                "id=?",
                parametrosConsultaUpdate
            )
        baseDatosEscritura.close()
        return if (resultadoActualizar == -1) false else true
    }

    //Power
    fun obtenerPowersPorSuperhero(idSuperhero: Int): List<Power> {
        val listaPowers = mutableListOf<Power>()
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM POWER WHERE idSuperhero = ?"
        val parametrosConsultaLectura = arrayOf(idSuperhero.toString())
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, parametrosConsultaLectura)

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val id = resultadoConsultaLectura.getInt(0)
                val name = resultadoConsultaLectura.getString(1)
                val description = resultadoConsultaLectura.getString(2)
                val isOffensive = resultadoConsultaLectura.getInt(3) == 1
                val effectiveness = resultadoConsultaLectura.getDouble(4)

                val power = Power(
                    id,
                    name,
                    description,
                    isOffensive,
                    effectiveness
                )
                listaPowers.add(power)
            } while (resultadoConsultaLectura.moveToNext())
        }
        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return listaPowers
    }

    fun crearPower(
        name: String,
        description: String,
        isOffensive: Boolean,
        effectiveness: Double,
        idSuperhero: Int
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("name", name)
        valoresGuardar.put("description", description)
        valoresGuardar.put("isOffensive", isOffensive)
        valoresGuardar.put("effectiveness", effectiveness)
        valoresGuardar.put("idSuperhero", idSuperhero)

        println("Datos a guardar en POWER: $valoresGuardar")

        val resultadoGuardar = baseDatosEscritura
            .insert(
                "POWER", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }

    fun eliminarPower(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "POWER", // tabla
                "id=?", // condición
                parametrosConsultaDelete // parámetros
            )
        baseDatosEscritura.close()
        return if (resultadoEliminar.toInt() == -1) false else true
    }

    fun actualizarPower(
        name: String,
        description: String,
        isOffensive: Boolean,
        effectiveness: Double,
        idSuperhero: Int,
        id: Int
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("name", name)
        valoresAActualizar.put("description", description)
        valoresAActualizar.put("isOffensive", isOffensive)
        valoresAActualizar.put("effectiveness", effectiveness)
        valoresAActualizar.put("idSuperhero", idSuperhero)
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "POWER", // tabla
                valoresAActualizar, // valores
                "id=?", // condición
                parametrosConsultaActualizar // parámetros
            )
        baseDatosEscritura.close()
        return if (resultadoActualizar == -1) false else true
    }
}