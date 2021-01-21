package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "categoryTable")
data class Category (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoryId")
    var id: Int,

    @ColumnInfo(name = "categoryName")
    var name: String,

    @ColumnInfo(name = "categoryIconResId")
    var iconResId: Int,

    @ColumnInfo(name = "categoryColorResId")
    var colorResId: Int

) : Parcelable