package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.Movement
import org.joda.time.LocalDate
import java.util.*

@Dao
interface MovementDao {

    @Query(
        """
        SELECT * 
        FROM movement_table 
        WHERE movement_date BETWEEN :fromDate AND :toDate
        ORDER BY movement_date DESC 
        LIMIT :limit"""
    )
    fun getMovements(
        fromDate: Date,
        toDate: Date,
        limit: Int
    ): LiveData<List<Movement>>

    /**
     * Performs a query on [Movement]s with multiple conditions.
     *
     * @param accountIds query condition.
     * @param currenciesCodes query condition.
     * @param from starting date to condition query's result.
     * @param to ending date to condition query's result.
     * @param search string indicating movement's name or description, or person's name to condition query's result.
     *
     * @return a live list of movements.
     */
    @Query(
        """
        SELECT * 
        FROM movement_table 
        WHERE movement_account_id IN (:accountIds)
        AND movement_currency_code IN (:currenciesCodes)
        AND (movement_date BETWEEN :from AND :to)
        AND (
          movement_old_scheduled_name LIKE '%' || :search || '%' 
            OR movement_old_scheduled_description LIKE '%' || :search || '%' 
            OR movement_old_scheduled_debtor LIKE '%' || :search || '%' 
            OR movement_old_scheduled_creditor LIKE '%' || :search || '%'
        )
        ORDER BY movement_date
        """
    )
    fun searchMovements(
        accountIds: List<Int>,
        currenciesCodes: List<String>,
        from: Date = LocalDate().minusDays(30).toDate(),
        to: Date = Date(),
        search: String,
    ): LiveData<List<Movement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(vararg movement: Movement)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovement(vararg movement: Movement)

    @Delete
    suspend fun deleteMovement(vararg movement: Movement)

}