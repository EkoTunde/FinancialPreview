package com.ekosoftware.financialpreview.data.model.currency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currency(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "currencyCodeId")
    var id: String,

    @ColumnInfo(name = "currencyIsMain")
    var isMain: Boolean = false,

    /**
     * Indicates how many times main currency fits in current currency
     */
    @ColumnInfo(name = "currencyProportionFromMain")
    var proportionFromMain: Double

)
