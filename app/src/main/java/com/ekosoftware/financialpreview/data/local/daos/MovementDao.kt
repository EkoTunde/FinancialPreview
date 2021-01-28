package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

@Dao
interface MovementDao {

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
     * data contained in [MovementSummary].
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
            movementId AS movementId, movementLeftAmount as leftAmount, movementCurrencyCode as currencyCode, 
            movementName as name, categoryName as categoryName, categoryIconResId as categoryIconResId,
            categoryColorResId as categoryColorResId, movementFreqFrom as fromYearMonth, movementFreqTo as toYearMonth, movementFreqInstallments as totalInstallments 
        FROM movements
        INNER JOIN categories ON movementCategoryId = categoryId
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
    ): LiveData<List<MovementSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduled(vararg movement: Movement)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduled(vararg movement: Movement)

    @Delete
    suspend fun deleteScheduled(vararg movement: Movement)

    @Query(
        """
        SELECT movementId AS id,
        movementName AS name,
        movementCurrencyCode AS currencyCode,
        movementLeftAmount AS amount,
        categories.categoryColorResId AS color,
        categories.categoryIconResId AS iconResId
        FROM movements
        INNER JOIN categories ON movementCategoryId = categoryId
        WHERE movementName LIKE '%' || :searchPhrase || '%' 
        OR movementDescription LIKE '%' || :searchPhrase || '%'
        OR movementCurrencyCode LIKE '%' || :searchPhrase || '%'
    """
    )
    fun getMovementsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>

    @Query("SELECT SUM(accountBalance) FROM accounts WHERE accountCurrencyCode = :currency")
    fun getAccountsTotalForCurrency(currency: String): LiveData<Double?>

    /*@Transaction
    @Query(
        """
        SELECT :currencyCode AS currencyCode,
        :yearMonth AS yearMonth,
        (SELECT SUM(accountBalance) FROM accounts WHERE accountCurrencyCode = :currencyCode) AS currentBalance,
        (
            SELECT SUM(movementLeftAmount) 
            FROM movements 
            WHERE movementLeftAmount >= 0 
            AND movementCurrencyCode = :currencyCode 
            AND movementFreqFrom <= :yearMonth 
            AND movementFreqTo >= :yearMonth
            AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS currentMonthMovementIncome,
        (
            SELECT SUM(budgetLeftAmount) 
            FROM budgets 
            WHERE budgetLeftAmount >= 0 
            AND budgetCurrencyCode = :currencyCode 
            AND budgetFreqFrom <= :yearMonth 
            AND budgetFreqTo >= :yearMonth
            AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS currentMonthBudgetIncome,
        (
            SELECT SUM(movementLeftAmount) 
            FROM movements 
            WHERE movementLeftAmount < 0 
            AND movementCurrencyCode = :currencyCode 
            AND movementFreqFrom <= :yearMonth 
            AND movementFreqTo >= :yearMonth
            AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS currentMonthMovementExpense,
        (
            SELECT SUM(budgetLeftAmount) 
            FROM budgets 
            WHERE budgetLeftAmount < 0 
            AND budgetCurrencyCode = :currencyCode 
            AND budgetFreqFrom <= :yearMonth 
            AND budgetFreqTo >= :yearMonth
            AND budgetFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
        ) AS currentMonthBudgetExpense
        """
    )
    fun getBigQuery(currencyCode: String, yearMonth: Int, monthIncluded: String): LiveData<BQ>*/


}

data class BQ(
    val currencyCode: String,
    val yearMonth: Int,
    val currentBalance: Long = 0,
    val currentMonthMovementIncome: Long? = 0,
    val currentMonthMovementExpense: Long? = 0,
    val currentMonthBudgetIncome: Long? = 0,
    val currentMonthBudgetExpense: Long? = 0,
)