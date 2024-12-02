package com.example.whiskr_app.ui.adoption.dialog

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.util.Canadian

fun showProvinceAndFilterDialog(
    context: Context,
    provinces: List<String>,
    currentProvince: String,
    currentAgeGroups: List<String>,
    currentSexes: List<String>,
    currentPostalCode: String?,
    currentSwitchState: Boolean,
    onFilterSelected: (
        filterType: String,
        filterValue: String,
        selectedAge: List<String>,
        selectedSex: List<String>,
        switchState: Boolean
    ) -> Unit
) {

    // Inflate the dialog layout
    val inflater = LayoutInflater.from(context)
    val dialogView = inflater.inflate(R.layout.dialog_filters, null)

    // Create the dialog builder
    val dialogBuilder = AlertDialog.Builder(context)
        .setView(dialogView)
        .setCancelable(true)

    // Create the actual dialog instance here
    val dialog = dialogBuilder.create()

    val filterSwitch: SwitchCompat = dialogView.findViewById(R.id.dialogFilterSwitch)
    val postalCodeTextView: TextView = dialogView.findViewById(R.id.dialogPostalCodeTitle)
    val postalCodeEditText: EditText = dialogView.findViewById(R.id.postalCodeEditText)
    val spinnerTitle: TextView = dialogView.findViewById(R.id.dialogProvinceTitle)
    val spinner: Spinner = dialogView.findViewById(R.id.provinceSpinner)
    val error: TextView = dialogView.findViewById(R.id.dialogTextError)
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

    // Restore Switch State
    filterSwitch.isChecked = currentSwitchState
    spinner.visibility = if (currentSwitchState) View.GONE else View.VISIBLE
    spinnerTitle.visibility = if (currentSwitchState) View.GONE else View.VISIBLE

    postalCodeEditText.visibility = if (currentSwitchState) View.VISIBLE else View.GONE
    postalCodeTextView.visibility = if (currentSwitchState) View.VISIBLE else View.GONE

    // Restore current selections
    postalCodeEditText.setText(currentPostalCode ?: "")
    formatCanadianPostalCode(postalCodeEditText)

    // Pre-select the current province
    val currentProvinceIndex = provinces.indexOfFirst { it == currentProvince }
    if (currentProvinceIndex >= 0) {
        spinner.setSelection(currentProvinceIndex)
    }

    // Toggle visibility based on switch
    filterSwitch.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            spinner.visibility = View.GONE
            spinnerTitle.visibility = View.GONE
            postalCodeEditText.visibility = View.VISIBLE
            postalCodeTextView.visibility = View.VISIBLE
            formatCanadianPostalCode(postalCodeEditText)
        } else {
            spinner.visibility = View.VISIBLE
            spinnerTitle.visibility = View.VISIBLE
            postalCodeEditText.visibility = View.GONE
            postalCodeTextView.visibility = View.GONE
        }
    }

    // Pre-select the current age groups
    ageGroupBabyCheckBox.isChecked = "Baby" in currentAgeGroups
    ageGroupYoungCheckBox.isChecked = "Young" in currentAgeGroups
    ageGroupAdultCheckBox.isChecked = "Adult" in currentAgeGroups
    ageGroupSeniorCheckBox.isChecked = "Senior" in currentAgeGroups

    // Pre-select the current sexes
    sexFemaleCheckBox.isChecked = "Female" in currentSexes
    sexMaleCheckBox.isChecked = "Male" in currentSexes

    // Handle "Cancel" button click
    cancelButton.setOnClickListener {
        dialog.dismiss()
    }

    // Handle "OK" button click
    okButton.setOnClickListener {
        val converter = Canadian()
        var onCheck = true
        val filterType: String
        val filterValue: String

        if (filterSwitch.isChecked) {
            filterType = "postalCode"
            filterValue = postalCodeEditText.text.toString()
        } else {
            filterType = "province"
            val selectedProvince = spinner.selectedItem as String
            val util = Canadian()
            filterValue = util.getProvinceAbbreviation(selectedProvince)
        }

        // Collect selected filters
        val selectedAgeGroup = mutableListOf<String>()
        if (ageGroupBabyCheckBox.isChecked) selectedAgeGroup.add("Baby") else selectedAgeGroup.remove("Baby")
        if (ageGroupYoungCheckBox.isChecked) selectedAgeGroup.add("Young") else selectedAgeGroup.remove("Young")
        if (ageGroupAdultCheckBox.isChecked) selectedAgeGroup.add("Adult") else selectedAgeGroup.remove("Adult")
        if (ageGroupSeniorCheckBox.isChecked) selectedAgeGroup.add("Senior") else selectedAgeGroup.remove("Senior")

        // Collection of selected sex
        val selectedSex = mutableListOf<String>()
        if (sexFemaleCheckBox.isChecked) selectedSex.add("Female") else selectedSex.remove("Female")
        if (sexMaleCheckBox.isChecked) selectedSex.add("Male") else selectedSex.remove("Male")

        // Check for empty
        if (selectedAgeGroup.isEmpty()) {
            error.text = "Selected Age Group Cannot be Empty"
            error.visibility = View.VISIBLE
            onCheck = false
        } else if (selectedSex.isEmpty()) {
            error.text = "Selected Sex Group Cannot be Empty"
            error.visibility = View.VISIBLE
            onCheck = false
        } else {
            error.visibility = View.GONE
        }

        if (filterType == "postalCode") {
            if (!converter.isValidCanadianPostalCode(filterValue)) {
                error.text = "Invalid Postal Code"
                error.visibility = View.VISIBLE
                onCheck = false
            }
        } else {
            error.visibility = View.GONE
        }

        if (onCheck) {
            // Pass selected province and filters to the callback
            onFilterSelected(filterType, filterValue, selectedAgeGroup, selectedSex, filterSwitch.isChecked)

            // Dismiss the dialog
            dialog.dismiss()
        }
    }

    // Show the dialog
    dialog.show()
}

    private fun formatCanadianPostalCode(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false // Prevent recursive calls

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                s?.let {
                    // Remove non-alphanumeric characters and format the text
                    val formatted = it.toString().uppercase().replace(Regex("[^A-Za-z0-9]"), "")
                    if (formatted.length >= 6) {
                        val postalCode = "${formatted.substring(0, 3)} ${formatted.substring(3, 6)}"
                        editText.setText(postalCode)
                        editText.setSelection(postalCode.length) // Move cursor to the end
                    } else {
                        editText.setText(formatted)
                        editText.setSelection(formatted.length)
                    }
                }

                isFormatting = false
            }
        })
    }