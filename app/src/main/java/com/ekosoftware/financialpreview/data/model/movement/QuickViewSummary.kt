package com.ekosoftware.financialpreview.data.model.movement

data class QuickViewSummary(
    val currencyCode: String,
    val months: List<String>,
    val incomes: List<Long>,
    val expenses: List<Long>,
    val balances: List<Long>,
    val accumulatedBalances: List<Long>
)
