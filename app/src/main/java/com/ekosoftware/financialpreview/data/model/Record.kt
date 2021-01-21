package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekosoftware.financialpreview.data.model.movement.Movement
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "recordTable")
data class Record(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recordId")
    var id: Int? = null,

    @ColumnInfo(name = "recordDate")
    var date: Date? = null,

    @Embedded(prefix = "recordOld_")
    var movement: Movement? = null,

    @ColumnInfo(name = "recordAmount")
    var amount: Double,

    @ColumnInfo(name = "recordAccountId")
    var accountId: Int,

    @ColumnInfo(name = "recordCurrencyCode")
    var currencyCode: String

) : Parcelable