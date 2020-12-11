package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.tax.Tax
import com.ekosoftware.financialpreview.data.model.tax.TaxWithScheduled

@Dao
interface TaxDao {

    @Query("SELECT * FROM tax_table")
    fun getTaxes(): LiveData<List<Tax>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxes(vararg tax: Tax)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTaxes(vararg tax: Tax)

    @Delete
    suspend fun deleteTaxes(vararg tax: Tax)

}