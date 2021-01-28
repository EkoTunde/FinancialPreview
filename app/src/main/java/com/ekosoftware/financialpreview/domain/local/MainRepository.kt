package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import com.ekosoftware.financialpreview.data.DummyData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.util.monthNameKey
import com.ekosoftware.financialpreview.util.plusMonths
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val currencyConversionDao: CurrencyConversionDao,
    private val recordDao: RecordDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao,
    private val someDao: SomeDao,
) {

    fun insertSettleGroups(vararg settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef) = someDao.insertSettleGroupCrossRef(*settleGroupMovementsCrossRef)

    fun getAccountsTotalForCurrency(currency: String): LiveData<Long?> =
        accountDao.getAccountsTotalForCurrency(currency)

    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>> =
        settleGroupDao.getSettleGroupsWithMovements()

    fun getMonthPendingSummary(fromTo: Int, currencyCode: String): MonthSummary =
        movementDao.getMonthPendingSummary(currencyCode, fromTo, fromTo.monthNameKey())

    fun getMovementsSummaries(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<MovementSummary>> = movementDao.getMovementsSummaries(
        searchPhrase,
        fromTo,
        currencyCode,
        fromTo.monthNameKey()
    )

    fun getBudgets(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<Budget>> = budgetDao.getBudgets(searchPhrase, currencyCode, fromTo)

}