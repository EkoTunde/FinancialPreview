package com.ekosoftware.financialpreview.data.model.movement

import androidx.room.ColumnInfo

data class Budget (

    @ColumnInfo(name = "movementId")
    val id: Int,

    @ColumnInfo(name = "movementName")
    var name: String,

    @ColumnInfo(name = "movementCurrencyCode")
    var currencyCode: String,

    @ColumnInfo(name = "movementLeftAmount")
    var leftAmount: Double,

    @ColumnInfo(name = "movementStartingSmount")
    var startingAmount: Double,

    @ColumnInfo(name = "categoryName")
    var categoryName: String?,

    @ColumnInfo(name = "categoryIconResId")
    var categoryIconResId: Int?,

    @ColumnInfo(name = "movementBudgetId")
    var budgetId: Int
)