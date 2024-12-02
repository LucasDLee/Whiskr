package com.example.whiskr_app.ui.adoption.service

import com.example.whiskr_app.ui.adoption.model.AnimalResponse
import com.example.whiskr_app.ui.adoption.model.FilterRequest

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RescueGroupsApiService {

    @POST("public/animals/search/available/&include=pictures,orgs&limit=5")
    suspend fun getAvailableAnimalsByPostalCode(
        @Body filters: FilterRequest,
        @Header("Authorization") token: String = TODO("API KEY")
    ): AnimalResponse

    @POST("public/animals/search/available/&include=pictures,orgs&limit=5")
    suspend fun getAvailableAnimals(
        @Body filters: FilterRequest,
        @Header("Authorization") token: String = TODO("API KEY")
    ): AnimalResponse
}