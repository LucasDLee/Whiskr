package com.example.whiskr_app.ui.adoption.model

data class FilterRequest(
    val data: FilterData
)

data class FilterData(
    val filters: List<Filter>
)

data class Filter(
    val fieldName: String,
    val operation: String,
    val criteria: Any
)