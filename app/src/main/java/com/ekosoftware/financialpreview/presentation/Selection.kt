package com.ekosoftware.financialpreview.presentation

data class Selection(val type: Type, var queryText: String = "")
{
    enum class Type {
        ACCOUNTS,
        BUDGETS,
        BUDGETS_SETTLE,
        CATEGORIES,
        MOVEMENTS,
        SETTLE_GROUPS
    }
}