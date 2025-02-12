package com.example.gr2sw_deber_ksgt

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class GgoogleMaps : AppCompatActivity() {
    private lateinit var mapa: GoogleMap
    var permisos = false
    val nombrePermisoFine = android.Manifest.permission.ACCESS_FINE_LOCATION
    val nombrePermisoCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ggoogle_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val superhero = intent.getParcelableExtra<SuperHero>("superhero")
        if (superhero != null) {
            solicitarPermisos()
            inicializarLogicaMapa(superhero.latitud, superhero.longitud)
        }

    }
    fun tengoPermisos():Boolean{
        val contexto = applicationContext
        val permisoFine = ContextCompat.checkSelfPermission(contexto, nombrePermisoFine)
        val permisoCoarse = ContextCompat.checkSelfPermission(contexto, nombrePermisoCoarse)
        val tienePermisos = permisoFine == PackageManager.PERMISSION_GRANTED &&
                permisoCoarse == PackageManager.PERMISSION_GRANTED
        permisos = tienePermisos
        return permisos
    }
    fun solicitarPermisos(){
        if(!tengoPermisos()){
            ActivityCompat.requestPermissions(
                this, arrayOf(nombrePermisoFine, nombrePermisoCoarse), 1
            )
        }
    }
    fun inicializarLogicaMapa(latitud: Double, longitud: Double){
        val fragmentoMapa = supportFragmentManager.findFragmentById(
            R.id.map ) as SupportMapFragment
        fragmentoMapa.getMapAsync { googleMap ->
            with(googleMap){
                mapa = googleMap
                establecerConfiguracionMapa()
                moverUbicacion(latitud, longitud)
                escucharListeners()
            }
        }
    }
    fun moverUbicacion(latitud: Double, longitud: Double){
        val home = LatLng(latitud, longitud)
        val titulo = "Casa Del Superhero"
        val marcadorQuicentro = anadirMarcador(home, titulo)
        marcadorQuicentro.tag = titulo
        moverCamaraConZoom(home)
    }
    fun escucharListeners(){
        mapa.setOnPolygonClickListener {
            mostrarSnackbar("setOnPolygonClickListener ${it.tag}")
        }
        mapa.setOnPolylineClickListener {
            mostrarSnackbar("setOnPolylineClickListener ${it.tag}")
        }
        mapa.setOnMarkerClickListener {
            mostrarSnackbar("setOnMarkerClickListener ${it.tag}")
            return@setOnMarkerClickListener true
        }
        mapa.setOnCameraIdleListener { mostrarSnackbar("setOnCameraIdleListener") }
        mapa.setOnCameraMoveListener { mostrarSnackbar("setOnCameraMoveListener") }
        mapa.setOnCameraMoveStartedListener { mostrarSnackbar("setOnCameraMoveStartedListener") }
    }
    @SuppressLint("MissingPermission")
    fun establecerConfiguracionMapa(){
        with(mapa){
            if(tengoPermisos()){
                mapa.isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
            uiSettings.isZoomControlsEnabled = true
        }
    }
    fun moverCamaraConZoom(latLang: LatLng, zoom: Float = 17f){
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang,zoom))
    }
    fun anadirMarcador(latLang: LatLng, title: String): Marker {
        return mapa.addMarker(MarkerOptions().position(latLang).title(title))!!
    }
    fun mostrarSnackbar(texto: String){
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }
}