package com.ekosoftware.financialpreview.repository

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.BudgetDao
import com.ekosoftware.financialpreview.data.local.daos.MovementDao
import com.ekosoftware.financialpreview.data.local.daos.RecordDao
import com.ekosoftware.financialpreview.data.local.daos.SettleGroupDao
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.record.RecordUI
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import javax.inject.Inject

class DetailsRepository @Inject constructor(
    private val movementDao: MovementDao,
    private val budgetDao: BudgetDao,
    private val settleGroupDao: SettleGroupDao,
    private val recordDao: RecordDao
) {

    fun getMovement(id: String): LiveData<MovementUI> = movementDao.getMovementUI(id)

    fun getBudget(id: String): LiveData<Budget> = budgetDao.getLiveBudget(id)

    fun getSingleSettleGroup(id: String): LiveData<SettleGroup> = settleGroupDao.getSingleSettleGroup(id)

    fun getSimpleMovementsForSettleGroupId(settleGroupId: String): LiveData<List<SimpleQueryData>> =
        movementDao.getMovementsForSettleGroup(settleGroupId)

    suspend fun addSettleGroupMovementCrossRef(settleGroupId: String, movementId: String) {
        settleGroupDao.insertSettleGroupMovementsCrossRef(SettleGroupMovementsCrossRef(settleGroupId, movementId))
    }

    fun getRecordUI(id: String): LiveData<RecordUI> = recordDao.getRecordsUI(id)

}