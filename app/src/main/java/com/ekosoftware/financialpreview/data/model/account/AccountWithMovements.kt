package com.ekosoftware.financialpreview.data.model.account

import androidx.room.Embedded
import androidx.room.Relation
import com.ekosoftware.financialpreview.data.model.record.Record

data class AccountWithMovements(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "recordAccountId"
    )
    val records: List<Record>
)