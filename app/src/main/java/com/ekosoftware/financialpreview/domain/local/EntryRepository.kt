package com.ekosoftware.financialpreview.domain.local

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.currency.Currency
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
    private val currencyDao: CurrencyDao,
    private val movementDao: MovementDao,
    private val recordDao: RecordDao,
    private val settleGroupDao: SettleGroupDao,
) {

    companion object {
        private const val TAG = "EntryRepository"
    }

    suspend fun <T : Any> insert(obj: T) {
        when (obj) {
            is Account -> {
                (obj as Account).apply { id = newId() }
                accountDao.insert(obj)
            }
            is Budget -> {
                (obj as Budget).apply { id = newId() }
                budgetDao.insert(obj)
            }
            is Category -> {
                (obj as Category).apply { id = newId() }
                categoryDao.insert(obj)
            }
            is Currency -> {
                currencyDao.insert((obj as Currency))
            }
            is Movement -> {
                (obj as Movement).apply { id = newId() }
                movementDao.insert(obj)
            }
            is Record -> {
                (obj as Record).apply { id = newId() }
                recordDao.insert(obj)
            }
            is SettleGroup -> {
                (obj as SettleGroup).apply { id = newId() }
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
        Log.d(TAG, "update: UDPATE $obj")
        return when (obj) {
            is Account -> accountDao.update(obj as Account)
            is Budget -> budgetDao.update(obj as Budget)
            is Category -> categoryDao.update(obj as Category)
            is Movement -> movementDao.update(obj as Movement)
            is Record -> recordDao.update(obj as Record)
            is SettleGroup -> settleGroupDao.update(obj as SettleGroup)
            else -> {
            }
        }
    }

    suspend fun <T : Any> delete(obj: T) {
        return when (obj) {
            is Account -> accountDao.delete(obj as Account)
            is Budget -> budgetDao.delete(obj as Budget)
            is Category -> categoryDao.delete(obj as Category)
            is Movement -> movementDao.delete(obj as Movement)
            is Record -> recordDao.delete(obj as Record)
            is SettleGroup -> settleGroupDao.delete(obj as SettleGroup)
            else -> {
            }
        }
    }

    suspend fun deleteWithId(id: String) = movementDao.deleteWithId(id)

    fun getAccount(id: String): LiveData<Account> = accountDao.getAccount(id)

    fun getBudget(id: String): LiveData<Budget> = budgetDao.getBudget(id)

    fun getCategory(id: String): LiveData<Category> = categoryDao.getCategory(id)

    fun getMovement(id: String): Movement = movementDao.getMovement(id)

    fun getRecord(id: String): LiveData<Record> = recordDao.getRecord(id)

    fun getSingleSettleGroupWithMovements(id: String) = settleGroupDao.getSingleSettleGroupWithMovements(id)

    fun getAccountName(id: String): LiveData<String> = accountDao.getAccountName(id)

    fun getBudgetName(id: String): LiveData<String> = budgetDao.getBudgetName(id)

    fun getCategoryName(id: String): LiveData<String> = categoryDao.getCategoryName(id)

    fun getCurrencyCode(id: String): LiveData<String> = currencyDao.getCurrencyCode(id)

    fun getAccountCurrencyCode(accountId: String): LiveData<String> = accountDao.getCurrencyCodeForAccountId(accountId)
}