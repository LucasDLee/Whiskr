package com.example.whiskr_app.ui.adoption.model

data class FilterRequest(
    val data: FilterData
)

data class FilterData(
    val filters: List<Filter>,
    val filterRadius: FilterRadius? = null
)

data class Filter(
    val fieldName: String,
    val operation: String,
    val criteria: Any
)

data class FilterRadius(
    val kilometers: Int,
    val postalcode: String
)