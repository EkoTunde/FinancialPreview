package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.data.model.summary.MonthSummary
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val currencyConversionDao: CurrencyConversionDao,
    private val registryDao: RegistryDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao
) {

    fun getAccountsTotalForCurrency(currency: String): LiveData<Double?> =
        accountDao.getAccountsTotalForCurrency(currency)

    fun getPendingSummaryWithoutTaxes(fromTo: Int, currencyCode: String): LiveData<MonthSummary> =
        movementDao.getPendingSummaryWithoutTaxes(
            fromTo,
            currencyCode
        )

    fun getTaxesForSettleGroups(): LiveData<List<SettleGroupWithMovements>> =
        settleGroupDao.getSettleGroupsWithMovements()


    fun getMonthPendingSummary(fromTo: Int, currencyCode: String): MonthSummary =
        movementDao.getMonthPendingSummary(fromTo, currencyCode)

}