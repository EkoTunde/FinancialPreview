package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.data.model.summary.MonthSummary
import com.ekosoftware.financialpreview.data.model.summary.MovementSummary
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val currencyConversionDao: CurrencyConversionDao,
    private val recordDao: RecordDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao
) {

    fun getAccountsTotalForCurrency(currency: String): LiveData<Double?> =
        accountDao.getAccountsTotalForCurrency(currency)

    fun getLiveMonthSummary(fromTo: Int, currencyCode: String): LiveData<MonthSummary> =
        movementDao.getLiveMonthSummary(
            fromTo,
            currencyCode
        )

    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>> =
        settleGroupDao.getSettleGroupsWithMovements()

    fun getMonthPendingSummary(fromTo: Int, currencyCode: String): MonthSummary =
        movementDao.getMonthPendingSummary(fromTo, currencyCode)

    fun getMovementsSummaries(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<MovementSummary>> =
        movementDao.getMovementsSummaries(searchPhrase, currencyCode, fromTo)

    fun getBudgets(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ) : LiveData<List<Budget>> = budgetDao.getBudgets(searchPhrase, currencyCode, fromTo)

}