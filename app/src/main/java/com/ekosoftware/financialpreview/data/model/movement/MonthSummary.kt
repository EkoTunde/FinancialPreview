package com.ekosoftware.financialpreview.data.model.movement

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.util.getMonth

/**
 * Data class representing a summary of a month ins and outs of money.
 *
 * @property currencyCode represents the
 *
 *
 */
data class MonthSummary(
    var currencyCode: String,
    var yearMonth: Int,
    var movementIncome: Long? = 0,
    var movementExpense: Long? = 0,
    var budgetIncome: Long? = 0,
    var budgetExpense: Long? = 0
) {
    val totalBalance
        get() = (movementIncome ?: 0 + (movementExpense ?: 0) + (budgetIncome ?: 0) + (budgetExpense ?: 0))

    val monthName
        get() = when (yearMonth.getMonth()) {
            1 -> Strings.get(R.string.month_jan)
            2 -> Strings.get(R.string.month_feb)
            3 -> Strings.get(R.string.month_mar)
            4 -> Strings.get(R.string.month_apr)
            5 -> Strings.get(R.string.month_may)
            6 -> Strings.get(R.string.month_jun)
            7 -> Strings.get(R.string.month_jul)
            8 -> Strings.get(R.string.month_aug)
            9 -> Strings.get(R.string.month_sep)
            10 -> Strings.get(R.string.month_oct)
            11 -> Strings.get(R.string.month_nov)
            12 -> Strings.get(R.string.month_dec)
            else -> null
        }
}
