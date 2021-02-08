package com.ekosoftware.financialpreview.data.model.record

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RecordUIShort(
    var id: String,
    var date: Date?,
    var amount: Long,
    var currencyCode: String,
    var name: String?,
    var categoryName: String?,
    var categoryIconResId: Int?,
    var categoryColorResId: Int?
) : Parcelable