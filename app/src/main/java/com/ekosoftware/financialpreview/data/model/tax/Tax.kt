package com.ekosoftware.financialpreview.data.model.tax

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "tax_table")
data class Tax(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "taxId")
    var taxId: Int,

    @ColumnInfo(name = "tax_name")
    var name: String,

    @ColumnInfo(name = "tax_percentage")
    var percentage: Double,

    @ColumnInfo(name = "tax_currency_code")
    var currencyCode: String

) : Parcelable