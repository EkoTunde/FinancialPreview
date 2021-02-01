package com.ekosoftware.financialpreview.data.model.movement

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovementUI(
    var id: String,
    var leftAmount: Long,
    var startingAmount: Long,
    var currencyCode: String,
    var name: String,
    var fromYearMonth: Int,
    var toYearMonth: Int,
    var totalInstallments: Int?,
    var monthsChecked: String,
    var accountId: String?,
    var accountName: String?,
    var accountColorResId: Int?,
    var categoryId: String?,
    var categoryName: String?,
    val categoryIconResId: Int?,
    val categoryColorResId: Int?,
    var budgetId: String?,
    var budgetName: String?
) : Parcelable
