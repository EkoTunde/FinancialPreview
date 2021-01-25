package com.ekosoftware.financialpreview.data.model.account

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "accounts")
data class Account(

    @PrimaryKey
    @ColumnInfo(name = "accountId")
    var id: String,

    @ColumnInfo(name = "accountName")
    var name: String,

    @ColumnInfo(name = "accountCurrencyCode")
    var currencyCode: String = "",

    @ColumnInfo(name = "accountBalance")
    var balance: Double = 0.0,

    @ColumnInfo(name = "accountDescription")
    var description: String? = null,

    @ColumnInfo(name = "accountNumber")
    var number: String? = null,

    @ColumnInfo(name = "accountBank")
    var bank: String? = null,

    @ColumnInfo(name = "accountColorResId")
    var colorResId: Int?

) : Parcelable