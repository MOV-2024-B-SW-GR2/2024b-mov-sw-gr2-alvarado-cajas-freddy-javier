package com.example.gr2sw_deber_ksgt

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class PListView : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaPowers = mutableListOf<Power>()
    private var idSuperhero = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plist_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_list_power)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lvl_view_power)
        val txtPower = findViewById<TextView>(R.id.txt_view_power)
        val btnAnadirPower = findViewById<Button>(R.id.btn_crear_power)

        adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, listaPowers.map { it.name })
        listView.adapter = adapter

        val superhero = intent.getParcelableExtra<SuperHero>("superhero")
        if (superhero != null) {
            idSuperhero = superhero.id
        }
        txtPower.setText("${superhero?.name?.toUpperCase(Locale.ROOT)}'S POWERS")
        registerForContextMenu(listView)
        cargarDatosDesdeBaseDeDatos(idSuperhero)

        btnAnadirPower.setOnClickListener {
            irActividad(CrudPower::class.java)
        }
    }

    var posicionItemSeleccionado = -1 // VARIABLE GLOBAL
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // llenamos opciones del menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.mis_poderes)?.isVisible = false
        menu?.findItem(R.id.route_mapa)?.isVisible = false
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarDatosDesdeBaseDeDatos(idSuperhero) // Refresca la lista
        }
    }

    override fun onContextItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_editar -> {
                val powerSeleccionado = listaPowers[posicionItemSeleccionado]
                irActividad(CrudPower::class.java, powerSeleccionado)
                return true
            }

            R.id.mi_eliminar -> {
                abrirDialogo()
                return true
            }

            R.id.mis_poderes -> {
                return true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.cl_list_power),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    fun abrirDialogo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Desea Eliminar")
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener{ dialog, which ->
                val powerSeleccionado = listaPowers[posicionItemSeleccionado]
                val id = powerSeleccionado.id

                // Llamar al método de eliminación
                val eliminado = BaseDeDatos.tablaSuperheroPower?.eliminarPower(id)
                if (eliminado == true) {
                    mostrarSnackbar("Poder eliminado correctamente.")
                    cargarDatosDesdeBaseDeDatos(idSuperhero) // Refrescar la lista
                } else {
                    mostrarSnackbar("Error al eliminar el poder.")
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

    fun cargarDatosDesdeBaseDeDatos(idSuperhero: Int) {
        val powers = BaseDeDatos.tablaSuperheroPower?.obtenerPowersPorSuperhero(idSuperhero)
        listaPowers.clear()
        if (powers != null) {
            listaPowers.addAll(powers)

        }
        adapter.clear()
        adapter.addAll(listaPowers.map { it.name })
        adapter.notifyDataSetChanged()
    }

    fun irActividad(clase: Class<*>, power: Power? = null) {
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("idSuperhero", idSuperhero)

        if (power != null) {
            intentExplicito.putExtra("modo", "editar")
            intentExplicito.putExtra("power", power)
        } else {
            intentExplicito.putExtra("modo", "crear")
        }

        startActivityForResult(intentExplicito, 1)

    }
}

