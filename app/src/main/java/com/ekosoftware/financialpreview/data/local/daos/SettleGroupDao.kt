package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovementsCount
import java.util.*


@Dao
interface SettleGroupDao : BaseDao<SettleGroup> {

    @Transaction
    @Query("SELECT * FROM settleGroups")
    fun getSettleGroupsWithMovements(): LiveData<List<SettleGroupWithMovements>>

    @Transaction
    @Query("SELECT * FROM settleGroups WHERE settleGroupId = :id LIMIT 1")
    fun getSingleSettleGroupWithMovements(id: String): LiveData<SettleGroupWithMovements>

    @Query("SELECT * FROM settleGroups WHERE settleGroupId = :id LIMIT 1")
    fun getSingleSettleGroup(id: String): LiveData<SettleGroup>

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

    @Query("DELETE FROM settleGroups WHERE settleGroupId = :settleGroupId")
    suspend fun deleteWithId(settleGroupId: String): Int

    @Query("DELETE FROM settleGroupMovementsCrossRefTable WHERE settleGroupId = :settleGroupId")
    suspend fun deleteRefWithId(settleGroupId: String): Int

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

    /**
     * Queries for [SettleGroup]s which aren't related to specified movementId
     * in SimpleQueryData format.
     */
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
        WHERE settleGroupId NOT IN (
            SELECT settleGroupId AS groupId 
            FROM settleGroupMovementsCrossRefTable
            WHERE movementId = :movementId
        ) AND (
            settleGroupName LIKE '%' || :searchPhrase || '%' 
            OR settleGroupDescription LIKE '%' || :searchPhrase || '%'
            OR settleGroupPercentage LIKE '%' || :searchPhrase || '%'
        )
        ORDER BY settleGroupName
    """
    )
    fun getSettleGroupsAsSimpleDataForMovementSelection(searchPhrase: String, movementId: String): LiveData<List<SimpleQueryData>>

    @Query(
        """
                SELECT s.settleGroupId AS id,
                s.settleGroupName AS name, 
                s.settleGroupPercentage AS percentage, 
                s.settleGroupDescription As description, 
                COUNT(m.movementId) AS count 
                FROM settleGroups s
                LEFT JOIN settleGroupMovementsCrossRefTable cf 
                ON s.settleGroupId = cf.settleGroupId 
                LEFT JOIN (
                    SELECT * FROM movements                
                    WHERE movementLeftAmount < 0
                    AND movementCurrencyCode = :currencyCode 
                    AND movementFreqFrom <= :yearMonth 
                    AND movementFreqTo >= :yearMonth
                    AND movementFreqMonthsChecked LIKE '%' || :monthIncluded || '%'
                ) m ON m.movementId = cf.movementId
                
                GROUP BY settleGroupName
    """
    )
    fun getSettleGroupsWithMovementsCounts(
        currencyCode: String,
        yearMonth: Int,
        monthIncluded: String
    ): LiveData<List<SettleGroupWithMovementsCount>>
}