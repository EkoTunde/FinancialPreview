package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.CurrencyConversion

@Dao
interface CurrencyConversionDao {

    @Query("SELECT * FROM currencies")
    fun getCurrencies() : LiveData<List<CurrencyConversion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrency(vararg currencyConversion: CurrencyConversion)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrency(vararg currencyConversion: CurrencyConversion)

    @Delete
    suspend fun deleteCurrency(vararg currencyConversion: CurrencyConversion)

}