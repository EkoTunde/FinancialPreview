package com.ekosoftware.financialpreview.data.model

import android.util.Log
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.util.forCommunicationAmount

data class HomeData(
    var currencyCode: String = "",
    var currentYearMonth: Int = 0,
    var currentAccountsBalance: Long = 0,
    var monthSummaries: List<MonthSummary> = emptyList()
) {

    /**
     * Gets the current balance as a Double.
     */
    fun currentBalance(): Double = currentAccountsBalance.forCommunicationAmount()

    /**
     * Maps each MonthSummary to corresponding month name.
     */
    fun monthsNames(): List<String> = monthSummaries.map { month -> month.monthNameAbbr ?: "" }

    /**
     * Gets total income, made up from the sum of all pending movements, budgets and
     * taxes (settle groups percentage times included movements) when their amount is more than zero.
     */
    fun incomes(): List<Double> = monthSummaries.map { it.getTotalIncomes().forCommunicationAmount() }

    /**
     * Gets total expenses, made up from the sum of all pending movements, budgets and
     * taxes (settle groups percentage times included movements) when their amount is less than zero.
     */
    fun expenses(): List<Double> = monthSummaries.map { it.getTotalExpenses().forCommunicationAmount() }


    /**
     * The result of adding total incomes and total expenses.
     */
    fun balances(): List<Double> = monthSummaries.map { it.getBalance().forCommunicationAmount() }

    /**
     * The result of adding month by month incomes and expenses
     * while adding the accumulated balance from previous months.
     * Adds current balance to first month.
     */
    fun accumulatedBalances(): List<Double> {
        return monthSummaries.map {
            Log.d("PRUEBA REAL B", "$it")
            it.getBalance()
        }.scan(currentAccountsBalance) { acc, balance ->
            Log.d("PRUEBA REAL A", "${balance + acc}")
            balance + acc
        }.map { it.forCommunicationAmount() }
    }
}