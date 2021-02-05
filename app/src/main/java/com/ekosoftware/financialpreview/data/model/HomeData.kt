package com.ekosoftware.financialpreview.data.model

import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.util.forCommunicationAmount
import com.ekosoftware.financialpreview.util.taxes

data class HomeData(
    var currencyCode: String = "",
    var currentYearMonth: Int = 0,
    var balance: Long = 0,
    var monthSummaries: List<MonthSummary> = emptyList(),
    var settleGroupsWithMovements: List<SettleGroupWithMovements> = emptyList()
) {

    // Gets the current balance as a Double.
    val currentBalance get() = balance.forCommunicationAmount()

    // Maps each MonthSummary to corresponding month name.
    val monthsNames get() = monthSummaries.map { month -> month.monthName ?: "" }

    // Gets total income, made up from the sum of all pending movements, budgets and
    // taxes (settle groups percentage times included movements) whose amount is bigger than zero.
    val incomes
        get() = monthSummaries.map { month ->
            (month.movementIncome ?: 0) + (month.budgetIncome
                ?: 0) + settleGroupsWithMovements.sumOf { settleGroupWithMovements ->
                settleGroupWithMovements.taxes(month.yearMonth, month.currencyCode) {
                    it.leftAmount > 0
                }
            }
        }

    // Gets total income, made up from the sum of all pending movements, budgets and
    // taxes (settle groups percentage times included movements) whose amount is less than zero.
    val expenses
        get() = monthSummaries.map { month ->
            (month.movementExpense ?: 0) + (month.budgetExpense
                ?: 0) + settleGroupsWithMovements.sumOf { settleGroupWithMovements ->
                settleGroupWithMovements.taxes(month.yearMonth, month.currencyCode) {
                    it.leftAmount < 0
                }
            }
        }

    // The result of adding total incomes and total expenses.
    val balances
        get() = listOf(0..12).flatten().map { index ->
            (incomes[index] + expenses[index]).forCommunicationAmount()
        }

    // The result of adding month by month incomes and expenses
    // while adding the accumulated balance from previous months.
    // Adds current balance to first month.
    val accumulatedBalances
        get() = listOf(0..12).flatten().map { index ->
            incomes[index] + expenses[index]
        }.scan(balance) { acc, balance ->
            balance + acc
        }.map { it.forCommunicationAmount() }
}