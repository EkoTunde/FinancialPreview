package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.account.AccountWithMovements

@Dao
interface AccountDao {

    @Query("SELECT SUM(accountBalance) FROM accounts WHERE accountCurrencyCode = :currency")
    fun getAccountsTotalForCurrency(currency: String): LiveData<Double?>

    @Query(
        """
        SELECT * 
        FROM accounts 
        WHERE accountName LIKE '%' || :search || '%'
        OR accountDescription LIKE '%' || :search || '%'
        OR accountBank LIKE '%' || :search || '%'
        OR accountCurrencyCode LIKE '%' || :search || '%'
        """
    )
    fun getAccounts(search: String): LiveData<List<Account>?>

    @Transaction
    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccountWithMovements(accountId: Int): LiveData<List<AccountWithMovements>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(vararg account: Account)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccounts(vararg account: Account)

    @Delete
    suspend fun deleteAccounts(vararg account: Account)
}