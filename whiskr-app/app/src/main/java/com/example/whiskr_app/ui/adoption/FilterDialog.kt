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
    onFilterSelected: (province: String, selectedAge: List<String>, selectedSex: List<String>) -> Unit
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
    val sexFemaleCheckBox: CheckBox = dialogView.findViewById(R.id.filterGroupFemale)
    val sexMaleCheckBox: CheckBox = dialogView.findViewById(R.id.filterGroupMale)
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
        dialog.dismiss()
    }

    // Handle "OK" button click
    okButton.setOnClickListener {
        val selectedProvince = spinner.selectedItem as String
        val selectedProvinceAbbreviation = provincesMap[selectedProvince] ?: ""

        // Collect selected filters
        val selectedAgeGroup = mutableListOf<String>()
        if (ageGroupBabyCheckBox.isChecked) selectedAgeGroup.add("Baby")
        if (ageGroupYoungCheckBox.isChecked) selectedAgeGroup.add("Young")
        if (ageGroupAdultCheckBox.isChecked) selectedAgeGroup.add("Adult")
        if (ageGroupSeniorCheckBox.isChecked) selectedAgeGroup.add("Senior")

        // Collection of selected sex
        val selectedSex = mutableListOf<String>()
        if (sexFemaleCheckBox.isChecked) selectedSex.add("Female")
        if (sexMaleCheckBox.isChecked) selectedSex.add("Male")

        // Pass selected province and filters to the callback
        onFilterSelected(selectedProvinceAbbreviation, selectedAgeGroup, selectedSex)

        // Dismiss the dialog
        dialog.dismiss()
    }

    // Show the dialog
    dialog.show()
}