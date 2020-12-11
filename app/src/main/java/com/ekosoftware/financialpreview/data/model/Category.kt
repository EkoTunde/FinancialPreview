package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "category_table")
data class Category (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    var id: Int,

    @ColumnInfo(name = "category_name")
    var name: String,

    @ColumnInfo(name = "category_icon_res_id")
    var iconResId: Int

) : Parcelable