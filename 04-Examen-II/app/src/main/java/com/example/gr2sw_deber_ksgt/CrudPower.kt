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

class CrudPower : AppCompatActivity() {
    fun mostrarSnackbar(texto: String){
        val snack = Snackbar.make(
            findViewById(R.id.cl_crud_power),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_obras)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_crud_power)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val modo = intent.getStringExtra("modo") ?: "crear"
        val power: Power? = intent.getParcelableExtra("power")
        val idSuperhero = intent.getIntExtra("idSuperhero", -1)

        if (idSuperhero == -1) {
            mostrarSnackbar("Error: No se proporcionó un ID válido del superhéroe.")
            finish()
            return
        }

        val botonGuardarPowerBDD = findViewById<Button>(R.id.btn_guardar_power)
        val inputNombre = findViewById<EditText>(R.id.input_name_power)
        val inputDescription = findViewById<EditText>(R.id.input_description)
        val switchOffensive = findViewById<Switch>(R.id.switch_offensive)
        val inputEffectiveness = findViewById<EditText>(R.id.input_effectiveness)

        if (modo == "editar" && power != null) {
            // Configurar campos con los datos del superhéroe
            inputNombre.setText(power.name)
            inputDescription.setText(power.description)
            switchOffensive.isChecked = power.isOffensive
            inputEffectiveness.setText(power.effectiveness.toString())
            botonGuardarPowerBDD.text = "Actualizar"
        } else {
            botonGuardarPowerBDD.text = "Crear"
        }

        botonGuardarPowerBDD.setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val description = inputDescription.text.toString().trim()
            val isOffensive = switchOffensive.isChecked
            val effectivenessString = inputEffectiveness.text.toString().trim()

            if (nombre.isEmpty() || description.isEmpty() || effectivenessString.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos")
                return@setOnClickListener
            }

            // Intentar convertir popularidad a número
            val effectiveness = effectivenessString.toDoubleOrNull()
            if (effectiveness == null || effectiveness < 0) {
                mostrarSnackbar("Por favor, ingresa un valor válido para la efectividad.")
                return@setOnClickListener
            }

            if (modo == "crear") {
                val respuesta = BaseDeDatos.tablaSuperheroPower?.crearPower(
                    nombre,
                    description,
                    isOffensive,
                    effectiveness,
                    idSuperhero
                )

                if (respuesta == true) {
                    mostrarSnackbar("Poder creado")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al crear el poder")
                }
            } else if (modo == "editar" && power != null) {
                val respuesta = BaseDeDatos.tablaSuperheroPower?.actualizarPower(
                    nombre,
                    description,
                    isOffensive,
                    effectiveness,
                    idSuperhero,
                    power.id
                )

                if (respuesta == true) {
                    mostrarSnackbar("Poder actualizado correctamente")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarSnackbar("Error al actualizar el poder")
                }
            }
        }

    }
}