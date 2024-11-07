package com.example.whiskr_app.ui.adoption

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CatViewModel : ViewModel() {
    private val _catList = MutableLiveData<List<Cat>>()
    private val allCats = listOf(
        Cat( "", "Whiskers", 2, "Male", "Manx"),
        Cat("","Mittens", 5, "Female", "Siamese"),
        Cat("","Fluffy", 1, "Female", "Domestic Longhair"),
        Cat("","Shadow", 7, "Female", "Bengal")
    )

    val catList: LiveData<List<Cat>> = _catList

    init {
        _catList.value = allCats // Initialize with local list
    }

    fun getCatBreeds(): ArrayList<String> {
        var breedList = ArrayList<String>()
        breedList.add("Breed")
        breedList.add("All")
        for (cat in allCats)
            if (cat.breed in breedList) {
                continue
            } else {
                breedList.add(cat.breed)
        }
        return breedList
    }

    fun filterCatsByAge(ageOption: String) {
        val filteredCats = when (ageOption) {
            "All" -> allCats
            "Kitten" -> allCats.filter { it.age < 2 }
            "Adult" -> allCats.filter { it.age in 2..6 }
            "Senior" -> allCats.filter { it.age > 6 }
            else -> allCats
        }
        _catList.value = filteredCats // Update LiveData
    }
}