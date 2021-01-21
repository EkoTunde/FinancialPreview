package com.ekosoftware.financialpreview.data.model.summary


data class MovementSummary(
    var movementId: Int,
    var leftAmount: Double,
    var currencyCode: String,
    var name: String,
    var categoryName: String?,
    val categoryIconResId: Int,
    val categoryColorResId: Int,
    var fromYearMonth: Int,
    var toYearMonth: Int,
    var totalInstallments: Int?
)
