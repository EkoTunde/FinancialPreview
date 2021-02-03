package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.account.AccountType
import com.ekosoftware.financialpreview.data.model.account.AccountWithMovements
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<Account> {

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

    @Transaction
    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccountWithMovements(accountId: Int): LiveData<List<AccountWithMovements>>

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccount(accountId: String): LiveData<Account>

    @Query("SELECT accountName FROM accounts WHERE accountId = :accountId")
    fun getAccountName(accountId: String): LiveData<String>

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(vararg account: Account)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccounts(vararg account: Account)

    @Delete
    suspend fun deleteAccounts(vararg account: Account)*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountTypes(vararg accountType: AccountType)

    @Query(
        """
        SELECT 
        accountId AS id, 
        accountName AS name, 
        NULL AS currencyCode, 
        NULL AS amount,
        accountTypeId AS typeId, 
        NULL AS description, 
        accountColorResId AS color, 
        NULL AS iconResId 
        FROM accounts
        WHERE accountName LIKE '%' || :searchPhrase || '%' 
        OR accountDescription LIKE '%' || :searchPhrase || '%'
        OR accountBank LIKE '%' || :searchPhrase || '%'
        OR accountCurrencyCode LIKE '%' || :searchPhrase || '%'
        ORDER BY accountName
    """
    )
    fun getAccountsAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>

    @Query(
        """
        SELECT accountCurrencyCode FROM accounts WHERE accountId = :accountId
    """
    )
    fun getCurrencyCodeForAccountId(accountId: String): String
}