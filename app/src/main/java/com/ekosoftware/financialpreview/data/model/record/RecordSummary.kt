package com.ekosoftware.financialpreview.data.model.record

import java.util.*

data class RecordSummary(
    var id: String,
    var date: Date?,
    var currencyCode: String,
    var amount: Long,
    var name: String?,
    var categoryName: String?,
    var categoryIconResId: Int?,
    var categoryColorResId: Int?,
    var accountIdIn: String?,
    var accountIdOut: String?,
)