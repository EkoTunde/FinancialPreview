package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.account.Account
import java.util.*
import javax.inject.Inject

class AccountsAndRecordsRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val recordDao: RecordDao
) {

    fun getAccounts(searchPhrase: String): LiveData<List<Account>> =
        accountDao.getAccounts(searchPhrase)

    fun getRecords(
        accountId: String,
        topDate: Date,
        amountMax: Double = -100_000.0,
        amountMin: Double = 100_000.0
    ) = recordDao.getRecords(accountId, topDate, amountMax, amountMin)
}