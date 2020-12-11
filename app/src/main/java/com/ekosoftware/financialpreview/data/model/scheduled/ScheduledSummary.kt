package com.ekosoftware.financialpreview.data.model.scheduled

import androidx.room.ColumnInfo

data class ScheduledSummary(

    @ColumnInfo(name = "scheduledId")
    val id: Int,

    @ColumnInfo(name = "scheduled_left_amount")
    val leftAmount: Double,

    @ColumnInfo(name = "scheduled_currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "category_icon_res_id")
    val categoryIconResId: Int,

    @ColumnInfo(name = "account_name")
    val accountName: String
)