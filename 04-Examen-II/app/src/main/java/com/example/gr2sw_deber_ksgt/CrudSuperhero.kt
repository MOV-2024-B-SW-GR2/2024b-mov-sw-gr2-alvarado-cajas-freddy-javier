package com.example.gr2sw_deber_ksgt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeParseException

class CrudSuperhero : AppCompatActivity() {
    fun mostrarSnackbar(texto: String){
        val snack = Snackbar.make(
            findViewById(R.id.cl_crud_superhero),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_artista)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_crud_superhero)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val modo = intent.getStringExtra("modo") ?: "crear"
        val superHero: SuperHero? = intent.getParcelableExtra("superhero")

        val botonGuardarSuperheroBDD = findViewById<Button>(R.id.btn_guardar_superhero)
        val inputNombre = findViewById<EditText>(R.id.input_name_superhero)
        val switchActivo = findViewById<Switch>(R.id.switch_active)
        val inputFecha = findViewById<EditText>(R.id.input_date)
        val inputPopularidad = findViewById<EditText>(R.id.input_popularity)
        val inputLatitude = findViewById<EditText>(R.id.txt_latitude)
        val inputLongitude = findViewById<EditText>(R.id.txt_longitude)

        if (modo == "editar" && superHero != null) {
            // Configurar campos con los datos del superhéroe
            inputNombre.setText(superHero.name)
            switchActivo.isChecked = superHero.isActive
            inputFecha.setText(superHero.debutDate.toString())
            inputPopularidad.setText(superHero.popularity.toString())
            inputLatitude.setText(superHero.latitude.toString())
            inputLongitude.setText(superHero.longitude.toString())
            botonGuardarSuperheroBDD.text = "Actualizar"
        } else {
            botonGuardarSuperheroBDD.text = "Crear"
        }
        
        botonGuardarSuperheroBDD.setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val isActive = switchActivo.isChecked
            val debutDateString = inputFecha.text.toString().trim()
            val popularityString = inputPopularidad.text.toString().trim()
            val latitudeString = inputLatitude.text.toString().trim()
            val longitudeString = inputLongitude.text.toString().trim()

            // Validar entradas
            if (nombre.isEmpty() || debutDateString.isEmpty() || popularityString.isEmpty()) {
                mostrarSnackbar("Por favor, completa todos los campos.")
                return@setOnClickListener
            }

            // Convertir fecha a LocalDate
            val debutDate: LocalDate = try {
                LocalDate.parse(debutDateString)
            } catch (e: DateTimeParseException) {
                mostrarSnackbar("Por favor, ingresa una fecha válida en el formato AAAA-MM-DD.")
                return@setOnClickListener
            }

            // Intentar convertir popularidad a número
            val popularity = popularityString.toDoubleOrNull()
            val latitude = latitudeString.toDoubleOrNull()
            val longitude = longitudeString.toDoubleOrNull()
            if (popularity == null || popularity < 0) {
                mostrarSnackbar("Por favor, ingresa un valor válido para la popularidad.")
                return@setOnClickListener
            }
            if(latitude == null || longitude == null){
                mostrarSnackbar("Por favor, ingresa valores válidos para la latitud y longitud.")
                return@setOnClickListener
            }

            if (modo == "crear") {
                val respuesta = BaseDeDatos.tablaSuperheroPower?.crearSuperhero(
                    nombre,
                    isActive,
                    debutDate,
                    popularity,
                    latitude,
                    longitude
                )

                if (respuesta == true) {
                    mostrarSnackbar("Superhéroe creado correctamente.")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al crear el superhéroe.")
                }
            } else if (modo == "editar" && superHero != null) {
                val respuesta = BaseDeDatos.tablaSuperheroPower?.actualizarSuperhero(
                    nombre,
                    isActive,
                    debutDate,
                    popularity,
                    superHero.id,
                    latitude,
                    longitude,
                )

                if (respuesta == true) {
                    mostrarSnackbar("Superhéroe actualizado correctamente.")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al actualizar el superhéroe.")
                }
            }
        }
    }
}