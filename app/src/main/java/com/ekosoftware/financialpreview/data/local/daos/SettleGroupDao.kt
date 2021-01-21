package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import java.util.*

@Dao
interface SettleGroupDao {

    @Transaction
    @Query("SELECT * FROM settleGroupTable")
    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>>
}