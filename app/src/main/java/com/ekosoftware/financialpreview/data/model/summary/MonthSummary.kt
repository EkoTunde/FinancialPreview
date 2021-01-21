package com.ekosoftware.financialpreview.data.model.summary

import android.content.Context
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.util.getMonth

data class MonthSummary(
    val currencyCode: String,
    val yearMonth: Int,
    val totalIncome: Double? = .0,
    val totalExpense: Double? = .0
) {
    val totalBalance: Double get() = ((totalIncome ?: .0) + (totalExpense ?: .0))

    fun monthName(context: Context): String? {
        return when (yearMonth.getMonth()) {
            1 -> context.getString(R.string.month_jan)
            2 -> context.getString(R.string.month_feb)
            3 -> context.getString(R.string.month_mar)
            4 -> context.getString(R.string.month_apr)
            5 -> context.getString(R.string.month_may)
            6 -> context.getString(R.string.month_jun)
            7 -> context.getString(R.string.month_jul)
            8 -> context.getString(R.string.month_aug)
            9 -> context.getString(R.string.month_sep)
            10 -> context.getString(R.string.month_oct)
            11 -> context.getString(R.string.month_noc)
            12 -> context.getString(R.string.month_dec)
            else -> null
        }
    }
}
