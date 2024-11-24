package com.example.whiskr_app.ui.adoption

import com.example.whiskr_app.ui.adoption.service.RescueGroupsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.rescuegroups.org/v5/"

    val apiService: RescueGroupsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
            .build()
            .create(RescueGroupsApiService::class.java)
    }
}