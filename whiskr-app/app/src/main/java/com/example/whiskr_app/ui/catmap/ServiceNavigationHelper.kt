package com.example.whiskr_app.ui.catmap

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ServiceNavigationHelper(private val apiKey: String = "TODO") {

    // Function to fetch directions between origin and destination
    suspend fun getDirections(origin: LatLng, destination: LatLng): List<LatLng> {
        val originString = "${origin.latitude},${origin.longitude}"
        val destinationString = "${destination.latitude},${destination.longitude}"
        val directionsUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=$originString&destination=$destinationString&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(directionsUrl).build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        parseDirectionsResponse(responseBody)
                    } ?: emptyList()
                } else {
                    Log.e("NavigationHelper", "Failed to fetch directions: ${response.message}")
                    emptyList()
                }
            } catch (e: IOException) {
                Log.e("NavigationHelper", "Error fetching directions: ${e.message}")
                emptyList()
            }
        }
    }

    // Function to parse the directions response and extract the route points
    private fun parseDirectionsResponse(response: String): List<LatLng> {
        val routePoints = mutableListOf<LatLng>()
        try {
            val jsonObject = JSONObject(response)
            val routes = jsonObject.optJSONArray("routes")
            if (routes != null && routes.length() > 0) {
                val legs = routes.getJSONObject(0).optJSONArray("legs")
                val steps = legs?.getJSONObject(0)?.optJSONArray("steps")
                steps?.let {
                    for (i in 0 until it.length()) {
                        val step = it.getJSONObject(i)
                        val startLocation = step.optJSONObject("start_location")
                        val lat = startLocation?.optDouble("lat") ?: continue
                        val lng = startLocation.optDouble("lng")
                        routePoints.add(LatLng(lat, lng))
                    }
                }
            }
        } catch (e: JSONException) {
            Log.e("NavigationHelper", "Error parsing directions response: ${e.message}")
        }
        return routePoints
    }

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
