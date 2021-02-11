package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.movement.Movement
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
        amountMax: Long = 1_000_000_000_000,
        amountMin: Long = -1_000_000_000_000,
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
            r.recordName AS name,
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

    /*@Query(
        """
        SELECT *
        FROM registryTable
        WHERE recordDate BETWEEN :fromDate AND :toDate
        ORDER BY recordDate DESC
        LIMIT :limit"""
    )
    fun getMovements(
        fromDate: Date,
        toDate: Date,
        limit: Int
    ): LiveData<List<Record>>

    *//*
     * Performs a query on [Record]s with multiple conditions.
     *
     * @param accountIds query condition.
     * @param currenciesCodes query condition.
     * @param from starting date to condition query's result.
     * @param to ending date to condition query's result.
     * @param search string indicating movement's name or description, or person's name to condition query's result.
     *
     * @return a live list of movements.
     *//*
    @Query(
        """
        SELECT * 
        FROM registryTable 
        WHERE recordAccountId IN (:accountIds)
        AND recordCurrencyCode IN (:currenciesCodes)
        AND (recordDate BETWEEN :from AND :to)
        AND (
          recordOld_movementName LIKE '%' || :search || '%' 
            OR recordOld_movementDescription LIKE '%' || :search || '%'
        )
        ORDER BY recordDate
        """
    )
    fun searchMovements(
        accountIds: List<Int>,
        currenciesCodes: List<String>,
        from: Date = LocalDate().minusDays(30).toDate(),
        to: Date = Date(),
        search: String,
    ): LiveData<List<Record>>*/

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(vararg record: Record)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRecord(vararg record: Record)

    @Delete
    suspend fun deleteRecord(vararg record: Record)*/


    @Query(
        """
        SELECT * FROM records r
        ORDER BY 
            CASE WHEN :orderBy = 'amountAsc' THEN r.recordAmount END ASC,
            CASE WHEN :orderBy = 'amountDesc' THEN r.recordAmount END DESC,
            CASE WHEN :orderBy = 'dateAsc' THEN r.recordDate END ASC,
            CASE WHEN :orderBy = 'dateDesc' THEN r.recordDate END DESC,
            CASE WHEN :orderBy = 'nameAsc' THEN r.recordName END ASC,
            CASE WHEN :orderBy = 'nameDesc' THEN r.recordName END DESC
            """
    )
    fun a(orderBy: String): List<Record>


    @Query(
        """
        SELECT * FROM movements m
        ORDER BY 
            CASE WHEN :orderBy = 'amountAsc' THEN m.movementLeftAmount END ASC,
            CASE WHEN :orderBy = 'amountDesc' THEN m.movementLeftAmount END DESC,
            CASE WHEN :orderBy = 'dateAsc' THEN m.movementId END ASC,
            CASE WHEN :orderBy = 'dateDesc' THEN m.movementId END DESC,
            CASE WHEN :orderBy = 'nameAsc' THEN m.movementName END ASC,
            CASE WHEN :orderBy = 'nameDesc' THEN m.movementName END DESC
            """
    )
    fun b(orderBy: String): List<Movement>
}