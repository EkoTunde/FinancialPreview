package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekosoftware.financialpreview.data.model.scheduled.Scheduled
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "movement_table")
data class Movement(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "movement_id")
    var id: Int? = null,

    @ColumnInfo(name = "movement_date")
    var date: Date? = null,

    @Embedded(prefix = "movement_old_")
    var scheduled: Scheduled? = null,

    @ColumnInfo(name = "movement_amount")
    var amount: Double,

    @ColumnInfo(name = "movement_account_id")
    var accountId: Int,

    @ColumnInfo(name = "movement_currency_code")
    var currencyCode: String

) : Parcelable