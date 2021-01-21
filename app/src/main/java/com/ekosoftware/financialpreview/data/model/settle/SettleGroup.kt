package com.ekosoftware.financialpreview.data.model.settle

import android.os.Parcelable
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "settleGroupTable")
data class SettleGroup(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "settleGroupId", index = true)
    var id: Int,

    @ColumnInfo(name = "settleGroupName")
    var name: String,

    @ColumnInfo(name = "settleGroupDescription")
    var description: String,

    @ColumnInfo(name = "settleGroupPercentage")
    var percentage: Double,

    @ColumnInfo(name = "settleGroupCurrencyCode")
    var currencyCode: String

    ) : Parcelable

