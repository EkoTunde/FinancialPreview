package com.ekosoftware.financialpreview.data.model.summary

import java.util.*

data class QuickViewSummary(
    val currencyCode: String,
    val months: List<String>,
    val incomes: List<Double>,
    val expenses: List<Double>,
    val balances: List<Double>,
    val accumulatedBalances: List<Double>
)
