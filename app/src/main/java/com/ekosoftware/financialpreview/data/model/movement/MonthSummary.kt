package com.ekosoftware.financialpreview.data.model.movement

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
    val currentAccountsBalance: Long? = 0,
    var movementIncome: Long? = 0,
    var movementExpense: Long? = 0,
    var budgetIncome: Long? = 0,
    var budgetExpense: Long? = 0,
    var settleGroupsIncome: Long? = 0,
    var settleGroupsExpense: Long? = 0
) {
    val totalBalance
        get() = (movementIncome ?: 0 + (movementExpense ?: 0) + (budgetIncome ?: 0) + (budgetExpense ?: 0))

    val monthNameAbbr get() = yearMonth.getMonth().parseToAbbreviatedString()

    fun getTotalIncomes(): Long = (movementIncome ?: 0L) + (budgetIncome ?: 0L) + (settleGroupsIncome ?: 0L)

    fun getTotalExpenses(): Long = (movementExpense ?: 0L) + (budgetExpense ?: 0L) + (settleGroupsExpense ?: 0L)

    fun getBalance(): Long = getTotalIncomes() + getTotalExpenses()
}
