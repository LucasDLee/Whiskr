package com.example.whiskr_app.ui.adoption.util

class Canadian {
    private val provinces = listOf(
        "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador",
        "Nova Scotia", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan"
    )

    /**
     * Adds abbreviations to provinces for filtering
     */
    fun getProvinceAbbreviation(province: String): String {
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
        return provincesMap[province] ?: " "
    }

    /**
     * Translates province abbreviations to full name for legibility
     */
    fun getProvinceName(abbreviation: String): String {
        val provincesMap = mapOf(
            "AB" to "Alberta",
            "BC" to "British Columbia",
            "MB" to "Manitoba",
            "NB" to "New Brunswick",
            "NL" to "Newfoundland and Labrador",
            "NS" to "Nova Scotia",
            "ON" to "Ontario",
            "PE" to "Prince Edward Island",
            "QC" to "Quebec",
            "SK" to "Saskatchewan"
        )
        return provincesMap[abbreviation] ?: " "
    }

    fun getProvinces(): List<String> {
        return provinces
    }

    fun getProvinceFromPostalCode(postalCode: String): String {
        val provinceMap = mapOf(
            'A' to "NL", // Newfoundland and Labrador
            'B' to "NS", // Nova Scotia
            'C' to "PE", // Prince Edward Island
            'E' to "NB", // New Brunswick
            'G' to "QC", // Quebec
            'H' to "QC", // Quebec
            'J' to "QC", // Quebec
            'K' to "ON", // Ontario
            'L' to "ON", // Ontario
            'M' to "ON", // Ontario
            'N' to "ON", // Ontario
            'P' to "ON", // Ontario
            'R' to "MB", // Manitoba
            'S' to "SK", // Saskatchewan
            'T' to "AB", // Alberta
            'V' to "BC", // British Columbia
//            'X' to "NT/NU", // Northwest Territories/Nunavut
//            'Y' to "YT"  // Yukon
        )

        val firstChar = postalCode.uppercase().firstOrNull()
        return provinceMap[firstChar] ?: "Unknown Province/Territory"
    }

        fun isValidCanadianPostalCode(input: String): Boolean {
        val postalCodeRegex = Regex("^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$")
        return postalCodeRegex.matches(input)
    }

}