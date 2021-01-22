package com.ekosoftware.financialpreview.data.model.budget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgetTable")
data class Budget(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budgetId", index = true)
    val id: Int,

    @ColumnInfo(name = "budgetName")
    var name: String,

    @ColumnInfo(name = "budgetDescription")
    var description: String?,

    @ColumnInfo(name = "budgetCurrencyCode")
    var currencyCode: String,

    @ColumnInfo(name = "budgetLeftAmount")
    var leftAmount: Double,

    @ColumnInfo(name = "budgetStartingAmount")
    var startingAmount: Double,

    @ColumnInfo(name = "budgetUUID")
    var budgetUUID: Int,

    @ColumnInfo(name = "budgetFrom")
    var from: Int,

    @ColumnInfo(name = "budgetTo")
    var to: Int,
)