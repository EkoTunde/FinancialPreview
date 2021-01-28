package com.ekosoftware.financialpreview.data.model.movement


data class MovementSummary(
    var movementId: String,
    var leftAmount: Long,
    var currencyCode: String,
    var name: String,
    var categoryName: String?,
    val categoryIconResId: Int?,
    val categoryColorResId: Int?,
    var fromYearMonth: Int,
    var toYearMonth: Int,
    var totalInstallments: Int?
)
