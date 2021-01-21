package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.summary.MonthSummary
import com.ekosoftware.financialpreview.data.model.summary.MovementSummary

@Dao
interface MovementDao {

    @Query(
        """
        SELECT 
            :currencyCode AS currencyCode,
            :fromTo AS yearMonth,
            (
                (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementLeftAmount >= 0 AND movementCurrencyCode = :currencyCode AND movementFrom <= :fromTo AND movementTo >= :fromTo) +
                (SELECT SUM(budgetLeftAmount) FROM budgetTable WHERE budgetLeftAmount >= 0 AND budgetCurrencyCode = :currencyCode AND budgetFrom <= :fromTo AND budgetTo >= :fromTo) 
            ) AS totalIncome,
            (
                (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementLeftAmount < 0 AND movementCurrencyCode = :currencyCode AND movementFrom <= :fromTo AND movementTo >= :fromTo) +
                (SELECT SUM(budgetLeftAmount) FROM budgetTable WHERE budgetLeftAmount < 0 AND budgetCurrencyCode = :currencyCode AND budgetFrom <= :fromTo AND budgetTo >= :fromTo) 
            ) AS totalExpense
        """
    )
    fun getLiveMonthSummary(fromTo: Int, currencyCode: String): LiveData<MonthSummary>

    @Query(
        """
        SELECT :currencyCode AS currencyCode,
        :fromTo AS yearMonth, 
        (
            (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementLeftAmount >= 0 AND movementCurrencyCode = :currencyCode AND movementFrom <= :fromTo AND movementTo >= :fromTo) +
            (SELECT SUM(budgetLeftAmount) FROM budgetTable WHERE budgetLeftAmount >= 0 AND budgetCurrencyCode = :currencyCode AND budgetFrom <= :fromTo AND budgetTo >= :fromTo)
        ) AS totalIncome,
        (
            (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementLeftAmount < 0 AND movementCurrencyCode = :currencyCode AND movementFrom <= :fromTo AND movementTo >= :fromTo) +
            (SELECT SUM(budgetLeftAmount) FROM budgetTable WHERE budgetLeftAmount < 0 AND budgetCurrencyCode = :currencyCode AND budgetFrom <= :fromTo AND budgetTo >= :fromTo)
        ) AS totalExpense
        """
    )
    fun getMonthPendingSummary(fromTo: Int, currencyCode: String): MonthSummary

    @Query(
        """
        SELECT 
            movementId AS movementId, movementLeftAmount as leftAmount, movementCurrencyCode as currencyCode, 
            movementName as name, categoryName as categoryName, categoryIconResId as categoryIconResId,
            categoryColorResId as categoryColorResId, movementFrom as fromYearMonth, movementTo as toYearMonth, movementTotalInstallments as totalInstallments 
        FROM movementTable
        INNER JOIN categoryTable ON movementCategoryId = categoryId
        WHERE movementCurrencyCode = :currencyCode AND movementFrom <= :fromTo AND movementTo >= :fromTo
        AND (movementName LIKE '%' || :searchPhrase || '%' OR movementDescription LIKE '%' || :searchPhrase || '%')
        ORDER BY leftAmount
    """
    )
    fun getMovementsSummaries(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<MovementSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduled(vararg movement: Movement)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduled(vararg movement: Movement)

    @Delete
    suspend fun deleteScheduled(vararg movement: Movement)

}

