package com.example.whiskr_app.ui.adoption.service

import com.example.whiskr_app.ui.adoption.model.AnimalResponse
import com.example.whiskr_app.ui.adoption.model.FilterRequest

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RescueGroupsApiService {
    @Headers(
        "Content-Type: application/vnd.api+json",
        "Authorization: ####"
    )

    @POST("public/animals/search/available&limit=5")
    suspend fun getAvailableAnimals(
        @Body filters: FilterRequest
    ): AnimalResponse

    @POST("public/animals/search/available&limit=5")
    suspend fun getAnimals(
        @Body filters: FilterRequest
    ): AnimalResponse
}