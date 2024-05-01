package com.apprepartidor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class pedidos_disponible : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map : GoogleMap
    private lateinit var btnCalculate : Button
    private lateinit var autocompleteFragment : AutocompleteSupportFragment

    private var start : String = ""
    private var end : String = ""
    var poly : Polyline? = null
    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos_disponible)
        Places.initialize(applicationContext,getString(R.string.google_maps_key))
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_pedido)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(this@pedidos_disponible, "Error en el buscador", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                val add = place.address
                val id = place.id
                val latLng = place.latLng!!
                val marker = addMarker(latLng)
                marker.title = "$add"
                marker.snippet = "$id"
                zoomOnMap(latLng)
            }

        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn1 = findViewById<Button>(R.id.button6)
        val btn2 = findViewById<Button>(R.id.button7)
        val btn3 = findViewById<Button>(R.id.button8)

        btn3.setOnClickListener {
            btn1.visibility = View.VISIBLE
            btn2.visibility = View.VISIBLE
            btn3.visibility = View.INVISIBLE
        }
        
        btnCalculate = findViewById(R.id.btnCalculateRoute)
        btnCalculate.setOnClickListener {
            start = ""
            end = ""
            poly?.remove()
            poly = null
            map.clear()
            Toast.makeText(this, "Selecciona punto de origen y final", Toast.LENGTH_SHORT).show()
            if(::map.isInitialized){
                map.setOnMapClickListener {

                    if(start.isEmpty()){
                        start = "${it.longitude},${it.latitude}"

                    }else if(end.isEmpty()){
                        end = "${it.longitude},${it.latitude}"
                        addMarker(it)
                        createRoute()
                    }
                }
            }
        }
        createFragment()

        val btn = findViewById<ImageView>(R.id.imageView4)
        btn.setOnClickListener {
            val intent = Intent(this, menu::class.java)
            startActivity(intent)
        }

    }
    private fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        map.animateCamera(newLatLngZoom)
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        // createMarker()
        enableLocation()
        map.setOnMyLocationButtonClickListener(this)

        // add marker
        // addMarker(LatLng(13.123,12.123))
        // marcador draggable
        // addDraggableMarker(LatLng(12.456, 14.765))
        //marcador custom
        // addCustomMarker(R.drawable.flag, LatLng(13.999, 12.456))

        map.setOnMapClickListener {
            map.clear()
            addMarker(it)
        }

        map.setOnMapLongClickListener { position ->
            addCustomMarker(R.drawable.flag, position)
        }

        map.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }
    }

    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiServicios::class.java)
                .getRoute("5b3ce3597851110001cf62489797bf016b11410eb2d4beec71206e7f", start, end)
            if(call.isSuccessful){
                drawRoute( call.body())

            }else{
                Log.i("aris", "ok")

            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
        }
        runOnUiThread {
            poly = map.addPolyline(polyLineOptions)
        }

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    private fun addMarker(position : LatLng) : Marker {
        // simple marcador
        val marker =  map.addMarker(
            MarkerOptions()
            .position(position)
            .title("Marker")
        )

        return marker!!
    }

    private fun addDraggableMarker(position: LatLng){
        map.addMarker(
            MarkerOptions()
            .position(position)
            .title("Draggable Marker")
            .draggable(true)
        )
    }

    private fun addCustomMarker(icon : Int, position: LatLng){
        map.addMarker(
            MarkerOptions()
            .position(position)
            .title("Custom Marker")
            .icon(BitmapDescriptorFactory.fromResource(icon))
        )
    }


    private fun createMarker() {
        val coordinates = LatLng(14.790611, -89.776506)
        val marker = MarkerOptions().position(coordinates).title("Cliente")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            5000, null
        )
    }


    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
               REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission", "MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
           REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para Activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()

            }
            else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!:: map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para Activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {

        return false
    }

    override fun onMyLocationClick(p0: Location) {

    }

}