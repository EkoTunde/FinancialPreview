package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.presentation.SimpleQueryData

@Dao
interface BudgetDao {

    @Query(
        """
        SELECT * FROM budgets
        
        WHERE budgetCurrencyCode = :currencyCode AND budgetFreqFrom <= :fromTo AND budgetFreqTo >= :fromTo
        AND (budgetName LIKE '%' || :searchPhrase || '%' OR budgetDescription LIKE '%' || :searchPhrase || '%')
        ORDER BY budgetName
    """
    )
    fun getBudgets(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<Budget>>

    @Query(
        """
        SELECT budgetId AS id,
        budgetName AS name,
        budgetCurrencyCode AS currencyCode,
        budgetLeftAmount AS amount
        FROM budgets
        WHERE budgetName LIKE '%' || :searchPhrase || '%' 
        OR budgetDescription LIKE '%' || :searchPhrase || '%'
        OR budgetCurrencyCode LIKE '%' || :searchPhrase || '%'
    """
    )
    fun getBudgetsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>
}