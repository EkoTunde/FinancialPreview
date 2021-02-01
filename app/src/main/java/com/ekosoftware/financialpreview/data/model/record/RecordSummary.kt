package com.ekosoftware.financialpreview.data.model.record

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RecordSummary(
    var id: String,
    var date: Date?,
    var currencyCode: String,
    var amount: Long,
    var name: String?,
    var categoryId: String?,
    var categoryName: String?,
    var categoryIconResId: Int?,
    var categoryColorResId: Int?,
    var accountId: String?,
    var accountName: String?,
    var accountColorResId: Int?,
    var accountOutName: String?,
    var debtorName: String?,
    var lenderName: String?,
    var description: String?
) : Parcelable