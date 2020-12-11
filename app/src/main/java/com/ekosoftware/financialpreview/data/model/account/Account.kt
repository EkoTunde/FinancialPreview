package com.ekosoftware.financialpreview.data.model.account

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "account_table")
data class Account(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id")
    var id: Int = 0,

    @ColumnInfo(name = "account_currency_code")
    var currencyCode: String = "",

    @ColumnInfo(name = "account_name")
    var name: String,

    @ColumnInfo(name = "account_balance")
    var balance: Double = 0.0,

    @ColumnInfo(name = "account_description")
    var description: String? = null,

    @ColumnInfo(name = "account_number")
    var number: String? = null,

    @ColumnInfo(name = "account_bank")
    var bank: String? = null

)