package com.ekosoftware.financialpreview.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "currencyTable", primaryKeys = ["currencyCodeId"])
data class CurrencyConversion(

    @ColumnInfo(name = "currencyCodeId")
    var code: String = "",

    @ColumnInfo(name = "currencyName")
    var name: String = "",

    @ColumnInfo(name = "currencyFactor")
    var factor: Double = 0.0,

    @ColumnInfo(name = "currencyLastUpdate")
    var lastUpdate: Date?
)

