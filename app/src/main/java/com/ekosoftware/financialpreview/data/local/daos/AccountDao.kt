package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.account.AccountType
import com.ekosoftware.financialpreview.data.model.account.AccountWithMovements
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT SUM(accountBalance) FROM accounts WHERE accountCurrencyCode = :currency")
    fun getAccountsTotalForCurrency(currency: String): LiveData<Long?>

    @Query("SELECT SUM(accountBalance) FROM accounts WHERE accountCurrencyCode = :currency")
    fun getAccountsTotalForCurrency2(currency: String): Flow<Long?>

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
    fun getAccounts(search: String): LiveData<List<Account>>

    @Query(
        """
        SELECT accountId AS id, accountName AS name, accountTypeId AS typeId, accountColorResId AS color 
        FROM accounts 
        WHERE accountName LIKE '%' || :search || '%'
        OR accountDescription LIKE '%' || :search || '%'
        OR accountBank LIKE '%' || :search || '%'
        OR accountCurrencyCode LIKE '%' || :search || '%'
        """
    )
    fun getAccountsAsSimpleData(search: String) : LiveData<List<SimpleQueryData>>

    @Transaction
    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccountWithMovements(accountId: Int): LiveData<List<AccountWithMovements>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(vararg account: Account)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccounts(vararg account: Account)

    @Delete
    suspend fun deleteAccounts(vararg account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountTypes(vararg accountType: AccountType)
}