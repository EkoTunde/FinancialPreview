package com.ekosoftware.financialpreview.repository

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.AccountDao
import com.ekosoftware.financialpreview.data.local.daos.MovementDao
import com.ekosoftware.financialpreview.data.local.daos.SettleDao
import com.ekosoftware.financialpreview.data.local.daos.SettleGroupDao
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import java.util.*
import javax.inject.Inject

class SettleRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val movementDao: MovementDao,
    private val settleDao: SettleDao,
    private val settleGroupDao: SettleGroupDao
) {

    fun getMovementUI(movementId: String): LiveData<MovementUI> = movementDao.getMovementUI(movementId)

    fun getMovement(movementId: String) = movementDao.getMovement(movementId)

    fun getSettleGroupWithMovement(settleGroupId: String) = settleGroupDao.getSingleSettleGroupWithMovements(settleGroupId)

    suspend fun settleMovement(record: Record, fromTo: Int) = settleDao.settleMovement(record, fromTo)

    suspend fun settleSettleGroup(fromTo: Int, date: Date, movements: List<Movement>) = settleDao.settleSettleGroup(fromTo, date, movements)

    suspend fun settleSimpleRecord(record: Record) = settleDao.settleSimpleRecord(record)

    suspend fun settleBudgetRecord() {
        // add record
        // update budget
        // create budget (create a duplicate with different frequency)
    }

    suspend fun settleAdjustmentToAccount(accountId: String, newValue: Long) {
        // add record
        // update account
    }

    suspend fun settleLoanDebt(record: Record, createCompanion: Boolean) {
        // add record
        // update account
        // add movement (if createCompanion is set to true)
    }

    suspend fun settleTransfer(record: Record) {
        // update account
        // update account
        // add record
    }

    suspend fun updateAccount(accountId: String, value: Long) {}
    suspend fun addRecord(vararg record: Record) {}
    suspend fun addMovement(vararg movement: Movement) {}
    suspend fun updateMovement(vararg movement: Movement) {}
    suspend fun deleteMovement(vararg movement: Movement) {}

    fun getAccount(accountId: String): LiveData<Account> = accountDao.getAccount(accountId)
}