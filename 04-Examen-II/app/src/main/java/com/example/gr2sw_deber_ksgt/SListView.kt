package com.example.gr2sw_deber_ksgt

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class SListView : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaSuperheroes = mutableListOf<SuperHero>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_slist_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_list_superhero)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lvl_view_superhero)
        val botonAnadirListView = findViewById<Button>(R.id.btn_crear_superhero)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaSuperheroes.map { it.name })
        listView.adapter = adapter

        registerForContextMenu(listView)

        cargarDatosDesdeBaseDeDatos()

        botonAnadirListView.setOnClickListener {
            irActividad(CrudSuperhero::class.java) // Pasa el requestCode 1
        }


    }

    var posicionItemSeleccionado = -1 // VARIABLE GLOBAL
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ){
        super.onCreateContextMenu(menu, v, menuInfo)
        // llenamos opciones del menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        // obtener id
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarDatosDesdeBaseDeDatos() // Refresca la lista
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_editar -> {
                val superHeroSeleccionado = listaSuperheroes[posicionItemSeleccionado]
                irActividad(CrudSuperhero::class.java, superHeroSeleccionado)
                return true
            }
            R.id.mi_eliminar -> {
                abrirDialogo()
                return true
            }
            R.id.mis_poderes -> {
                val superHeroSeleccionado = listaSuperheroes[posicionItemSeleccionado]
                irActividad(PListView::class.java, superHeroSeleccionado)
                return true
            }
            R.id.route_mapa -> {
                val superHeroSeleccionado = listaSuperheroes[posicionItemSeleccionado]
                irActividad(GgoogleMaps::class.java, superHeroSeleccionado)
                return true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    fun mostrarSnackbar(texto: String){
        val snack = Snackbar.make(
            findViewById(R.id.cl_list_superhero),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    fun abrirDialogo(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Desea Eliminar")
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener{ dialog, which ->
                val superHeroSeleccionado = listaSuperheroes[posicionItemSeleccionado]
                val id = superHeroSeleccionado.id

                // Llamar al método de eliminación
                val eliminado = BaseDeDatos.tablaSuperheroPower?.eliminarSuperhero(id)
                if (eliminado == true) {
                    mostrarSnackbar("Superhéroe eliminado correctamente.")
                    cargarDatosDesdeBaseDeDatos() // Refrescar la lista
                } else {
                    mostrarSnackbar("Error al eliminar el superhéroe.")
                }
            }
        )
        builder.setNegativeButton(
            "Cancelar",
            null
        )
        val dialogo = builder.create()
        dialogo.show()
    }

    fun cargarDatosDesdeBaseDeDatos() {
        val superheroes = BaseDeDatos.tablaSuperheroPower?.obtenerTodosLosSuperheroes()
        listaSuperheroes.clear()
        if (superheroes != null) {
            listaSuperheroes.addAll(superheroes)

        }
        adapter.clear()
        adapter.addAll(listaSuperheroes.map { it.name })
        adapter.notifyDataSetChanged()
    }

    fun irActividad(clase: Class<*>, superHero: SuperHero? = null) {
        val intentExplicito = Intent(this, clase)
        if (superHero != null) {
            intentExplicito.putExtra("modo", "editar")
            intentExplicito.putExtra("superhero", superHero)
        } else {
            intentExplicito.putExtra("modo", "crear")
        }
        startActivityForResult(intentExplicito, 1)
    }

}