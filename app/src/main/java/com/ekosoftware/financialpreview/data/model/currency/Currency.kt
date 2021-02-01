package com.ekosoftware.financialpreview.data.model.currency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currency(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "currencyCodeId")
    var currencyCode: String,

    @ColumnInfo(name = "currencyIsMain")
    var isMain: Boolean = false,

    @ColumnInfo(name = "currencyProportionToMain")
    var proportion: Double

)
