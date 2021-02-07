package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovementsCount
import com.ekosoftware.financialpreview.util.monthNameKey
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val recordDao: RecordDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao,
    private val someDao: SomeDao,
) {

    fun insertSettleGroups(vararg settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef) =
        someDao.insertSettleGroupCrossRef(*settleGroupMovementsCrossRef)

    fun getAccountsTotalForCurrency(currency: String): LiveData<Long?> =
        accountDao.getAccountsTotalForCurrency(currency)

    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>> =
        settleGroupDao.getSettleGroupsWithMovements()

    fun getSettleGroups(): LiveData<List<SettleGroup>> =
        settleGroupDao.getSettleGroups()

    fun getMonthPendingSummary(fromTo: Int, currencyCode: String): MonthSummary =
        movementDao.getMonthPendingSummary(currencyCode, fromTo, fromTo.monthNameKey())

    fun getLiveMonthPendingSummary(fromTo: Int, currencyCode: String): LiveData<MonthSummary> =
        movementDao.getLiveMonthPendingSummaries(currencyCode, fromTo, fromTo.monthNameKey())

    fun getMovementsSummaries(
        searchPhrase: String,
        currencyCode: String,
        fromTo: Int
    ): LiveData<List<MovementUI>> = movementDao.getMovementsSummaries(
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

    fun getSettleGroupsWithMovementsCount(
        currencyCode: String,
        yearMonth: Int
    ): LiveData<List<SettleGroupWithMovementsCount>> =
        settleGroupDao.getSettleGroupsWithMovementsCounts(currencyCode, yearMonth, yearMonth.monthNameKey())
}