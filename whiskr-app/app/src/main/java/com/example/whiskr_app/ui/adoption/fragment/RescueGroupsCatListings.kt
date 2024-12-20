package com.example.whiskr_app.ui.adoption.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.adapter.CatListAdapter
import com.example.whiskr_app.ui.adoption.view_model.RescueGroupsViewModel
import com.example.whiskr_app.ui.adoption.model.AnimalData
import com.example.whiskr_app.ui.adoption.model.Filter
import com.example.whiskr_app.ui.adoption.model.FilterRadius
import com.example.whiskr_app.ui.adoption.dialog.showProvinceAndFilterDialog
import com.example.whiskr_app.ui.adoption.util.Canadian
import kotlin.collections.ArrayList

class RescueGroupsCatListings : Fragment() {
    private lateinit var filter: TextView
    private lateinit var catListView: ListView
    private lateinit var catAdapter: CatListAdapter
    private lateinit var viewModel: RescueGroupsViewModel
    private lateinit var emptyView: TextView
    private var selectProvince: String = ""
    private var selectAgeGroup: List<String> = listOf()
    private var selectSex: List<String> = listOf()
    private var switchState = false
    private var postalCode = " "

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse_adoption, container, false)
    }

    /**
     * Builds the adoption filter UI
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectProvince = " "
        selectAgeGroup = listOf("Baby", "Young", "Adult", "Senior")
        selectSex = listOf("Female", "Male")



        // Restore saved state
        if (savedInstanceState != null) {
            val restoredProvince = savedInstanceState.getString("selectedProvince", "")
            val restoredAgeGroup = savedInstanceState.getStringArrayList("selectedAgeGroup")
            val restoredSexes = savedInstanceState.getStringArrayList("selectedSexGroup")
            val restoredPostalCode = savedInstanceState.getString("postalCode", " ")
            val restoredSwitchState = savedInstanceState.getBoolean("switchState", false)
            postalCode = restoredPostalCode
            switchState = restoredSwitchState
            if (restoredAgeGroup != null) {
                selectAgeGroup = restoredAgeGroup
            }

            if (restoredProvince != null) {
                selectProvince = restoredProvince
            }

            if (restoredSexes != null) {
                selectSex = restoredSexes
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

        if (!switchState) {
            applyFilters()
        }

        catListView.setOnItemClickListener { _, _, position, _ ->
            val selectedAnimal = catAdapter.getItem(position) as? AnimalData
            val extra = viewModel.included.value ?: emptyList()

            if (selectedAnimal != null) {
                val intent = Intent(requireContext(), DetailsActivity::class.java)
                intent.putExtra("cat_detail", selectedAnimal)
                intent.putParcelableArrayListExtra("cat_details_extra", ArrayList(extra))
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Failed to load details.", Toast.LENGTH_SHORT).show()
            }
        }



        filter = view.findViewById(R.id.fragmentBrowseAdoptionFilterBy)
        filter.setOnClickListener {
            val util = Canadian()
            val provinceName = util.getProvinceName(selectProvince)
            showProvinceAndFilterDialog(requireContext(), util.getProvinces(),
                provinceName, selectAgeGroup, selectSex, postalCode, switchState) {
                // Handle the selected province and filters
                filterType, filterValue, selectedAgeGroup, selectedSex, switchState ->
                if (filterType == "province") {
                    selectProvince = filterValue
                    postalCode = " "
                }
                if (filterType == "postalCode") {
                    postalCode = filterValue
                    val converter = Canadian()
                    val province = converter.getProvinceFromPostalCode(postalCode)
                    selectProvince = province
                }

                selectAgeGroup = selectedAgeGroup
                selectSex = selectedSex
                this.switchState = switchState

                if (filterType == "province") {
                    applyFilters()
                } else if (filterType == "postalCode") {
                    applyFiltersPostalCode()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedProvince", selectProvince)
        outState.putString("postalCode", postalCode)
        outState.putBoolean("switchState", switchState)
        outState.putStringArrayList("selectedAgeGroup",  ArrayList(selectAgeGroup))
        outState.putStringArrayList("selectedSexGroup", ArrayList(selectSex))
    }

    /**
     * Uses postal code as the filter
     */
    private fun applyFiltersPostalCode() {
        val filters = mutableListOf(
            Filter("statuses.name", "equals", "Available"),
            Filter("species.singular", "equals", "Cat"),
            Filter("locations.country", "equals", "Canada"),
        )
        val filterRadius = FilterRadius(300, postalCode)

        if (selectAgeGroup.isNotEmpty()) {
            filters.add(Filter("animals.ageGroup", "equals", selectAgeGroup))
        }

        if (selectSex.isNotEmpty()) {
            filters.add(Filter("animals.sex", "equals", selectSex))
        }

        viewModel.fetchAnimalsByPostalCode(filters, filterRadius)
    }

    /**
     * Enables filters for the user
     */
    private fun applyFilters() {
        if (selectProvince.isBlank()) {
            selectProvince = "BC"
        }
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