package com.example.whiskr_app.ui.adoption.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.CatListAdapter
import com.example.whiskr_app.ui.adoption.RescueGroupsViewModel
import com.example.whiskr_app.ui.adoption.model.AnimalData
import com.example.whiskr_app.ui.adoption.model.Filter
import com.example.whiskr_app.ui.adoption.model.FilterData
import com.example.whiskr_app.ui.adoption.model.FilterRequest
import com.example.whiskr_app.ui.adoption.showProvinceAndFilterDialog

class RescueGroupsCatListings : Fragment() {
    private lateinit var filter: TextView
    private lateinit var catListView: ListView
    private lateinit var catAdapter: CatListAdapter
    private lateinit var viewModel: RescueGroupsViewModel
    private val provinces = listOf(
        "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador",
        "Nova Scotia", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan"
    )

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

        viewModel = ViewModelProvider(this)[RescueGroupsViewModel::class.java]
        viewModel.animals.observe(viewLifecycleOwner) { animals ->
            catAdapter.updateCats(animals)
            catAdapter.notifyDataSetChanged()
        }

        viewModel.fetchAnimals()

        catListView.setOnItemClickListener { _, _, position, _ ->

            val selectedAnimal = catAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putParcelable("cat_detail", (selectedAnimal as AnimalData))

            findNavController().navigate(R.id.nav_adoption_details, bundle)

        }

        filter = view.findViewById(R.id.fragmentBrowseAdoptionFilterBy)
        filter.setOnClickListener {
            showProvinceAndFilterDialog(requireContext(), provinces) {
                // Handle the selected province and filters
                selectedProvince, selectedFilters ->
                println("Selected Province: $selectedProvince")
                println("Selected Filters: $selectedFilters")

                // Constructing a filter list:
                val filters = mutableListOf(
                    Filter("locations.country", "equals", "Canada"),
                    Filter("locations.state", "equals", selectedProvince)
                )

                // Add additional filters based on user selection
                if (selectedFilters.contains("Age Group: Baby")) {
                    filters.add(Filter("ageGroup", "equals", "Baby"))
                }

                // Pass filters to the API service
                val filterData = FilterData(filters)
                val filterRequest = FilterRequest(filterData)
                println("VC: $filterRequest" )
//            apiService.getAnimals(filterRequest)
            }
        }

    }
}