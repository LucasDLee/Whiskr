package com.example.whiskr_app.ui.catmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatmapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.bottomsheet.BottomSheetDialog
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

    private var userLocationMarker: Marker? = null
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

        setupUI(root)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return root
    }

    private fun setupUI(root: View) {
        serviceSpinner = root.findViewById(R.id.spinner_filter)
        findButton = root.findViewById(R.id.button_find)
        mapView = root.findViewById(R.id.map_view)

        setupServiceSpinner()
        findButton.setOnClickListener {
            val selectedService = serviceSpinner.selectedItem.toString()
            Toast.makeText(requireContext(), "Searching for: $selectedService", Toast.LENGTH_SHORT).show()
            searchNearby(selectedService)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        setupMapListeners()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            startLocationUpdates()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showBottomSheetDialog(marker: Marker) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.location_info_layout, null)

        val locationName = view.findViewById<TextView>(R.id.location_name)
        val locationAddress = view.findViewById<TextView>(R.id.location_address)
        val navigateButton = view.findViewById<Button>(R.id.button_navigation)

        locationName.text = marker.title
        locationAddress.text = marker.snippet

        navigateButton.setOnClickListener {
            val destination = marker.position
            // Use navigationHelper to launch the navigation in Gmaps
            navigationHelper.launchNavigation(requireContext(), destination)
            bottomSheetDialog.dismiss() // Dismiss the bottom sheet after navigation
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun setupMapListeners() {
        googleMap.setOnMarkerClickListener { marker ->
            showBottomSheetDialog(marker)
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    updateUserLocationMarker(LatLng(it.latitude, it.longitude))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun updateUserLocationMarker(latLng: LatLng) {
        val customIcon = getCustomIcon(R.drawable.current_location_icon, 48, 48)

        if (userLocationMarker == null) {
            userLocationMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("You are here")
                    .icon(customIcon)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {
            userLocationMarker?.position = latLng
        }
    }

    private fun setupServiceSpinner() {
        val serviceOptions = resources.getStringArray(R.array.filter_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceSpinner.adapter = adapter
    }

    private fun searchNearby(service: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showToast("Location permission not granted")
            return
        }

        val serviceToQuery = mapOf(
            "Vet Clinic" to "veterinary care",
            "Shelter" to "animal shelter",
            "Pet Store" to "pet store"
        )
        val query = when (service) {
            "Any" -> serviceToQuery.values.joinToString(" AND ")
            else -> serviceToQuery[service] ?: service
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val searchHelper = ServiceSearchHelper(
                        LatLng(location.latitude, location.longitude),
                        query
                    )
                    searchHelper.findPlaces { places ->
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            if (places.isEmpty()) {
                                showToast("No places found for $service")
                            } else {
                                updateMapMarkers(places)
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Unable to get current location")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }


    private fun updateMapMarkers(places: List<SearchResult>) {
        googleMap.clear()
        val boundsBuilder = LatLngBounds.Builder()
        places.forEach { place ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(place.location)
                    .title(place.name)
                    .snippet(place.address)
            )?.let { boundsBuilder.include(it.position) }
        }
        if (places.isNotEmpty()) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
        }
    }

    private fun getCustomIcon(resourceId: Int, width: Int, height: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(requireContext(), resourceId)!!
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
