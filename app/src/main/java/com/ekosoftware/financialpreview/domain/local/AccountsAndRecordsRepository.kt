package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
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
        searchPhrase: String,
        topDate: Date,
        amountMax: Long = 1_000_000_000_000,
        amountMin: Long = -1_000_000_000_000,
        orderBy: String
    ): LiveData<List<RecordUIShort>> = recordDao.getRecordsUIShort(accountId, searchPhrase, topDate, amountMax, amountMin, orderBy)
}