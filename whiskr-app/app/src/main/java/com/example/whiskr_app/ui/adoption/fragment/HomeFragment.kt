package com.example.whiskr_app.ui.adoption.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.util.Canadian

class HomeFragment : Fragment() {
    private lateinit var spinner: Spinner
    private lateinit var submit: Button
    private var provinceAbbreviation = " "
    private var firstTime = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse_adoption_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val util = Canadian()
        val listOfProvinces = util.getProvinces()

        // Set up the spinner with the provinces list
        spinner = view.findViewById(R.id.fragmentBrowseAdoptionHomeSpinner)
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listOfProvinces)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        submit = view.findViewById(R.id.fragmentBrowseAdoptionHomeSubmit)
        submit.setOnClickListener {

            val bundle = Bundle()
            val selectedProvince = spinner.selectedItem as String
            provinceAbbreviation = util.getProvinceAbbreviation(selectedProvince)
            bundle.putString("selectedProvince", provinceAbbreviation)
            firstTime = false
            findNavController().navigate(R.id.nav_adoption, bundle)

        }
    }

}