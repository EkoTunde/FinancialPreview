package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.scheduled.Budget
import com.ekosoftware.financialpreview.data.model.scheduled.Scheduled
import com.ekosoftware.financialpreview.data.model.scheduled.ScheduledSummary

@Dao
interface ScheduledDao {

    @Query("SELECT * FROM scheduled_table WHERE scheduled_currency_code = :currencyCode")
    fun getScheduledByCurrency(currencyCode: String): LiveData<List<Scheduled>>

    @Query(
        """
        SELECT scheduledId, scheduled_left_amount, scheduled_currency_code, category_name, category_icon_res_id, account_name  
        FROM scheduled_table
        INNER JOIN category_table ON category_table.category_id = scheduled_table.scheduled_category_id
        INNER JOIN account_table ON account_table.account_id = scheduled_table.scheduled_account_id
        WHERE scheduled_from >= :from AND scheduled_to <= :to 
        ORDER BY category_name ASC, scheduled_currency_code, account_name
        """
    )
    fun getSchedulesSummaries(from: Int, to: Int): LiveData<List<ScheduledSummary>>

    @Query(
        """
        SELECT scheduledId, scheduled_left_amount, scheduled_currency_code, category_name, category_icon_res_id, account_name  
        FROM scheduled_table
        INNER JOIN category_table ON category_table.category_id = scheduled_table.scheduled_category_id
        INNER JOIN account_table ON account_table.account_id = scheduled_table.scheduled_account_id
        WHERE scheduled_from >= :from AND scheduled_to <= :to AND scheduled_currency_code = :currencyCode
        ORDER BY category_name ASC, scheduled_currency_code, account_name"""
    )
    fun getScheduledSummaryBetweenLapse(
        currencyCode: String,
        from: Int,
        to: Int
    ): LiveData<List<ScheduledSummary>>

    @Query(
        """
        SELECT scheduledId, scheduled_name, scheduled_currency_code, scheduled_left_amount, scheduled_starting_amount, category_name, category_icon_res_id, scheduled_budget_id
        FROM scheduled_table
        INNER JOIN category_table ON category_id = scheduled_category_id
        WHERE scheduled_is_budget = 1
        AND scheduled_category_id = :categoryId
        AND scheduled_from = :from
        AND scheduled_to = :to
        """
    )
    fun getBudgets(categoryId: Int, from: Int, to: Int): LiveData<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduled(vararg scheduled: Scheduled)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduled(vararg scheduled: Scheduled)

    @Delete
    suspend fun deleteScheduled(vararg scheduled: Scheduled)

}

