package com.ekosoftware.financialpreview.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "currencies", primaryKeys = ["currencyCodeId"])
data class CurrencyConversion(

    @ColumnInfo(name = "currencyCodeId")
    var code: String = "",

    @ColumnInfo(name = "currencyName")
    var name: String = "",

    @ColumnInfo(name = "currencyFactor")
    var factor: Long = 0,

    @ColumnInfo(name = "currencyLastUpdate")
    var lastUpdate: Date?
)

