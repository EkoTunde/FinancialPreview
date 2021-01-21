package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.data.model.budget.Budget

@Dao
interface BudgetDao {

    @Query("""
        SELECT 
            movementId AS movementId, movementLeftAmount as leftAmount, movementCurrencyCode as currencyCode, 
            movementName as name, categoryName as categoryName, categoryIconResId as categoryIconResId,
            categoryColorResId as categoryColorResId, movementFrom as fromYearMonth, movementTo as toYearMonth, movementTotalInstallments as totalInstallments 
        FROM movementTable
        INNER JOIN categoryTable ON movementCategoryId = categoryId
        WHERE movementCurrencyCode = :currencyCode AND movementFrom <= :fromTo AND movementTo >= :fromTo
        AND (movementName LIKE '%' || :searchPhrase || '%' OR movementDescription LIKE '%' || :searchPhrase || '%')
        ORDER BY leftAmount
    """)
    fun getBudgets(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<Budget>

}