package com.ekosoftware.financialpreview.data.model.settle

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettleGroupWithMovementsCount(
    val id: String,
    val name: String,
    val description: String?=null,
    val percentage: Double,
    val count: Int? = 0
) : Parcelable