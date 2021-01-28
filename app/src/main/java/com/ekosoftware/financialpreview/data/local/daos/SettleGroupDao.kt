package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface SettleGroupDao {

    @Transaction
    @Query("SELECT * FROM settleGroups")
    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>>

    @Query(
        """
        SELECT settleGroupId AS id,
        settleGroupName AS name,
        settleGroupDescription AS description
        FROM settleGroups
        WHERE settleGroupName LIKE '%' || :searchPhrase || '%' 
        OR settleGroupDescription LIKE '%' || :searchPhrase || '%'
    """
    )
    fun getSettleGroupsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>
}