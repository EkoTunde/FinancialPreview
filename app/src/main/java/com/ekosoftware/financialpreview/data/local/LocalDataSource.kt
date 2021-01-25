package com.ekosoftware.financialpreview.data.local

import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.account.Account
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val currencyConversionDao: CurrencyConversionDao,
    private val recordDao: RecordDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao
) {

    // Traer una lista de cuentas
    //fun getAccountBalances() = accountDao.getAccounts()

    // Traer una lista de movimientos
    /*fun getMovements(fromDate: Date, toDate: Date, limit: Int) =
        recordDao.getMovements(fromDate, toDate, limit)*/

    // Saldos totales para diferentes monedas (corriente mes)


    // Gastos programadas limitados a lo indicado


    // Presupuestos y lo que queda de ellos

    // Impuestos

    // Promedio categorias

    // Saldos total anual (para una moneda en particular)

    // Saldos totales mensuales para los proximos 24 meses

    // Buscar todas las cuentas

    // Insertar cuenta
    suspend fun insertAccount(vararg account: Account) = accountDao.insertAccounts(*account)

    // Editar cuenta

    // Eliminar cuenta

    // Buscar todas las categorias

    // Insertar categoria

    // Modificar categoria

    // Eliminar categoria

    // Buscar todas las monedas

    // Insertar moneda
    //fun addCurrency(vararg currency: Currency) = currencyDao.addCurrency()

    // Modificar moneda

    // Eliminar moneda

    // Buscar impuestos

    // Insertar impuestos

    // Modificar impuestos

    // Eliminar impuestos

    // Buscar movimientos por cuenta, limitado por dias

    // Insertar movimientos
    suspend fun insertMovement(vararg record: Record) = recordDao.insertRecord(*record)

    // Modificar movimientos

    // Eliminar movimientos

    // Buscar programados por moneda

    // Buscar programados por mes

    // Buscar programados por categoria

    // Insertar programados

    // Modificar programados

    // Eliminar programados
}