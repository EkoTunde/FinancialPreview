package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao : BaseDao<Movement> {

    /*@Transaction
    open fun updateData(users: List<User>) {
        deleteAllUsers()
        insertAll(users)
    }*/

    /**
     * Emits live results of performing a sum to
     * all movements and budget corresponding to
     * indicated month and year.
     *
     * @param fromTo the [Int] year and month to query in "yyyyMM" format.
     * @param currencyCode the [String] representing a currency, e.g.: "USD".
     * @param monthIncluded the [String] representing the month in lettered abbreviated format, e.g.: "Jan".
     *
     * @return [LiveData] object containing a [MonthSummary].
     */
    @Query(
        """
        SELECT 
            :currencyCode AS currencyCode,
            :fromTo AS yearMonth,
            (
                (
                    SELECT SUM(movementLeftAmount) 
                    FROM movements 
                    WHERE movementLeftAmount > 0 
                    AND movementCurrencyCode = :currencyCode 
                    AND :fromTo >= movementFreqFrom  
                    AND :fromTo <= movementFreqTo
                    AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) + (
                    SELECT SUM(budgetLeftAmount) 
                    FROM budgets 
                    WHERE budgetLeftAmount > 0 
                    AND budgetCurrencyCode = :currencyCode 
                    AND :fromTo >= budgetFreqFrom  
                    AND :fromTo <= budgetFreqTo
                    AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) 
            ) AS totalIncome, (
                (
                    SELECT SUM(movementLeftAmount) 
                    FROM movements 
                    WHERE movementLeftAmount < 0 
                    AND movementCurrencyCode = :currencyCode 
                    AND :fromTo >= movementFreqFrom  
                    AND :fromTo <= movementFreqTo
                    AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) + (
                    SELECT SUM(budgetLeftAmount) 
                    FROM budgets 
                    WHERE budgetLeftAmount < 0 
                    AND budgetCurrencyCode = :currencyCode 
                    AND :fromTo >= budgetFreqFrom  
                    AND :fromTo <= budgetFreqTo
                    AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) 
            ) AS totalExpense
        """
    )
    fun getLiveMonthSummary(
        fromTo: Int,
        currencyCode: String,
        monthIncluded: String
    ): LiveData<MonthSummary>

    @Query(
        """
        SELECT 
            :currencyCode AS currencyCode,
            :fromTo AS yearMonth,
            (
                (
                    SELECT SUM(movementLeftAmount) 
                    FROM movements 
                    WHERE movementLeftAmount >= 0 
                    AND movementCurrencyCode = :currencyCode 
                    AND movementFreqFrom <= :fromTo 
                    AND movementFreqTo >= :fromTo 
                    AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) + (
                    SELECT SUM(budgetLeftAmount) 
                    FROM budgets 
                    WHERE budgetLeftAmount >= 0 
                    AND budgetCurrencyCode = :currencyCode 
                    AND budgetFreqFrom <= :fromTo 
                    AND budgetFreqTo >= :fromTo
                    AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) 
            ) AS totalIncome, (
                (
                    SELECT SUM(movementLeftAmount) 
                    FROM movements 
                    WHERE movementLeftAmount < 0 
                    AND movementCurrencyCode = :currencyCode 
                    AND movementFreqFrom <= :fromTo 
                    AND movementFreqTo >= :fromTo
                    AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) + (
                    SELECT SUM(budgetLeftAmount) 
                    FROM budgets 
                    WHERE budgetLeftAmount < 0 
                    AND budgetCurrencyCode = :currencyCode 
                    AND budgetFreqFrom <= :fromTo 
                    AND budgetFreqTo >= :fromTo
                    AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) 
            ) AS totalExpense
        """
    )
    fun getLiveMonthSummary2(
        fromTo: Int,
        currencyCode: String,
        monthIncluded: String
    ): Flow<MonthSummary>

    /**
     * Similar to [getLiveMonthSummary], queries for the results of
     * performing a sum to all movements and budget corresponding to
     * indicated month and year, but doesn't return a [LiveData] observable.
     *
     * @param fromTo the [Int] year and month to query in "yyyyMM" format.
     * @param currencyCode the [String] representing a currency, e.g.: "USD".
     * @param monthIncluded the [String] representing the month in lettered abbreviated format, e.g.: "Jan".
     *
     * @return a [MonthSummary].
     *
     */
    @Query(
        """
        SELECT :currencyCode AS currencyCode,
        :fromTo AS yearMonth, 
        (
            (
                SELECT SUM(movementLeftAmount) 
                FROM movements 
                WHERE movementLeftAmount >= 0 
                AND movementCurrencyCode = :currencyCode 
                AND movementFreqFrom <= :fromTo 
                AND movementFreqTo >= :fromTo
                AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
            ) + (
                SELECT SUM(budgetLeftAmount) 
                FROM budgets 
                WHERE budgetLeftAmount >= 0 
                AND budgetCurrencyCode = :currencyCode 
                AND budgetFreqFrom <= :fromTo 
                AND budgetFreqTo >= :fromTo
                AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
            )
        ) AS totalIncome, (
            (
                SELECT SUM(movementLeftAmount) 
                FROM movements 
                WHERE movementLeftAmount < 0 
                AND movementCurrencyCode = :currencyCode 
                AND movementFreqFrom <= :fromTo 
                AND movementFreqTo >= :fromTo
                AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
            ) + (
                SELECT SUM(budgetLeftAmount) 
                FROM budgets 
                WHERE budgetLeftAmount < 0 
                AND budgetCurrencyCode = :currencyCode 
                AND budgetFreqFrom <= :fromTo 
                AND budgetFreqTo >= :fromTo
                AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
            )
        ) AS totalExpense
        """
    )
    fun getMonthPendingSummary(
        fromTo: Int,
        currencyCode: String,
        monthIncluded: String
    ): MonthSummary

    @Transaction
    @Query(
        """
        SELECT :currencyCode AS currencyCode,
        :yearMonth AS yearMonth,
        (
            SELECT SUM(movementLeftAmount) 
            FROM movements 
            WHERE movementLeftAmount >= 0 
            AND movementCurrencyCode = :currencyCode 
            AND movementFreqFrom <= :yearMonth 
            AND movementFreqTo >= :yearMonth
            AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS movementIncome,
        (
            SELECT SUM(budgetLeftAmount) 
            FROM budgets 
            WHERE budgetLeftAmount >= 0 
            AND budgetCurrencyCode = :currencyCode 
            AND budgetFreqFrom <= :yearMonth 
            AND budgetFreqTo >= :yearMonth
            AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS budgetIncome,
        (
            SELECT SUM(movementLeftAmount) 
            FROM movements 
            WHERE movementLeftAmount < 0 
            AND movementCurrencyCode = :currencyCode 
            AND movementFreqFrom <= :yearMonth 
            AND movementFreqTo >= :yearMonth
            AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS movementExpense,
        (
            SELECT SUM(budgetLeftAmount) 
            FROM budgets 
            WHERE budgetLeftAmount < 0 
            AND budgetCurrencyCode = :currencyCode 
            AND budgetFreqFrom <= :yearMonth 
            AND budgetFreqTo >= :yearMonth
            AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS budgetExpense
        """
    )
    fun getMonthPendingSummary(
        currencyCode: String,
        yearMonth: Int,
        monthIncluded: String
    ): MonthSummary

    /**
     * Queries for all [Movement]s in database but outputs only specified
     * data contained in [MovementUI].
     *
     * @param searchPhrase the name or description phrase to include as a condition to the query.
     * @param fromTo the [Int] year and month to query in "yyyyMM" format.
     * @param currencyCode the [String] representing a currency, e.g.: "USD".
     * @param monthIncluded the [String] representing the month in lettered abbreviated format, e.g.: "Jan".
     *
     * @return [LiveData] object containing a [List] of [MonthSummary].
     */
    @Query(
        """
        SELECT 
        movementId AS id, movementLeftAmount AS leftAmount, movementStartingAmount AS startingAmount,
        movementCurrencyCode AS currencyCode, movementName AS name, movementFreqFrom AS fromYearMonth, 
        movementFreqTo AS toYearMonth, movementFreqInstallments AS totalInstallments, movementFreqMonthsChecked as monthsChecked,
        movementAccountId as accountId, accountName as accountName, accountColorResId AS accountColorResId, 
        movementCategoryId as categoryId, categoryName AS categoryName, categoryIconResId AS categoryIconResId,
        categoryColorResId AS categoryColorResId, movementBudgetId as budgetId, budgetName AS budgetName
        FROM movements
        LEFT JOIN categories ON movementCategoryId = categoryId
        LEFT JOIN accounts ON movementAccountId = accountId
        LEFT JOIN budgets ON movementBudgetId = budgetId
        WHERE movementCurrencyCode = :currencyCode AND movementFreqFrom <= :fromTo AND movementFreqTo >= :fromTo AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        AND (movementName LIKE '%' || :searchPhrase || '%' OR movementDescription LIKE '%' || :searchPhrase || '%')
        ORDER BY leftAmount
    """
    )
    fun getMovementsSummaries(
        searchPhrase: String = "",
        fromTo: Int,
        currencyCode: String,
        monthIncluded: String
    ): LiveData<List<MovementUI>>

    @Query("""
        SELECT movementId AS id, movementLeftAmount AS leftAmount, movementStartingAmount AS startingAmount,
        movementCurrencyCode AS currencyCode, movementName AS name, movementFreqFrom AS fromYearMonth, 
        movementFreqTo AS toYearMonth, movementFreqInstallments AS totalInstallments, movementFreqMonthsChecked as monthsChecked,
        movementAccountId as accountId, accountName as accountName, accountColorResId AS accountColorResId, 
        movementCategoryId as categoryId, categoryName AS categoryName, categoryIconResId AS categoryIconResId,
        categoryColorResId AS categoryColorResId, movementBudgetId as budgetId, budgetName AS budgetName
        FROM movements
        LEFT JOIN accounts ON movementAccountId = accountId
        LEFT JOIN categories ON movementCategoryId = categoryId
        LEFT JOIN budgets ON movementBudgetId = budgetId
        WHERE movementId = :id
    """)
    fun getMovementUI(id: String) : LiveData<MovementUI>

    @Query("SELECT SUM(accountBalance) FROM accounts WHERE accountCurrencyCode = :currency")
    fun getAccountsTotalForCurrency(currency: String): LiveData<Double?>

    @Query("SELECT * FROM movements WHERE movementId = :id")
    fun getMovement(id: String): Movement

    @Query(
        """
        SELECT 
            movementId AS id, 
            movementName AS name, 
            movementCurrencyCode AS currencyCode, 
            movementLeftAmount AS amount,
            NULL AS typeId, 
            NULL AS description, 
            categories.categoryColorResId AS color, 
            categories.categoryIconResId AS iconResId
        FROM movements
        INNER JOIN categories ON movementCategoryId = categoryId
        WHERE movementName LIKE '%' || :searchPhrase || '%' 
        OR movementDescription LIKE '%' || :searchPhrase || '%'
        OR movementCurrencyCode LIKE '%' || :searchPhrase || '%'
        ORDER BY movementName
    """
    )
    fun getMovementsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>
}