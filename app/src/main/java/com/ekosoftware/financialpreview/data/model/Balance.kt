package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Balance(val total: Double, val currency: String) : Parcelable
