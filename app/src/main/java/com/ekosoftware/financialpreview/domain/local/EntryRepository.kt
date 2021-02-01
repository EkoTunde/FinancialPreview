package com.ekosoftware.financialpreview.domain.local

import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import java.util.*
import javax.inject.Inject

class EntryRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val movementDao: MovementDao,
    private val recordDao: RecordDao,
    private val settleGroupDao: SettleGroupDao,
) {

    /*suspend fun insert(account: Account) = accountDao.insert(account)
    suspend fun insert(budget: Budget) = budgetDao.insert(budget)
    suspend fun insert(category: Category) = categoryDao.insert(category)
    suspend fun insert(movement: Movement) = movementDao.insert(movement)
    suspend fun insert(record: Record) = recordDao.insert(record)
    suspend fun insert(settleGroup: SettleGroup) = settleGroupDao.insert(settleGroup)*/


    suspend fun <T : Any> insert(obj: T) {

        return when (obj.javaClass) {
            Account::class -> {
                (obj as Account).id = newId()
                accountDao.insert(obj)
            }
            Budget::class -> {
                (obj as Budget).id = newId()
                budgetDao.insert(obj)
            }
            Category::class -> {
                (obj as Category).id = newId()
                categoryDao.insert(obj)
            }
            Movement::class -> {
                (obj as Movement).id = newId()
                movementDao.insert(obj)
            }
            Record::class -> {
                (obj as Record).id = newId()
                recordDao.insert(obj)
            }
            SettleGroup::class -> {
                (obj as SettleGroup).id = newId()
                settleGroupDao.insert(obj)
            }
            else -> {
            }
        }
    }

    private fun newId() = UUID.randomUUID().toString()

    suspend fun insertSettleGroupMovementRef(vararg settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef) {
        return settleGroupDao.insertSettleGroupMovementsCrossRef(*settleGroupMovementsCrossRef)
    }

    suspend fun <T : Any> update(obj: T) {
        return when (obj.javaClass) {
            Account::class -> accountDao.update(obj as Account)
            Budget::class -> budgetDao.update(obj as Budget)
            Category::class -> categoryDao.update(obj as Category)
            Movement::class -> movementDao.update(obj as Movement)
            Record::class -> recordDao.update(obj as Record)
            SettleGroup::class -> settleGroupDao.update(obj as SettleGroup)
            else -> {
            }
        }
    }

    suspend fun <T : Any> delete(obj: T) {
        return when (obj.javaClass) {
            Account::class -> accountDao.delete(obj as Account)
            Budget::class -> budgetDao.delete(obj as Budget)
            Category::class -> categoryDao.delete(obj as Category)
            Movement::class -> movementDao.delete(obj as Movement)
            Record::class -> recordDao.delete(obj as Record)
            SettleGroup::class -> settleGroupDao.delete(obj as SettleGroup)
            else -> {
            }
        }
    }

    fun getAccount(id: String) = accountDao.getAccount(id)

    fun getBudget(id: String) = budgetDao.getBudget(id)

    fun getCategory(id: String) = categoryDao.getCategory(id)

    fun getMovement(id: String) = movementDao.getMovement(id)

    fun getRecord(id: String) = recordDao.getRecord(id)

    fun getSingleSettleGroupWithMovements(id: String) = settleGroupDao.getSingleSettleGroupWithMovements(id)

    fun getAccountName(id: String) = accountDao.getAccountName(id)

    fun getBudgetName(id: String) = budgetDao.getBudgetName(id)

    fun getCategoryName(id: String) = categoryDao.getCategoryName(id)
}