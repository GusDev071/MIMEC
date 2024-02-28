package com.secaac.mimec.mapas
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.secaac.mimec.R

class MapaM : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private lateinit var selectedMarker: LatLng
    private var currentPolyline: Polyline? = null
    private lateinit var btnNavigate: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa_m, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btnNavigate = view.findViewById(R.id.btnNavigates)
        btnNavigate.setOnClickListener { startNavigation() }
    }

    private fun startNavigation() {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${selectedMarker.latitude},${selectedMarker.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Habilitar la ubicación del usuario
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Obtener la ubicación actual y centrar el mapa en ella
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            currentLocation = location
            currentLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
            }
        }

        // Cargar el estilo del mapa desde el archivo JSON
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Error al aplicar el estilo del mapa.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "No se pudo encontrar el archivo de estilo del mapa. Error: $e")
        }

        // Agregar marcadores personalizados
        val markers = listOf(
            LatLng(19.311353638430898, -98.87923286518901),
            LatLng(19.3113, -98.87),
            LatLng(19.3231, -98.878990),
            LatLng(19.2375, -98.8772)
        )

        for (coordinate in markers) {
            addCustomMarker(coordinate)
        }

        // Escuchar clics en los marcadores
        map.setOnMarkerClickListener { marker ->
            selectedMarker = marker.position
            getDirections()
            true
        }
    }


    private fun addCustomMarker(latLng: LatLng) {
        val customMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.mecanico)
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(customMarkerIcon)
        )
    }

    private fun getDirections() {
        // Eliminar la polilínea existente, si la hay
        currentPolyline?.remove()
        currentLocation?.let { current ->
            val destination = selectedMarker
            val context = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key)) //
                .build()

            val request: com.google.maps.model.LatLng =
                com.google.maps.model.LatLng(current.latitude, current.longitude)
            val dest: com.google.maps.model.LatLng =
                com.google.maps.model.LatLng(destination.latitude, destination.longitude)

            val directionsResult: DirectionsResult = DirectionsApi.newRequest(context)
                .origin(request)
                .destination(dest)
                .mode(TravelMode.DRIVING)
                .await()

            activity?.runOnUiThread {
                drawRoute(directionsResult)
            }
        }
    }

    private fun drawRoute(result: DirectionsResult) {
        val decodedPath = result.routes[0].overviewPolyline.decodePath()
        val points = ArrayList<LatLng>()
        for (point in decodedPath) {
            points.add(LatLng(point.lat, point.lng))
        }
        val polylineOptions = PolylineOptions().addAll(points).color(R.color.pastel_blue)
        currentPolyline = map.addPolyline(polylineOptions)
    }
}