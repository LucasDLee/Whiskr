package com.example.whiskr_app.ui.catmap

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class ServiceSearchHelper(
    private val location: LatLng,
    private val query: String? = null,
) {
    private val apiKey = "TODO"
    private val client = OkHttpClient()

    fun findPlaces(callback: (List<SearchResult>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encodedQuery = URLEncoder.encode(query ?: "", "UTF-8")
                val url =
                    "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$encodedQuery&location=${location.latitude},${location.longitude}&radius=5000&key=$apiKey"

                Log.d("ServiceSearchHelper", "Requesting URL: $url")

                val request = Request.Builder().url(url).get().build()

                val response = client.newCall(request).execute() // Synchronous call
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val results = parseSearchResults(responseBody)
                        withContext(Dispatchers.Main) {
                            callback(results)
                        }
                    } else {
                        Log.e("ServiceSearchHelper", "Response body is empty")
                        withContext(Dispatchers.Main) {
                            callback(emptyList())
                        }
                    }
                } else {
                    Log.e("ServiceSearchHelper", "Unsuccessful response: ${response.code}")
                    withContext(Dispatchers.Main) {
                        callback(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("ServiceSearchHelper", "Error fetching places: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            }
        }
    }

    // Parse JSON responses for location details
    private fun parseSearchResults(response: String): List<SearchResult> {
        val places = mutableListOf<SearchResult>()
        try {
            val jsonObject = JSONObject(response)
            val results = jsonObject.optJSONArray("results") ?: return places

            for (i in 0 until results.length()) {
                val result = results.getJSONObject(i)
                val name = result.optString("name")
                val location = result.optJSONObject("geometry")?.optJSONObject("location")
                val lat = location?.optDouble("lat") ?: continue
                val lng = location.optDouble("lng")
                val address = result.optString("formatted_address")
                places.add(SearchResult(name, LatLng(lat, lng), address))
            }
        } catch (e: Exception) {
            Log.e("ServiceSearchHelper", "Error parsing search results: ${e.message}")
        }
        return places
    }
}

data class SearchResult(val name: String, val location: LatLng, val address: String?)
