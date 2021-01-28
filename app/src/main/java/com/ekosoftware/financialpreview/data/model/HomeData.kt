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

    val currentBalance get() = balance.forCommunicationAmount()

    val monthsNames get() = monthSummaries.map { month -> month.monthName ?: "" }

    val incomes
        get() = monthSummaries.map { month ->
            (month.movementIncome ?: 0) + (month.budgetIncome
                ?: 0) + settleGroupsWithMovements.sumOf { settleGroupWithMovements ->
                settleGroupWithMovements.taxes(month.yearMonth, month.currencyCode) {
                    it.leftAmount > 0
                }
            }
        }

    val expenses
        get() = monthSummaries.map { month ->
            (month.movementExpense ?: 0) + (month.budgetExpense
                ?: 0) + settleGroupsWithMovements.sumOf { settleGroupWithMovements ->
                settleGroupWithMovements.taxes(month.yearMonth, month.currencyCode) {
                    it.leftAmount < 0
                }
            }
        }

    val balances
        get() = listOf(0..12).flatten().map { index ->
            (incomes[index] + expenses[index]).forCommunicationAmount()
        }

    val accumulatedBalances
        get() = listOf(0..12).flatten().map { index ->
            incomes[index] + expenses[index]
        }.scan(balance) { acc, balance ->
            balance + acc
        }.map { it.forCommunicationAmount() }
}