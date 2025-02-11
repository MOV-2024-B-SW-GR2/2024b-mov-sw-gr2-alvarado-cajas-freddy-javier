package com.example.a03_deber

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var adaptador: ArrayAdapter<String>
    private lateinit var listView: ListView
    private var posicionItemSeleccionado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar la base de datos
        BaseDeDatos.tablaArtistaObra = ESqliteHelperArtistaObra(this)

        // Cargar datos iniciales
        val arreglo = BaseDeDatos.tablaArtistaObra?.consultarArtistas()
        val nombresArtistas = arreglo?.map { it.nombre } ?: emptyList()
        listView = findViewById(R.id.lv_list_artista)

        // Configurar el adaptador del ListView
        adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            nombresArtistas.toMutableList()
        )
        listView.adapter = adaptador

        // Registrar el ListView para el menú de contexto
        registerForContextMenu(listView)

        // Botón para añadir artista
        val botonAñadir = findViewById<Button>(R.id.btn_crear_artista)
        botonAñadir.setOnClickListener {
            mostrarDialogoCrearArtista()
        }
    }

    // Función para mostrar el cuadro de diálogo emergente
    private fun mostrarDialogoCrearArtista() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear Artista")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputNombre = EditText(this)
        inputNombre.hint = "Nombre del artista"
        layout.addView(inputNombre)

        val inputDescripcion = EditText(this)
        inputDescripcion.hint = "Descripción del artista"
        layout.addView(inputDescripcion)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nombre = inputNombre.text.toString()
            val descripcion = inputDescripcion.text.toString()

            if (nombre.isNotBlank() && descripcion.isNotBlank()) {
                if (BaseDeDatos.tablaArtistaObra?.crearArtista(nombre, descripcion) == true) {
                    mostrarSnackbar("Artista agregado correctamente")
                    actualizarLista()
                } else {
                    mostrarSnackbar("Error al agregar artista")
                }
            } else {
                mostrarSnackbar("Los campos no pueden estar vacíos")
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // Función para actualizar el ListView
    private fun actualizarLista() {
        val nuevosDatos = BaseDeDatos.tablaArtistaObra?.consultarArtistas()?.map { it.nombre } ?: emptyList()
        adaptador.clear()
        adaptador.addAll(nuevosDatos)
        adaptador.notifyDataSetChanged()
    }

    // Función para mostrar Snackbar
    private fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_SHORT
        )
        snack.show()
    }

    // Menú de contexto para editar y eliminar
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        posicionItemSeleccionado = info.position
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_editar_artista -> {
                val artista = BaseDeDatos.tablaArtistaObra?.consultarArtistaPorId(posicionItemSeleccionado+1) // +1 porque el ID empieza en 1
                artista?.let {
                    mostrarDialogoEditarArtista(it)
                }
                true
            }
            R.id.menu_eliminar_artista -> {
                mostrarDialogoEliminarArtista()
                true
            }
            R.id.menu_ver_artista -> {
                startActivity(Intent(this, ObraView::class.java))
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun mostrarDialogoEditarArtista(artista: Artista) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Artista")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputNombre = EditText(this)
        inputNombre.setText(artista.nombre)
        layout.addView(inputNombre)

        val inputDescripcion = EditText(this)
        inputDescripcion.setText(artista.descripcion)
        layout.addView(inputDescripcion)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoNombre = inputNombre.text.toString()
            val nuevaDescripcion = inputDescripcion.text.toString()

            if (nuevoNombre.isNotBlank() && nuevaDescripcion.isNotBlank()) {
                if (BaseDeDatos.tablaArtistaObra?.actualizarArtista(nuevoNombre, nuevaDescripcion, artista.id) == true) {
                    mostrarSnackbar("Artista actualizado correctamente")
                    actualizarLista()
                } else {
                    mostrarSnackbar("Error al actualizar artista")
                }
            } else {
                mostrarSnackbar("Los campos no pueden estar vacíos")
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun mostrarDialogoEliminarArtista() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Deseas eliminar al artista?")

        builder.setPositiveButton("Eliminar") { _, _ ->
            val artista = BaseDeDatos.tablaArtistaObra?.consultarArtistaPorId(posicionItemSeleccionado + 1) // +1 porque el ID empieza en 1
            artista?.let {
                if (BaseDeDatos.tablaArtistaObra?.eliminarArtista(it.id) == true) {
                    mostrarSnackbar("Artista eliminado correctamente")
                    actualizarLista()
                } else {
                    mostrarSnackbar("Error al eliminar artista")
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}
