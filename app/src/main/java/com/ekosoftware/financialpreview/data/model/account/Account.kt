package com.ekosoftware.financialpreview.data.model.account

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekosoftware.financialpreview.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "accounts")
data class Account(

    @PrimaryKey
    @ColumnInfo(name = "accountId")
    var id: String = "Nan",

    @ColumnInfo(name = "accountName")
    var name: String,

    @ColumnInfo(name = "accountCurrencyCode")
    var currencyCode: String = "",

    @ColumnInfo(name = "accountBalance")
    var balance: Long = 0,

    @ColumnInfo(name = "accountTypeId")
    var typeId: Int = R.string.account_type_general,

    @ColumnInfo(name = "accountDescription")
    var description: String? = null,

    @ColumnInfo(name = "accountNumber")
    var number: String? = null,

    @ColumnInfo(name = "accountBank")
    var bank: String? = null,

    @ColumnInfo(name = "accountColorResId")
    var colorResId: Int?,

    @ColumnInfo(name = "accountIsMain")
    var isMainAccount: Boolean = false,

) : Parcelable

@Entity(tableName = "accountTypes")
data class AccountType(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "accountTypeId")
    var id: Int = 0,

    @ColumnInfo(name = "accountTypeNameStringResId")
    var nameStringResId: Int

)