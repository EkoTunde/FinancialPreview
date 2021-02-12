package com.ekosoftware.financialpreview.data.model

data class SimpleQueryData(
    var id: String,
    var name: String,
    var currencyCode: String? = null,
    var amount: Double? = null,
    var typeId: Int? = null,
    var description: String? = null,
    var color: Int? = null,
    var iconResId: Int? = null
)