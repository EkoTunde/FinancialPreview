package com.ekosoftware.financialpreview.data.model.movement

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.util.getMonth
import com.ekosoftware.financialpreview.util.parseToAbbreviatedString

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

    val monthName get() = yearMonth.getMonth().parseToAbbreviatedString()
}
