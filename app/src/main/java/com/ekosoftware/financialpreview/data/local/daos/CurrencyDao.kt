package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import com.ekosoftware.financialpreview.data.model.currency.Currency

@Dao
interface CurrencyDao : BaseDao<Currency> {

    @Query("""
        SELECT 
            currencyCodeId AS id, 
            currencyCodeId AS name, 
            NULL as currencyCode, 
            NULL AS amount,
            NULL AS typeId, 
            NULL AS description, 
            NULL AS color, 
            NULL AS iconResId 
        FROM currencies
        WHERE currencyCodeId LIKE '%' || :searchPhrase || '%'
        ORDER BY currencyCode
    """)
    fun getCurrenciesAsSimpleData(searchPhrase: String) : LiveData<List<SimpleQueryData>>

    @Query("SELECT currencyCodeId FROM currencies WHERE currencyCodeId = :id")
    fun getCurrencyCode(id: String): LiveData<String>

}