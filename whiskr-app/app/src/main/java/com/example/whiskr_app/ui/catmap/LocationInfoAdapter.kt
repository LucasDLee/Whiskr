package com.example.whiskr_app.ui.catmap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.example.whiskr_app.R

class LocationInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val infoWindow: View = LayoutInflater.from(context).inflate(R.layout.location_info_layout, null)

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        // Reuse the same infoWindow and populate its views.
        val locationName = infoWindow.findViewById<TextView>(R.id.location_name)
        val locationAddress = infoWindow.findViewById<TextView>(R.id.location_address)
        val navigateButton = infoWindow.findViewById<Button>(R.id.button_navigation)

        locationName.text = marker.title
        locationAddress.text = marker.snippet

        // Set click listener for the navigation button in the info window
        navigateButton.visibility = View.VISIBLE
        navigateButton.setOnClickListener {
            val destination = marker.position
            val navigationHelper = ServiceNavigationHelper()
            navigationHelper.launchNavigation(context, destination)
        }

        return infoWindow
    }
}
