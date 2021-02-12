package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.record.RecordUI
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
import java.util.*

@Dao
interface RecordDao : BaseDao<Record> {

    @Query(
        """
        SELECT 
            r.recordId AS id,
            r.recordDate AS date,
            r.recordAmount AS amount,
            r.recordCurrencyCode as currencyCode,
            r.recordName AS name,
            c.categoryName AS categoryName, 
            c.categoryIconResId AS categoryIconResId,  
            c.categoryColorResId AS categoryColorResId
        FROM records r 
        LEFT JOIN categories c ON recordCategoryId = categoryId
        WHERE recordAccountId = :accountId
        AND recordDate >= :topDate
        AND recordAmount BETWEEN :amountMin AND :amountMax 
        AND (
            (recordName LIKE '%' || :searchPhrase || '%') 
            OR (recordDescription LIKE '%' || :searchPhrase || '%')
            OR (recordCurrencyCode LIKE '%' || :searchPhrase || '%')
            OR (recordOld_movementName LIKE '%' || :searchPhrase || '%')
            OR (recordOld_movementDescription LIKE '%' || :searchPhrase || '%')
            OR (recordOld_movementCurrencyCode LIKE '%' || :searchPhrase || '%')
            OR (categoryName LIKE '%' || :searchPhrase || '%')
        )
         ORDER BY 
            CASE WHEN :orderBy = 'amountAsc' THEN r.recordAmount END ASC,
            CASE WHEN :orderBy = 'amountDesc' THEN r.recordAmount END DESC,
            CASE WHEN :orderBy = 'dateAsc' THEN r.recordDate END ASC,
            CASE WHEN :orderBy = 'dateDesc' THEN r.recordDate END DESC,
            CASE WHEN :orderBy = 'nameAsc' THEN r.recordName END ASC,
            CASE WHEN :orderBy = 'nameDesc' THEN r.recordName END DESC
    """
    )
    fun getRecordsUIShort(
        accountId: String,
        searchPhrase: String = "",
        topDate: Date,
        amountMax: Long = Long.MAX_VALUE,
        amountMin: Long = Long.MIN_VALUE,
        orderBy: String
    ): LiveData<List<RecordUIShort>>

    @Query("SELECT * FROM records WHERE recordId = :id")
    fun getRecord(id: String): LiveData<Record>

    @Query(
        """
        SELECT 
            r.recordId AS id,
            r.recordDate AS date,
            r.recordCurrencyCode as currencyCode,
            r.recordAmount AS amount, 
            r.recordOld_movementName AS name,
            c.categoryId AS categoryId, 
            c.categoryName AS categoryName, 
            c.categoryIconResId AS categoryIconResId,  
            c.categoryColorResId AS categoryColorResId,
            a.accountId AS accountId,
            a.accountName AS accountName,
            a.accountColorResId AS accountColorResId,
            account_out.accountName AS accountOutName,
            r.recordDebtorName AS debtorName,
            r.recordLenderName AS lenderName,
            r.recordDescription AS description
        FROM records r 
        LEFT JOIN categories c ON r.recordCategoryId = c.categoryId
        LEFT JOIN accounts a ON r.recordAccountId = a.accountId
        LEFT JOIN accounts account_out ON r.recordAccountIdOut = a.accountId
        WHERE recordId = :recordId
    """
    )
    fun getRecordsUI(
        recordId: String
    ): LiveData<RecordUI>
}