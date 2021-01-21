package com.ekosoftware.financialpreview.data.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.Record
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import java.util.*
import javax.inject.Inject

class NowRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val currencyConversionDao: CurrencyConversionDao,
    private val registryDao: RegistryDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao
) {

    // Traer una lista de cuentas
    fun getAccountBalances() = accountDao.getAccounts()

    // Traer una lista de movimientos
    fun getMovements(fromDate: Date, toDate: Date, limit: Int): LiveData<List<Record>> =
        registryDao.getMovements(fromDate, toDate, limit)

    // Saldos totales para diferentes monedas (corriente mes)
    fun getTotalForMonth(currencyId: String, from: Int, to: Int): LiveData<List<MovementSummary>> =
        movementDao.getScheduledSummaryBetweenLapse(
            currencyId,
            from,
            to
        )

    // Insertar cuenta
    suspend fun insertAccount(vararg account: Account) : Unit = accountDao.insertAccounts(*account)

    suspend fun updateAccount(vararg account: Account) : Unit = accountDao.updateAccounts(*account)

    suspend fun deleteAccount(vararg account: Account) : Unit = accountDao.deleteAccounts(*account)

    suspend fun insertMovement(vararg record: Record) : Unit = registryDao.insertMovement(*record)

    suspend fun updateMovement(vararg record: Record) : Unit = registryDao.updateMovement(*record)

    suspend fun deleteMovement(vararg record: Record) : Unit = registryDao.deleteMovement(*record)
}