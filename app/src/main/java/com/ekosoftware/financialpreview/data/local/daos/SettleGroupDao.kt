package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import java.util.*

@Dao
interface SettleGroupDao {

    @Transaction
    @Query("SELECT * FROM settleGroupTable")
    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>>

}