package com.example.a03_deber

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class ObraView : AppCompatActivity() {

    private lateinit var adaptador: ArrayAdapter<String>
    private lateinit var listView: ListView
    private var posicionItemSeleccionado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_obra_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Cargar obras
        val obras = BaseDeDatos.tablaArtistaObra?.consultarObras() // Método que necesitarás crear
        val titulosObras = obras?.map { it.titulo } ?: emptyList()

        // Configurar el adaptador del ListView
        listView = findViewById(R.id.lv_list_obras)
        adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            titulosObras.toMutableList()
        )
        listView.adapter = adaptador

        // Registrar el ListView para el menú de contexto
        registerForContextMenu(listView)

        // Botón para añadir obra
        val botonAñadir = findViewById<Button>(R.id.btn_crear_obra)
        botonAñadir.setOnClickListener {
            mostrarDialogoCrearObra()
        }
    }

    // Función para mostrar el cuadro de diálogo emergente
    private fun mostrarDialogoCrearObra() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear Obra")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputTitulo = EditText(this)
        inputTitulo.hint = "Título de la obra"
        layout.addView(inputTitulo)

        val inputDescripcion = EditText(this)
        inputDescripcion.hint = "Descripción de la obra"
        layout.addView(inputDescripcion)

        // TextView para mostrar el artista seleccionado
        val txtArtista = TextView(this)
        txtArtista.text = "Artista: ${getArtistaSeleccionado()}"
        layout.addView(txtArtista)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val titulo = inputTitulo.text.toString()
            val descripcion = inputDescripcion.text.toString()

            if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                if (BaseDeDatos.tablaArtistaObra?.crearObra(titulo, descripcion, getArtistaSeleccionado().id) == true) {
                    mostrarSnackbar("Obra agregada correctamente")
                    actualizarListaObras()
                } else {
                    mostrarSnackbar("Error al agregar obra")
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

    // Función para obtener el artista seleccionado
    private fun getArtistaSeleccionado(): Artista {
        // Asegúrate de que aquí devuelves el artista correcto que se haya seleccionado
        return Artista(1, "Artista Ejemplo", "Descripción del artista")
    }

    // Función para actualizar el ListView
    private fun actualizarListaObras() {
        val nuevasObras = BaseDeDatos.tablaArtistaObra?.consultarObras()?.map { it.titulo } ?: emptyList()
        adaptador.clear()
        adaptador.addAll(nuevasObras)
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
                val obra = BaseDeDatos.tablaArtistaObra?.consultarObraPorId(posicionItemSeleccionado + 1) // +1 porque el ID empieza en 1
                obra?.let {
                    mostrarDialogoEditarObra(it)
                }
                true
            }
            R.id.menu_eliminar_artista -> {
                mostrarDialogoEliminarObra()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun mostrarDialogoEditarObra(obra: Obra) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Obra")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputTitulo = EditText(this)
        inputTitulo.setText(obra.titulo)
        layout.addView(inputTitulo)

        val inputDescripcion = EditText(this)
        inputDescripcion.setText(obra.descripcion)
        layout.addView(inputDescripcion)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoTitulo = inputTitulo.text.toString()
            val nuevaDescripcion = inputDescripcion.text.toString()

            if (nuevoTitulo.isNotBlank() && nuevaDescripcion.isNotBlank()) {
                if (BaseDeDatos.tablaArtistaObra?.actualizarObra(nuevoTitulo, nuevaDescripcion, obra.id) == true) {
                    mostrarSnackbar("Obra actualizada correctamente")
                    actualizarListaObras()
                } else {
                    mostrarSnackbar("Error al actualizar obra")
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

    private fun mostrarDialogoEliminarObra() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Deseas eliminar la obra?")

        builder.setPositiveButton("Eliminar") { _, _ ->
            val obra = BaseDeDatos.tablaArtistaObra?.consultarObraPorId(posicionItemSeleccionado + 1)
            obra?.let {
                if (BaseDeDatos.tablaArtistaObra?.eliminarObra(it.id) == true) {
                    mostrarSnackbar("Obra eliminada correctamente")
                    actualizarListaObras()
                } else {
                    mostrarSnackbar("Error al eliminar obra")
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}