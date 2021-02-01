package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.record.RecordSummary
import java.util.*

@Dao
interface RecordDao : BaseDao<Record> {

    @Query(
        """
        SELECT recordId AS id, recordDate AS date, recordAmount AS amount, recordCurrencyCode as currencyCode 
        ,recordName AS name, categoryName AS categoryName, categoryIconResId AS categoryIconResId,  
        categoryColorResId AS categoryColorResId
        FROM records INNER JOIN categories ON recordOld_movementCategoryId = categoryId
        WHERE recordAccountId = :accountId
        AND recordDate >= :topDate
        AND recordAmount BETWEEN :amountMin AND :amountMax
        ORDER BY recordDate
    """
    )
    fun getRecords(
        accountId: String,
        topDate: Date,
        amountMax: Double = -100_000.0,
        amountMin: Double = 100_000.0
    ): LiveData<List<RecordSummary>>

    @Query("SELECT * FROM records WHERE recordId = :id")
    fun getRecord(id: String): LiveData<Record>


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
}