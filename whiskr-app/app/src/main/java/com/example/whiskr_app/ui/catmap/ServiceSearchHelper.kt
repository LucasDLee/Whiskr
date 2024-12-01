package com.example.whiskr_app.ui.catmap

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class ServiceSearchHelper(
    private val location: LatLng,
    private val query: String? = null,
) {

    fun findPlaces(callback: (List<SearchResult>) -> Unit) {
        val apiKey = "AIzaSyBZ0DT2nirbxUwuK-nemjXtsUAwnPZE_Sk"
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$encodedQuery&location=${location.latitude},${location.longitude}&radius=5000&key=$apiKey"

        Log.d("ServiceSearchHelper", "Requesting URL: $url")

        val client = OkHttpClient() // Initialize OkHttp client
        val request = Request.Builder() // Build the HTTP request
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ServiceSearchHelper", "Error fetching places: ${e.message}")
                callback(emptyList()) // Return an empty list on error
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val results = parseSearchResults(responseBody)
                        callback(results) // Return the parsed results
                    } ?: callback(emptyList()) // Return empty if response body is null
                } else {
                    Log.e("ServiceSearchHelper", "Unsuccessful response: ${response.code}")
                    callback(emptyList()) // Return an empty list on unsuccessful response
                }
            }
        })
    }


    private fun parseSearchResults(response: String): List<SearchResult> {
        val places = mutableListOf<SearchResult>()
        val jsonObject = JSONObject(response)
        val results = jsonObject.optJSONArray("results") ?: return places

        for (i in 0 until results.length()) {
            val result = results.getJSONObject(i)
            val name = result.optString("name")
            val location = result.optJSONObject("geometry")?.optJSONObject("location")
            val lat = location?.optDouble("lat") ?: continue
            val lng = location.optDouble("lng")
            val address = result.optString("formatted_address") // Fetch the address
            places.add(SearchResult(name, LatLng(lat, lng), address))
        }
        return places
    }

}

data class SearchResult(val name: String, val location: LatLng, val address: String?)
