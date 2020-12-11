package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.account.AccountWithMovements

@Dao
interface AccountDao {

    @Query("SELECT * FROM account_table")
    fun getAccounts(): LiveData<List<Account>>

    @Transaction
    @Query("SELECT * FROM account_table WHERE account_id = :accountId")
    fun getAccountWithMovements(accountId: Int): LiveData<List<AccountWithMovements>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(vararg account: Account)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccounts(vararg account: Account)

    @Delete
    suspend fun deleteAccounts(vararg account: Account)
}