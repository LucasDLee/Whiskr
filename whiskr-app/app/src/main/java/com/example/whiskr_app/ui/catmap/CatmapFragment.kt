package com.example.whiskr_app.ui.catmap

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatmapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale


class CatmapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCatmapBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatmapBinding.inflate(inflater, container, false)

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        // Initialize MapView
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.buttonFind.setOnClickListener {
            val city = binding.inputCity.text.toString().trim()
            if (city.isNotEmpty()) {
                searchCity(city)
            } else {
                Toast.makeText(requireContext(), "Please enter a city", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set a marker and move the camera
        val initialLocation = LatLng(49.266990, -123.025180)
        googleMap.addMarker(MarkerOptions().position(initialLocation).title("Initial Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
    }

    private fun searchCity(city: String) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(city, 1)
            val address = addresses?.firstOrNull()
            val cityLocation = address?.let { LatLng(it.latitude, it.longitude) }

            if (cityLocation != null) {
                // Move the map to the city
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 12f))

                // Search for veterinary services near the city
                searchVeterinaryServices(cityLocation)
            } else {
                Toast.makeText(requireContext(), "City not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error finding city: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchVeterinaryServices(location: LatLng) {
        val nearbySearchRequest = FindCurrentPlaceRequest.newInstance(
            listOf(Place.Field.NAME, Place.Field.LAT_LNG)
        )

        val query = "veterinary services near ${location.latitude},${location.longitude}"
        val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$query&key=${getString(R.string.google_maps_key)}"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = URL(url).readText()
                val json = JSONObject(response)
                val results = json.getJSONArray("results")

                withContext(Dispatchers.Main) {
                    googleMap.clear()
                    for (i in 0 until results.length()) {
                        val result = results.getJSONObject(i)
                        val name = result.getString("name")
                        val locationJson = result.getJSONObject("geometry").getJSONObject("location")
                        val lat = locationJson.getDouble("lat")
                        val lng = locationJson.getDouble("lng")

                        val markerLocation = LatLng(lat, lng)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(markerLocation)
                                .title(name)
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}

