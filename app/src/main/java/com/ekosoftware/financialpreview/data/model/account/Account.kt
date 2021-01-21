package com.ekosoftware.financialpreview.data.model.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accountTable")
data class Account(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "accountId")
    var id: Int = 0,

    @ColumnInfo(name = "accountCurrencyCode")
    var currencyCode: String = "",

    @ColumnInfo(name = "accountName")
    var name: String,

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

)