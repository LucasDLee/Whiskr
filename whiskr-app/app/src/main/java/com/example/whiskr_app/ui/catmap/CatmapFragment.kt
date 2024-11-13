package com.example.whiskr_app.ui.catmap

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatmapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class CatmapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCatmapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatmapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return inflater.inflate(R.layout.fragment_catmap, container, false)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val initialLocation = LatLng(49.266990, -123.025180)
        googleMap.addMarker(MarkerOptions().position(initialLocation))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
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
}
