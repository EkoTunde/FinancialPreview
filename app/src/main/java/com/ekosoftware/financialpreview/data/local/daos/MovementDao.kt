package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import com.ekosoftware.financialpreview.data.model.summary.Balance
import com.ekosoftware.financialpreview.data.model.summary.MonthSummary

@Dao
interface MovementDao {

    @Query(
        """
        SELECT 
            :currencyCode AS currencyCode,
            :fromTo AS yearMonth,
            (SELECT SUM(movementLeftAmount) 
            FROM movementTable 
            WHERE movementLeftAmount >= 0 AND movementCurrencyCode = :currencyCode AND movementFrom >= :fromTo AND movementTo <= :fromTo         
            ) AS totalIncome,
            (SELECT SUM(movementLeftAmount)
            FROM movementTable 
            WHERE movementLeftAmount < 0 AND movementCurrencyCode = :currencyCode AND movementFrom >= :fromTo AND movementTo <= :fromTo
            ) AS totalExpense
        """
    )
    fun getPendingSummaryWithoutTaxes(fromTo: Int, currencyCode: String): LiveData<MonthSummary>

    @Query(
        """
        SELECT :currencyCode AS currencyCode,
        :fromTo AS yearMonth, 
        (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementLeftAmount >= 0 AND movementCurrencyCode = :currencyCode AND movementFrom >= :fromTo AND movementTo <= :fromTo) AS totalIncome,
        (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementLeftAmount < 0 AND movementCurrencyCode = :currencyCode AND movementFrom >= :fromTo AND movementTo <= :fromTo) AS totalExpense
        """
    )
    fun getMonthPendingSummary(fromTo: Int, currencyCode: String): MonthSummary

    @Query(
        """
        SELECT :fromTo AS month,
            (SELECT SUM(movementLeftAmount) FROM movementTable WHERE movementCurrencyCode = :currencyCode AND movementFrom >= :fromTo AND movementTo <= :fromTo) AS balance"""
    )
    fun getBalance(currencyCode: String, fromTo: Int): LiveData<Balance>

    @Query(
        """
        SELECT movementId, movementLeftAmount, movementCurrencyCode, categoryName, categoryIconResId, accountName  
        FROM movementTable
        INNER JOIN categoryTable ON categoryTable.categoryId = movementTable.movementCategoryId
        INNER JOIN accountTable ON accountTable.accountId = movementTable.movementAccountId
        WHERE movementFrom >= :from AND movementTo <= :to AND movementCurrencyCode = :currencyCode
        ORDER BY categoryName ASC, movementCurrencyCode, accountName"""
    )
    fun getScheduledSummaryBetweenLapse(
        currencyCode: String,
        from: Int,
        to: Int
    ): LiveData<List<MovementSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduled(vararg movement: Movement)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduled(vararg movement: Movement)

    @Delete
    suspend fun deleteScheduled(vararg movement: Movement)

}

