package com.example.whiskr_app.ui.adoption

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import com.example.whiskr_app.R

fun showProvinceAndFilterDialog(
    context: Context,
    provinces: List<String>,
    onFilterSelected: (province: String, filters: List<String>) -> Unit
) {
    val provincesMap = mapOf(
        "Alberta" to "AB",
        "British Columbia" to "BC",
        "Manitoba" to "MB",
        "New Brunswick" to "NB",
        "Newfoundland and Labrador" to "NL",
        "Nova Scotia" to "NS",
        "Ontario" to "ON",
        "Prince Edward Island" to "PE",
        "Quebec" to "QC",
        "Saskatchewan" to "SK"
    )

    // Inflate the dialog layout
    val inflater = LayoutInflater.from(context)
    val dialogView = inflater.inflate(R.layout.dialog_filters, null)

    // Create the dialog builder
    val dialogBuilder = AlertDialog.Builder(context)
        .setView(dialogView)
        .setCancelable(true)

    // Create the actual dialog instance here
    val dialog = dialogBuilder.create()

    val spinner: Spinner = dialogView.findViewById(R.id.provinceSpinner)
    val ageGroupBabyCheckBox: CheckBox = dialogView.findViewById(R.id.filterAgeGroupBaby)
    val ageGroupYoungCheckBox: CheckBox = dialogView.findViewById(R.id.filterAgeGroupYoung)
    val ageGroupAdultCheckBox: CheckBox = dialogView.findViewById(R.id.filterAgeGroupAdult)
    val ageGroupSeniorCheckBox: CheckBox = dialogView.findViewById(R.id.filterAgeGroupSenior)
    val cancelButton: Button = dialogView.findViewById(R.id.dialogCancel)
    val okButton: Button = dialogView.findViewById(R.id.dialogOk)

    // Set up the spinner with the provinces list
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, provinces)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter

    // Handle "Cancel" button click
    cancelButton.setOnClickListener {
        println("VC: CANCEL CLICKED")
        dialog.dismiss()
    }

    // Handle "OK" button click
    okButton.setOnClickListener {
        val selectedProvince = spinner.selectedItem as String
        val selectedProvinceAbbreviation = provincesMap[selectedProvince] ?: ""

        // Collect selected filters
        val selectedFilters = mutableListOf<String>()
        if (ageGroupBabyCheckBox.isChecked) selectedFilters.add("Age Group: Baby")
        if (ageGroupYoungCheckBox.isChecked) selectedFilters.add("Age Group: Young")
        if (ageGroupAdultCheckBox.isChecked) selectedFilters.add("Age Group: Adult")
        if (ageGroupSeniorCheckBox.isChecked) selectedFilters.add("Age Group: Senior")

        // Pass selected province and filters to the callback
        onFilterSelected(selectedProvinceAbbreviation, selectedFilters)

        // Dismiss the dialog
        dialog.dismiss()
    }

    // Show the dialog
    dialog.show()
}