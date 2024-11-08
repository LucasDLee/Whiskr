package com.example.whiskr_app.ui.adoption

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.whiskr_app.R

class CatFragment : Fragment() {
    private lateinit var catListView: ListView
    private lateinit var ageSpinner: Spinner
    private lateinit var sexSpinner: Spinner
    private lateinit var distanceSpinner: Spinner
    private lateinit var breedSpinner: Spinner
    private lateinit var catAdapter: CatListAdapter
    private lateinit var catViewModel: CatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse_adoption, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catListView = view.findViewById(R.id.fragmentBrowseAdoptionListView)
        catAdapter = CatListAdapter(requireActivity(), emptyList())
        catListView.adapter = catAdapter

        catViewModel = ViewModelProvider(requireActivity()).get(CatViewModel::class.java)
        catViewModel.catList.observe(requireActivity()) { cats ->
            catAdapter.updateCats(cats)
            catAdapter.notifyDataSetChanged()
        }

        createAgeSpinner(view)
        createBreedSpinner(view)
        createDistanceSpinner(view)
        createSexSpinner(view)
    }

    private fun createSexSpinner(view: View) {
        sexSpinner = view.findViewById(R.id.fragmentBrowseAdoptionSpinnerSex)
        val sexOptions = listOf("Sex", "All", "Female", "Male")

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            sexOptions
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_dropdown_item_1line
        )
        sexSpinner.adapter = spinnerAdapter

        sexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long) {
                val selectedSexOption = sexOptions[position]
                catViewModel.filterCatsBySex(selectedSexOption)
            }

            override fun onNothingSelected(
                parent: AdapterView<*>?
            ) {}
        }
    }

    private fun createDistanceSpinner(view: View) {
        distanceSpinner = view.findViewById(R.id.fragmentBrowseAdoptionSpinnerDistance)
        val distanceOptions = listOf("Distance", "All", "25", "50", "100")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            distanceOptions
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_dropdown_item_1line
        )
        distanceSpinner.adapter = spinnerAdapter

        distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long) {
                val selectedDistanceOption = distanceOptions[position]
                catViewModel.filterCatsByDistance(selectedDistanceOption)
            }

            override fun onNothingSelected(
                parent: AdapterView<*>?
            ) {}
        }
    }

    private fun createBreedSpinner(view: View) {
        breedSpinner = view.findViewById(R.id.fragmentBrowseAdoptionSpinnerBreed)
        val breedOptions = listOf("Breed", "All", "Manx", "Siamese", "Domestic Longhair", "Bengal")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            breedOptions
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_dropdown_item_1line
        )
        breedSpinner.adapter = spinnerAdapter

        breedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long) {
                val selectedBreedOption = breedOptions[position]
                catViewModel.filterCatsByBreed(selectedBreedOption)
            }

            override fun onNothingSelected(
                parent: AdapterView<*>?
            ) {}
        }
    }

    private fun createAgeSpinner(view: View) {
        ageSpinner = view.findViewById(R.id.fragmentBrowseAdoptionSpinnerAge)
        val ageOptions = listOf("Age", "All", "Kitten", "Adult", "Senior")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            ageOptions
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_dropdown_item_1line
        )
        ageSpinner.adapter = spinnerAdapter

        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long) {
                val selectedAgeOption = ageOptions[position]
                catViewModel.filterCatsByAge(selectedAgeOption)
            }

            override fun onNothingSelected(
                parent: AdapterView<*>?
            ) {}
        }
    }
}