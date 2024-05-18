package com.app_repartidor.fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app_repartidor.ApiService
import com.app_repartidor.R
import com.app_repartidor.RouteResponse
import com.app_repartidor.databinding.FragmentNavigationBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.annotation.meta.When


class Navigation : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNavigationBinding
    private lateinit var maps: GoogleMap
    var poly : Polyline? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment


    private var start: String = ""
    private var end: String = ""
    private var coordinates: LatLng? = null
    private var isInitialized = false

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNavigationBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        Places.initialize(activity?.applicationContext, getString(R.string.google_maps_key))
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.searchLocation) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(requireContext(), "Error en el buscador", Toast.LENGTH_SHORT).show()
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

        binding.routeButton.setOnClickListener {
            start = ""
            end = ""
            poly?.remove()
            poly = null
            Toast.makeText(requireContext(), "Pulsa en el mapa tu destino", Toast.LENGTH_LONG).show()
            if(::maps.isInitialized){
                //setear la ubicación ubicación actual como nuestra ubicación de inicio
                enableLocation()
//                val currentCameraPosition = maps.cameraPosition.target
//                val latitude = currentCameraPosition.latitude
//                val longitude = currentCameraPosition.longitude
//                start = "${longitude},${latitude}"
//                Log.d("TAG", start)

                maps.setOnMapClickListener {
                    if(start.isEmpty()){
                        //primero longitud y luego latitud
                        start = "${it.longitude},${it.latitude}"
                    } else if(end.isEmpty()){
                        //primero longitud y luego latitud
                        end = "${it.longitude},${it.latitude}"
                        createRoute()
                    }
                }
            }
        }

        createFragment()

        return binding.root
    }

    private fun createFragment(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        maps = googleMap
//        createMarker()
        enableLocation()
        //createPolylines()
    }

    private fun createPolylines(){
        val polylineOptions = PolylineOptions()
            .add(LatLng(14.995379, -89.574338))
            .add(LatLng(14.996662, -89.572704))
            .width(15f)
            .color(ContextCompat.getColor(requireContext(), R.color.text_color))

        val polyline = maps.addPolyline(polylineOptions)
//        polyline.startCap = RoundCap()
//        polyline.endCap = RoundCap()

        val pattern = listOf(
            Dot(), Gap(10f), Dash(50f), Gap(10f)
        )

        polyline.pattern = pattern
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getRoute("5b3ce3597851110001cf6248b600d3fc35004c1ea0cdecb98d93f0be", start, end)
            if (call.isSuccessful){
                Log.i("MAPS", "OK")

                drawRoute(call.body())
            } else{
                Log.i("MAPS", "ERROR")
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
                .color(ContextCompat.getColor(requireContext(), R.color.navigation_color))
        }
        activity?.runOnUiThread {
            poly = maps.addPolyline(polyLineOptions)
        }

    }

    private fun addMarker(position : LatLng) : Marker {
        // simple marcador
        val marker =  maps.addMarker(
            MarkerOptions()
                .position(position)
                .title("Marker")
        )
        return marker!!
    }

    private fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        maps.animateCamera(newLatLngZoom)
    }

//    private fun createMarker() {
//        val coordinates = LatLng(14.995379, -89.574338)
//        val marker = MarkerOptions().position(coordinates).title("hola estanzuela")
//        maps.addMarker(marker)
//        maps.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
//            2000,
//            null
//        )
//    }
//
//    private fun isLocationPermissionGranted() {
//        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }

    private fun enableLocation(){
        if(!::maps.isInitialized) return
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            maps.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        start = "${longitude},${latitude}"
                        coordinates = LatLng(latitude, longitude)
                        if (isInitialized == false){
                            maps.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(coordinates!!, 18f),
                                2000,
                                null
                            )
                            isInitialized = true
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireContext() as Activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(requireContext(), "Activa la localización en ajustes", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                maps.isMyLocationEnabled = true
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                }
            } else{
                Toast.makeText(requireContext(), "Activa la localización en ajustes", Toast.LENGTH_SHORT).show()
            } else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        if(!::maps.isInitialized) return
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            maps.isMyLocationEnabled = false
        }
    }
}