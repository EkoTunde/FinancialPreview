package com.ekosoftware.financialpreview.data.model.movement

import androidx.room.ColumnInfo

data class MovementSummary(

    @ColumnInfo(name = "movementId")
    val id: Int,

    @ColumnInfo(name = "movementLeftAmount")
    val leftAmount: Double,

    @ColumnInfo(name = "movementCurrencyCode")
    val currencyCode: String,

    @ColumnInfo(name = "categoryName")
    val categoryName: String,

    @ColumnInfo(name = "categoryIconResId")
    val categoryIconResId: Int,

    @ColumnInfo(name = "accountName")
    val accountName: String
)