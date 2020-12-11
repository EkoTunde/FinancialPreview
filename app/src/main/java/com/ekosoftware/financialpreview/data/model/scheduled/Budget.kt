package com.ekosoftware.financialpreview.data.model.scheduled

import androidx.room.ColumnInfo

data class Budget (

    @ColumnInfo(name = "scheduledId")
    val id: Int,

    @ColumnInfo(name = "scheduled_name")
    var name: String,

    @ColumnInfo(name = "scheduled_currency_code")
    var currencyCode: String,

    @ColumnInfo(name = "scheduled_left_amount")
    var leftAmount: Double,

    @ColumnInfo(name = "scheduled_starting_amount")
    var startingAmount: Double,

    @ColumnInfo(name = "category_name")
    var categoryName: String?,

    @ColumnInfo(name = "category_icon_res_id")
    var categoryIconResId: Int?,

    @ColumnInfo(name = "scheduled_budget_id")
    var budgetId: Int
)