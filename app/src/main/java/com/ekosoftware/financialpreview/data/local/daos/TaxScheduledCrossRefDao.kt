package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.TaxScheduledCrossRef
import com.ekosoftware.financialpreview.data.model.scheduled.ScheduledWithTaxes
import com.ekosoftware.financialpreview.data.model.tax.TaxWithScheduled

@Dao
interface TaxScheduledCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTaxScheduledRef(vararg taxScheduledCrossRef: TaxScheduledCrossRef)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTaxScheduledRef(vararg taxScheduledCrossRef: TaxScheduledCrossRef)

    @Delete
    suspend fun deleteTaxScheduledRef(vararg taxScheduledCrossRef: TaxScheduledCrossRef)

    @Transaction
    @Query("SELECT * FROM tax_table")
    fun getTaxesWithSchedules(): LiveData<List<TaxWithScheduled>>

    @Transaction
    @Query("SELECT * FROM scheduled_table")
    fun getSchedulesWithTaxes(): LiveData<List<ScheduledWithTaxes>>
}