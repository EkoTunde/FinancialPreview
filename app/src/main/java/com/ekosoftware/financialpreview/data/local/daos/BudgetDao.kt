package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.data.model.budget.Budget

@Dao
interface BudgetDao {

    @Query(
        """
        SELECT * FROM budgetTable
        
        WHERE budgetCurrencyCode = :currencyCode AND budgetFrom <= :fromTo AND budgetTo >= :fromTo
        AND (budgetName LIKE '%' || :searchPhrase || '%' OR budgetDescription LIKE '%' || :searchPhrase || '%')
        ORDER BY budgetName
    """
    )
    fun getBudgets(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<Budget>>

}