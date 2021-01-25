package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class Category (

    @PrimaryKey
    @ColumnInfo(name = "categoryId")
    var id: String,

    @ColumnInfo(name = "categoryName")
    var name: String,

    @ColumnInfo(name = "categoryIconResId")
    var iconResId: Int,

    @ColumnInfo(name = "categoryColorResId")
    var colorResId: Int

) : Parcelable