package com.example.whiskr_app.ui.catmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatmapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CatmapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCatmapBinding? = null
    private val binding get() = _binding!!

    private lateinit var serviceSpinner: Spinner
    private lateinit var findButton: Button
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentMarker: Marker? = null
    private var lastPolyline: Polyline? = null
    private val navigationHelper: ServiceNavigationHelper by lazy {
        ServiceNavigationHelper()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatmapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        serviceSpinner = root.findViewById(R.id.spinner_filter)
        findButton = root.findViewById(R.id.button_find)

        setupServiceSpinner()

        findButton.setOnClickListener {
            val selectedService = serviceSpinner.selectedItem.toString()
            Toast.makeText(requireContext(), "Searching for: $selectedService", Toast.LENGTH_SHORT).show()
            searchNearby(selectedService)
        }
        mapView = root.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()

        // Set the custom info window adapter
        val locationInfoAdapter = LocationInfoAdapter(requireContext())
        googleMap.setInfoWindowAdapter(locationInfoAdapter)


        googleMap.setOnMarkerClickListener { marker ->
            currentMarker = marker
            marker.showInfoWindow()

            lastPolyline?.remove()
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    drawRoute(LatLng(location.latitude, location.longitude), marker.position)
                } else {
                    Toast.makeText(requireContext(), "Current location not available", Toast.LENGTH_SHORT).show()
                }
            }

            // Memory logging for allocation error debugging
            val usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)
            Log.d("Memory", "Memory used: ${usedMemory}MB")

            true
        }

    }


    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val userLocation = LatLng(it.latitude, it.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupServiceSpinner() {
        val serviceOptions = resources.getStringArray(R.array.filter_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceSpinner.adapter = adapter
    }

    private fun searchNearby(service: String) {
        val serviceToQuery = mapOf(
            "Vet Clinic" to "veterinary care",
            "Shelter" to "animal shelter",
            "Pet Store" to "pet store"
        )
        val query = when (service) {
            "Any" -> serviceToQuery.values.joinToString(" AND ") // Combine all services with AND for the "Any" option
            else -> serviceToQuery[service] ?: service // Use the specific query for selected service
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    val searchHelper = ServiceSearchHelper(currentLocation, query = query)
                    searchHelper.findPlaces { places ->
                        requireActivity().runOnUiThread {
                            Log.d("CatmapFragment", "Found ${places.size} places for query: $query")
                            if (places.isEmpty()) {
                                Toast.makeText(requireContext(), "No places found for $service", Toast.LENGTH_SHORT).show()
                            } else {
                                updateMapMarkers(places)
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMapMarkers(places: List<SearchResult>) {
        // Clear previous markers
        googleMap.clear()

        val boundsBuilder = LatLngBounds.Builder()

        for (place in places) {
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(place.location)
                    .title(place.name)
                    .snippet(place.address)
            )
            boundsBuilder.include(marker!!.position) // Include each marker in bounds
        }

        // Move the camera to show all markers
        if (places.isNotEmpty()) {
            val bounds = boundsBuilder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    // Possible cause of allocation error...
    private fun drawRoute(origin: LatLng, destination: LatLng) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val routePoints = navigationHelper.getDirections(origin, destination)
                withContext(Dispatchers.Main) {
                    lastPolyline?.remove()
                    if (routePoints.isNotEmpty()) {
                        val polylineOptions = navigationHelper.createRoutePolyline(routePoints)
                        lastPolyline = googleMap.addPolyline(polylineOptions)
                    } else {
                        Toast.makeText(requireContext(), "Unable to fetch route", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error fetching route: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}

