package com.ekosoftware.financialpreview.domain.local

import com.ekosoftware.financialpreview.data.local.daos.BudgetDao
import com.ekosoftware.financialpreview.data.local.daos.MovementDao
import com.ekosoftware.financialpreview.data.local.daos.RecordDao
import com.ekosoftware.financialpreview.data.local.daos.SettleGroupDao
import javax.inject.Inject

class DetailsRepository @Inject constructor(
    private val movementDao: MovementDao,
    private val budgetDao: BudgetDao,
    private val settleGroupDao: SettleGroupDao,
    private val recordDao: RecordDao
) {

    fun getMovement(id: String) = movementDao.getMovementUI(id)


}