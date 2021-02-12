package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import com.ekosoftware.financialpreview.data.model.budget.Budget

@Dao
interface BudgetDao : BaseDao<Budget> {

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

    @Query("SELECT * FROM budgets WHERE budgetId = :id LIMIT 1")
    fun getBudget(id: String): Budget

    @Query("SELECT * FROM budgets WHERE budgetId = :id LIMIT 1")
    fun getLiveBudget(id: String): LiveData<Budget>

    @Query("SELECT budgetName FROM budgets WHERE budgetId = :id")
    fun getBudgetName(id: String): LiveData<String>

    @Query("DELETE FROM budgets WHERE budgetId = :budgetId")
    suspend fun deleteWithId(budgetId: String): Int

    @Query(
        """
        SELECT budgetId AS id, budgetName AS name, NULL AS currencyCode, NULL AS amount,
        NULL AS typeId, budgetDescription AS description, NULL AS color, NULL AS iconResId 
        FROM budgets
        WHERE budgetName LIKE '%' || :searchPhrase || '%' 
        OR budgetDescription LIKE '%' || :searchPhrase || '%'
        ORDER BY budgetName
    """
    )
    fun getBudgetsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>
}