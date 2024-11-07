package com.example.whiskr_app.ui.adoption

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CatViewModel : ViewModel() {
    private val _catList = MutableLiveData<List<Cat>>()
    private val allCats = listOf(
        Cat( "", "Whiskers", 2, "Male", "Manx" , 13.0),
        Cat("","Mittens", 5, "Female", "Siamese", 25.0),
        Cat("","Fluffy", 1, "Female", "Domestic Longhair", 125.2),
        Cat("","Shadow", 7, "Female", "Bengal", 55.5)
    )

    val catList: LiveData<List<Cat>> = _catList

    init {
        _catList.value = allCats // Initialize with local list
    }

    fun filterCatsByAge(ageOption: String) {
        val filteredCats = when (ageOption) {
            "Kitten" -> allCats.filter { it.age < 2 }
            "Adult" -> allCats.filter { it.age in 2..6 }
            "Senior" -> allCats.filter { it.age > 6 }
            else -> allCats
        }
        _catList.value = filteredCats // Update LiveData
    }

    fun filterCatsByDistance(distanceOption: String) {
        val filteredCats = when (distanceOption) {
            "25" -> allCats.filter { it.distance < 25.0 }
            "50" -> allCats.filter { it.distance in 25.0 .. 50.0 }
            "100" -> allCats.filter { it.distance > 100.0 }
            else -> allCats
        }
        _catList.value = filteredCats // Update LiveData
    }
    fun filterCatsBySex(sexOption: String) {
        val filteredCats = when (sexOption) {
            "All" -> allCats
            "Sex" -> allCats
            else -> allCats.filter { it.sex == sexOption }
        }
        _catList.value = filteredCats // Update LiveData
    }

    fun filterCatsByBreed(breedOption: String) {
        val filteredCats = when (breedOption) {
            "All" -> allCats
            "Breed" -> allCats
            else -> allCats.filter { it.breed == breedOption }
        }
        println("VC: $filteredCats")
        _catList.value = filteredCats
    }
}