package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface SettleGroupDao : BaseDao<SettleGroup> {

    @Transaction
    @Query("SELECT * FROM settleGroups")
    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>>

    @Transaction
    @Query("SELECT * FROM settleGroups WHERE settleGroupId = :id")
    fun getSingleSettleGroupWithMovements(id: String): LiveData<SettleGroupWithMovements>

    @Query("SELECT * FROM settleGroups")
    fun getSettleGroups(): LiveData<List<SettleGroup>>

    /**
     * Insert a "SettleGroup-Movement" relation in the database.
     *
     * @param settleGroupMovementsCrossRef the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSettleGroupMovementsCrossRef(settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef)

    /**
     * Insert an array of "SettleGroup-Movement" relation in the database.
     *
     * @param settleGroupMovementsCrossRef the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSettleGroupMovementsCrossRef(vararg settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef)

    /**
     * Update a "SettleGroup-Movement" relation from the database.
     *
     * @param settleGroupMovementsCrossRef the object to be updated
     */
    @Update
    suspend fun updateSettleGroupMovementsCrossRef(settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef)

    /**
     * Delete a "SettleGroup-Movement" relation from the database
     *
     * @param settleGroupMovementsCrossRef the object to be deleted
     */
    @Delete
    suspend fun delete(settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef)

    @Query(
        """
        SELECT 
            settleGroupId AS id, 
            settleGroupName AS name, 
            NULL AS currencyCode, 
            NULL AS amount,
            NULL AS typeId, 
            CAST(settleGroupPercentage AS description), 
            NULL AS color, 
            NULL AS iconResId
        FROM settleGroups
        WHERE settleGroupName LIKE '%' || :searchPhrase || '%' 
        OR settleGroupDescription LIKE '%' || :searchPhrase || '%'
        OR settleGroupPercentage LIKE '%' || :searchPhrase || '%'
        ORDER BY settleGroupName
    """
    )
    fun getSettleGroupsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>

    /* @Query(
         """
         SELECT s.*, SUM(m.movementLeftAmount)
         FROM settleGroups s
         JOIN settleGroupMovementsCrossRefTable cf
         ON s.settleGroupId = cf.settleGroupId
         JOIN movements m ON m.movementId = cf.movementId
         AND movementCurrencyCode = :currencyCode
         AND movementFreqFrom <= :fromTo
         AND movementFreqTo >= :fromTo
         AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
         GROUP BY s.settleGroupName
     """
     )
     fun g(
         fromTo: Int,
         currencyCode: String,
         monthIncluded: String
     ): LiveData<SettleGroupSummary>

     data class SettleGroupSummary(
         val settleGroup: SettleGroup,
         val movementsBalance: Long
     )

     @Query(
         """
         SELECT (SUM(total))/100
         FROM(
             SELECT SUM(m.movementLeftAmount) * s.settleGroupPercentage AS total
             FROM settleGroups s
             JOIN settleGroupMovementsCrossRefTable cf
             ON s.settleGroupId = cf.settleGroupId
             JOIN movements m ON m.movementId = cf.movementId
             WHERE movementLeftAmount < 0
             AND movementCurrencyCode = :currencyCode
             AND movementFreqFrom <= :fromTo
             AND movementFreqTo >= :fromTo
             AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
         )
     """
     )
     fun asdasdg(
         fromTo: Int,
         currencyCode: String,
         monthIncluded: String
     ): LiveData<SettleGroupSummary>*/
}