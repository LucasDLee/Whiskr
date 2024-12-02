package com.example.whiskr_app.ui.catmap

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng

class ServiceNavigationHelper() {
    // To launch GMaps for navigation
    fun launchNavigation(context: Context, destination: LatLng) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            Toast.makeText(context, "Please install Google Maps", Toast.LENGTH_SHORT).show()
        }
    }

}
