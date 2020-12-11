package com.ekosoftware.financialpreview.data.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.Movement
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.scheduled.ScheduledSummary
import java.util.*
import javax.inject.Inject

class NowRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val currencyConversionDao: CurrencyConversionDao,
    private val movementDao: MovementDao,
    private val scheduledDao: ScheduledDao,
    private val taxDao: TaxDao,
    private val taxScheduledCrossRefDao: TaxScheduledCrossRefDao
) {

    // Traer una lista de cuentas
    fun getAccountBalances() = accountDao.getAccounts()

    // Traer una lista de movimientos
    fun getMovements(fromDate: Date, toDate: Date, limit: Int): LiveData<List<Movement>> =
        movementDao.getMovements(fromDate, toDate, limit)

    // Saldos totales para diferentes monedas (corriente mes)
    fun getTotalForMonth(currencyId: String, from: Int, to: Int): LiveData<List<ScheduledSummary>> =
        scheduledDao.getScheduledSummaryBetweenLapse(
            currencyId,
            from,
            to
        )

    // Insertar cuenta
    suspend fun insertAccount(vararg account: Account) : Unit = accountDao.insertAccounts(*account)

    suspend fun updateAccount(vararg account: Account) : Unit = accountDao.updateAccounts(*account)

    suspend fun deleteAccount(vararg account: Account) : Unit = accountDao.deleteAccounts(*account)

    suspend fun insertMovement(vararg movement: Movement) : Unit = movementDao.insertMovement(*movement)

    suspend fun updateMovement(vararg movement: Movement) : Unit = movementDao.updateMovement(*movement)

    suspend fun deleteMovement(vararg movement: Movement) : Unit = movementDao.deleteMovement(*movement)
}