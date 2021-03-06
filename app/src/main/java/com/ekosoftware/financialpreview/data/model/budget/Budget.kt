package com.ekosoftware.financialpreview.data.model.budget

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekosoftware.financialpreview.data.model.movement.Frequency

@Entity(tableName = "budgets")
data class Budget(

    @PrimaryKey
    @ColumnInfo(name = "budgetId", index = true)
    var id: String = "Nan",

    @ColumnInfo(name = "budgetName")
    var name: String,

    @ColumnInfo(name = "budgetDescription")
    var description: String?,

    @ColumnInfo(name = "budgetCurrencyCode")
    var currencyCode: String,

    @ColumnInfo(name = "budgetLeftAmount")
    var leftAmount: Long,

    @ColumnInfo(name = "budgetStartingAmount")
    var startingAmount: Long,

    @Embedded(prefix = "budget")
    var frequency: Frequency?,
)