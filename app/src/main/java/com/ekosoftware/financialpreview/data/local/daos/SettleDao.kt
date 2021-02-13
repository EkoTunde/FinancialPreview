package com.ekosoftware.financialpreview.data.local.daos

import android.util.Log
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.util.duplicate
import com.ekosoftware.financialpreview.util.newId
import com.ekosoftware.financialpreview.util.plusMonths
import java.util.*

@Dao
interface SettleDao {

    /**
     * Performs operation for settling a movement in a single transaction. It includes
     * the insertion [Insert] of a new [Record], updates to the [Account]'s balance and
     * the inserts, updates and deletions of movements, depending on whether the [Movement.leftAmount]
     * is paid in total or partially, and whether is a one-time movement or another with some frequency.
     *
     * @param record new [Record] to be inserted.
     * @param movement original [Movement] which is settling.
     * @param fromTo the year an month in [Int] format (e.g.: 202101 as in Jan-2021) when the settling operation is done.
     */
    @Transaction
    suspend fun settleMovement(record: Record, fromTo: Int) {
        // Adds the new record and update account's balance
        addRecord(record)
        updateAccount(record.accountId, record.amount)
        val movement = record.movement!!
        when {
            // Partial settling of a movement with pending transactions in upcoming months
            record.amount != movement.leftAmount && fromTo != movement.frequency!!.to!! -> {
                updateMovement(movement.apply { this@apply.frequency?.from = frequency?.from?.plusMonths(1) })
                addMovement(movement.duplicate().apply {
                    frequency?.from = fromTo
                    frequency?.to = fromTo
                    leftAmount = (movement.leftAmount - record.amount)
                })
            }
            // Partial settling of a last-month/one-time movement
            record.amount != movement.leftAmount && fromTo == movement.frequency!!.to!! -> updateMovement(movement.apply {
                leftAmount = (movement.leftAmount - record.amount)
            })

            // Total amount was settled and there are repetitions in upcoming months
            record.amount == movement.leftAmount && fromTo != movement.frequency!!.to!! -> updateMovement(movement.apply {
                frequency?.from = frequency?.from?.plusMonths(1)
            })


            // Total amount for last month was settled
            record.amount == movement.leftAmount && fromTo == movement.frequency!!.to!! -> deleteMovement(movement)
            else -> throw IllegalStateException("Movement $movement and Record $record at $fromTo yearMonth don't fit in any condition at ${this.javaClass.name}")
        }
    }


    /**
     * All movements must have an account id in order to be cancelled in a settle group.
     * That way, the operation should succeed.
     */
    @Transaction
    suspend fun settleSettleGroup(fromTo: Int, date: Date, movements: List<Movement>) { // -> BACKGROUND
        movements.forEach { movement ->
            val record = Record(
                id = newId(),
                date = date,
                name = null,
                movement = movement,
                amount = movement.leftAmount,
                currencyCode = movement.currencyCode,
                accountId = movement.accountId!!,
                currentPendingMovementId = null,
                accountIdOut = null,
                categoryId = movement.categoryId,
                debtorName = null,
                lenderName = null,
                description = movement.description,
                budgetId = movement.budgetId
            )
            settleMovement(record, fromTo)
        }
    }

    @Transaction
    suspend fun settleSimpleRecord(record: Record) {
        addRecord(record)
        updateAccount(record.accountId, record.amount)
    }

    @Transaction
    suspend fun settleBudgetRecord(record: Record, budget: Budget, fromTo: Int) {
        addRecord(record)
        updateAccount(record.accountId, record.amount)
        when {
            // Partial settling of a budget with pending transactions in upcoming months
            record.amount != budget.leftAmount && fromTo != budget.frequency!!.to!! -> {
                addBudget(budget.duplicate().apply {
                    frequency?.from = fromTo
                    frequency?.to = fromTo
                    leftAmount = if ((budget.leftAmount - record.amount) < 0L) 0L else budget.leftAmount - record.amount
                })
                updateBudget(budget.apply {
                    frequency?.from = frequency?.from?.plusMonths(1)
                })
            }
            // Partial settling of a last-month/one-time budget
            record.amount != budget.leftAmount && fromTo == budget.frequency!!.to!! -> updateBudget(budget.apply {
                leftAmount = if ((budget.leftAmount - record.amount) < 0L) 0L else budget.leftAmount - record.amount
            })

            // Total amount was settled and there are repetitions in upcoming months
            record.amount == budget.leftAmount && fromTo != budget.frequency!!.to!! -> updateBudget(budget.apply {
                frequency?.from = frequency?.from?.plusMonths(1)
            })

            // Total amount for one-time/last-month was settled
            record.amount == budget.leftAmount && fromTo == budget.frequency!!.to!! -> deleteBudget(budget)
            else -> throw IllegalStateException("Budget $budget and Record $record at $fromTo yearMonth don't fit in any condition at ${this.javaClass.name}")
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecord(record: Record)

    @Query("UPDATE accounts SET accountBalance = (accountBalance + :amount) WHERE accountId = :accountId")
    suspend fun updateAccount(accountId: String, amount: Long)

    @Query("SELECT * FROM movements WHERE movementId = :movementId LIMIT 1")
    fun getMovement(movementId: String): Movement

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovement(movement: Movement)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovement(movement: Movement)

/*
@Query("UPDATE movements SET ")
    suspend fun updateMovement(movement: Movement)
 */

    @Delete
    suspend fun deleteMovement(movement: Movement)

    @Query("SELECT * FROM budgets WHERE budgetId =:budgetId LIMIT 1")
    fun getBudget(budgetId: String): Budget

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBudget(budget: Budget)

    /**
     * Updates whole [Budget]. Called when there is a need to update [Budget]'s frequency.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBudget(budget: Budget)

    /**
     * Updates [Budget]'s left amount
     */
    @Query("UPDATE budgets SET budgetLeftAmount = (budgetLeftAmount + :amount) WHERE budgetId = :budgetId")
    suspend fun updateBudget(budgetId: String, amount: Long)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    /**
     * Updates [Budget]'s left amount taking out a movement's starting amount
     */
    @Query("UPDATE budgets SET budgetLeftAmount = (budgetLeftAmount - :amount) WHERE budgetId = :budgetId")
    suspend fun takeOutMovementLeftAmountFromBudget(budgetId: String, amount: Long)

    /**
     * Updates [Budget]'s left amount adding a movement's starting amount
     */
    @Query("UPDATE budgets SET budgetLeftAmount = (budgetLeftAmount + :amount) WHERE budgetId = :budgetId")
    suspend fun addMovementLeftAmountFromBudget(budgetId: String, amount: Long)
}