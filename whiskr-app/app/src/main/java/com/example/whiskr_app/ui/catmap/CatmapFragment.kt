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
import kotlinx.coroutines.tasks.await


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

        // Set the info window adapter
        val locationInfoAdapter = LocationInfoAdapter(requireContext())
        googleMap.setInfoWindowAdapter(locationInfoAdapter)

        googleMap.setOnMarkerClickListener { marker ->
            currentMarker = marker
            marker.showInfoWindow()

            lastPolyline?.remove()
            CoroutineScope(Dispatchers.IO).launch {
                fusedLocationClient.lastLocation.await()?.let { location ->
                    val origin = LatLng(location.latitude, location.longitude)
                    withContext(Dispatchers.Main) {
                        drawRoute(origin, marker.position)
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Current location not available", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
            CoroutineScope(Dispatchers.IO).launch {
                fusedLocationClient.lastLocation.await()?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    withContext(Dispatchers.Main) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to get current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
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
            "Any" -> serviceToQuery.values.joinToString(" AND ")
            else -> serviceToQuery[service] ?: service
        }

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Start a coroutine to perform the background task
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Await the result of the last known location
                    val location = fusedLocationClient.lastLocation.await()

                    location?.let {
                        val currentLocation = LatLng(it.latitude, it.longitude)
                        val searchHelper = ServiceSearchHelper(currentLocation, query = query)

                        // Perform the place search asynchronously
                        searchHelper.findPlaces { places ->
                            // Switch back to the main thread to update UI
                            CoroutineScope(Dispatchers.Main).launch {
                                Log.d("CatmapFragment", "Found ${places.size} places for query: $query")
                                if (places.isEmpty()) {
                                    Toast.makeText(requireContext(), "No places found for $service", Toast.LENGTH_SHORT).show()
                                } else {
                                    updateMapMarkers(places)
                                }
                            }
                        }
                    } ?: run {
                        // Handle case where location is null
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Handle exception if any error occurs during the location fetch or search
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Handle the case when permission is not granted
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateMapMarkers(places: List<SearchResult>) {
        googleMap.clear()

        val boundsBuilder = LatLngBounds.Builder()
        for (place in places) {
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(place.location)
                    .title(place.name)
                    .snippet(place.address)
            )
            boundsBuilder.include(marker!!.position)
        }

        if (places.isNotEmpty()) {
            val bounds = boundsBuilder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

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

