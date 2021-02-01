package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.currency.Currency
import com.ekosoftware.financialpreview.presentation.SimpleQueryData

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
        WHERE currencyCode LIKE '%' || :searchPhrase || '%'
        ORDER BY currencyCode
    """)
    fun getCurrenciesAsSimpleData(searchPhrase: String) : LiveData<List<SimpleQueryData>>
}