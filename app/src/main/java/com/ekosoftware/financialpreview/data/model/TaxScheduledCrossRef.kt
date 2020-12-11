package com.ekosoftware.financialpreview.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["taxId", "scheduledId"])
data class TaxScheduledCrossRef (
    val taxId: Int,
    val scheduledId: Int
)