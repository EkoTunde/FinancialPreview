package com.ekosoftware.financialpreview.data.model.account

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Balance(val total: Double, val currency: String)
