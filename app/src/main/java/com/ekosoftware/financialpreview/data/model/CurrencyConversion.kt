package com.ekosoftware.financialpreview.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "currency_table", primaryKeys = ["currency_code"])
data class CurrencyConversion(

    @ColumnInfo(name = "currency_code")
    var code: String = "",

    @ColumnInfo(name = "currency_name")
    var name: String = "",

    @ColumnInfo(name = "currency_factor")
    var factor: Double = 0.0,

    @ColumnInfo(name = "currency_last_update")
    var lastUpdate: Date?
)

