package com.example.whiskr_app.ui.adoption

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiskr_app.ui.adoption.model.AnimalData
import com.example.whiskr_app.ui.adoption.model.Filter
import com.example.whiskr_app.ui.adoption.model.FilterData
import com.example.whiskr_app.ui.adoption.model.FilterRequest
import com.example.whiskr_app.ui.adoption.model.IncludedItem
import kotlinx.coroutines.launch


class RescueGroupsViewModel : ViewModel() {

    private val _animals = MutableLiveData<List<AnimalData>>()
    val animals: LiveData<List<AnimalData>> get() = _animals

    private val _included = MutableLiveData<List<IncludedItem>>()
    val included: LiveData<List<IncludedItem>> get() = _included

    private val apiService = RetrofitClient.apiService

    fun fetchAnimals() {
        viewModelScope.launch {
            try {
                // Perform the API call
                val filters = listOf(
                    Filter("statuses.name", "equals", "Available"),
                    Filter("species.singular", "equals", "Cat"),
                    Filter("locations.country", "equals", "Canada"),
                    Filter("locations.state", "equals", "ON")
                )

                val filterRequest = FilterRequest(FilterData(filters))
                val response = apiService.getAvailableAnimals(filterRequest)

                _animals.value = response.data // Post the data to LiveData
                _included.value = response.included
                Log.d("Animal Fetch Success", response.toString())
            } catch (e: Exception) {
                Log.e("Animal Fetch Error", e.message.orEmpty())
            }
        }
    }
}