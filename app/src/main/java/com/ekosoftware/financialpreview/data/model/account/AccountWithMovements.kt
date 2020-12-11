package com.ekosoftware.financialpreview.data.model.account

import androidx.room.Embedded
import androidx.room.Relation
import com.ekosoftware.financialpreview.data.model.Movement

data class AccountWithMovements(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "account_id",
        entityColumn = "movement_account_id"
    )
    val movements: List<Movement>
)