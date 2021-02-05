package com.ekosoftware.financialpreview.data.model

import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.util.forCommunicationAmount

data class HomeData(
    var currencyCode: String = "",
    var currentYearMonth: Int = 0,
    var currentAccountsBalance: Long = 0,
    var monthSummaries: List<MonthSummary> = emptyList(),
    var settleGroupsWithMovements: List<SettleGroupWithMovements> = emptyList()
) {

    /**
     * Gets the current balance as a Double.
     */
    val currentBalance get() = currentAccountsBalance.forCommunicationAmount()

    /**
     * Maps each MonthSummary to corresponding month name.
     */
    val monthsNames get() = monthSummaries.map { month -> month.monthNameAbbr ?: "" }

    /** Gets total income, made up from the sum of all pending movements, budgets and
     * taxes (settle groups percentage times included movements) whose amount is more than zero.
     */
    val incomes get() = monthSummaries.map { it.getTotalIncomes().forCommunicationAmount() }
    /*(month.movementIncome ?: 0) + (month.budgetIncome ?: 0) + settleGroupsWithMovements.sumOf { settleGroupWithMovements ->
            settleGroupWithMovements.taxes(month.yearMonth, month.currencyCode) {
                it.leftAmount > 0
            }
        }*/

    /**
     * Gets total expenses, made up from the sum of all pending movements, budgets and
     * taxes (settle groups percentage times included movements) whose amount is less than zero.
     */
    val expenses get() = monthSummaries.map { it.getTotalExpenses().forCommunicationAmount() }
    /*val f = BigDecimal((month.movementExpense ?: 0).forCommunicationAmount().toString()).plus(
        BigDecimal(
            (month.budgetExpense ?: 0).forCommunicationAmount().toString()
        )
    ).plus(BigDecimal((month.settleGroupsExpense ?: 0L).forCommunicationAmount().toString())*//*.times(BigDecimal("0.012")))*//*)
            Log.d("PRUEBAAA", "$f")
            (month.movementExpense ?: 0) + (month.budgetExpense ?: 0) + (month.settleGroupsExpense
                ?: 0) + settleGroupsWithMovements.sumOf { settleGroupWithMovements ->
                val g = settleGroupWithMovements.taxes(month.yearMonth, month.currencyCode) {
                    it.leftAmount < 0
                }
                //Log.d("HOLA2", "$g")
                g
            }*/


    /**
     * The result of adding total incomes and total expenses.
     */
    val balances
        get() = monthSummaries.map { it.getBalance().forCommunicationAmount() }
    /* listOf(0..12).flatten().map { index ->
            (incomes[index] + expenses[index]).forCommunicationAmount()
        }*/

    /**
     * The result of adding month by month incomes and expenses
     * while adding the accumulated balance from previous months.
     * Adds current balance to first month.
     */
    val accumulatedBalances
        get() = monthSummaries.map { it.getBalance() }.scan(currentAccountsBalance) { acc, balance ->
            balance + acc
        }.map { it.forCommunicationAmount() }

    /*listOf(0..12).flatten().map { index ->
            incomes[index] + expenses[index]
        }.scan(currentAccountsBalance) { acc, balance ->
            balance + acc
        }.map { it.forCommunicationAmount() }*/

    /*fun getAccumulatedBalances(): List<Double> {
        return monthSummaries.map { it.getBalance() }.scan(currentAccountsBalance) { acc, balance ->
            balance + acc
        }.map {
            it.forCommunicationAmount()
        }
    }*/
}