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
import com.example.whiskr_app.ui.adoption.showProvinceAndFilterDialog
import java.util.ArrayList

class RescueGroupsCatListings : Fragment() {
    private lateinit var filter: TextView
    private lateinit var catListView: ListView
    private lateinit var catAdapter: CatListAdapter
    private lateinit var viewModel: RescueGroupsViewModel
    private lateinit var emptyView: TextView
    private lateinit var filters: MutableList<Filter>
    private lateinit var selectProvince: String
    private lateinit var selectAgeGroup: List<String>
    private lateinit var selectSex: List<String>

    private var default = true
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

        // Restore saved state
        if (savedInstanceState != null) {
            default = savedInstanceState.getBoolean("default")
            val restoredProvince = savedInstanceState.getString("selectedProvince")
            val restoredAgeGroup = savedInstanceState.getStringArrayList("selectedAgeGroup")

            if (restoredProvince != null && restoredAgeGroup != null) {
                selectProvince = restoredProvince
                selectAgeGroup = restoredAgeGroup
            }
        }

        emptyView = view.findViewById(R.id.fragmentsBrowseAdoptionEmpty)
        catListView = view.findViewById(R.id.fragmentBrowseAdoptionListView)
        catAdapter = CatListAdapter(requireActivity(), emptyList())
        catListView.adapter = catAdapter

        viewModel = ViewModelProvider(this)[RescueGroupsViewModel::class.java]
        viewModel.animals.observe(viewLifecycleOwner) { animals ->
            // Check for null returns
            if (animals.isNullOrEmpty()) {
                emptyView.visibility = View.VISIBLE
            } else {
                emptyView.visibility = View.GONE
                catAdapter.updateCats(animals)
                catAdapter.notifyDataSetChanged()
            }
        }

        if (default) {
            viewModel.fetchAnimals()
        } else {
            applyFilters()
        }

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
                selectedProvince, selectedAgeGroup, selectedSex ->
                println("Selected Province: $selectedProvince")
                selectProvince = selectedProvince
                println("Selected Age Groups: $selectedAgeGroup")
                selectAgeGroup = selectedAgeGroup
                println("Selected Sex: ${selectedSex}")
                selectSex = selectedSex

                // handle default for filter
                default = false

                // Constructing a filter list:
                filters = mutableListOf(
                    Filter("statuses.name", "equals", "Available"),
                    Filter("species.singular", "equals", "Cat"),
                    Filter("locations.country", "equals", "Canada"),
                    Filter("locations.state", "equals", selectedProvince)
                )

                // Add additional filters based on user selection
                if (selectedAgeGroup.isNotEmpty()) {
                    filters.add(Filter("animals.ageGroup", "equals", selectedAgeGroup))
                    selectAgeGroup = selectedAgeGroup
                }

                if (selectedSex.isNotEmpty()) {
                    filters.add(Filter("animals.sex", "equals", selectedSex))
                    selectSex = selectedSex
                }

                // Pass filters to the ViewModel
                viewModel.fetchAnimalsByProvince(filters)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("default", default)
        outState.putString("selectedProvince", selectProvince)
        outState.putStringArrayList("selectedAgeGroup", (selectAgeGroup as ArrayList<String>))
        outState.putStringArrayList("selectedSex", (selectSex as ArrayList<String>))

    }

    private fun applyFilters() {
        val filters = mutableListOf(
            Filter("statuses.name", "equals", "Available"),
            Filter("species.singular", "equals", "Cat"),
            Filter("locations.country", "equals", "Canada"),
            Filter("locations.state", "equals", selectProvince)
        )

        if (selectAgeGroup.isNotEmpty()) {
            filters.add(Filter("animals.ageGroup", "equals", selectAgeGroup))
        }

        if (selectSex.isNotEmpty()) {
            filters.add(Filter("animals.sex", "equals", selectSex))
        }

        viewModel.fetchAnimalsByProvince(filters)
    }
}