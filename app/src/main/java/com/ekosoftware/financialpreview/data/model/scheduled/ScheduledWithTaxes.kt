package com.ekosoftware.financialpreview.data.model.scheduled

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ekosoftware.financialpreview.data.model.tax.Tax
import com.ekosoftware.financialpreview.data.model.TaxScheduledCrossRef

data class ScheduledWithTaxes(
    @Embedded val scheduled: Scheduled,
    @Relation(
        parentColumn = "scheduledId",
        entityColumn = "taxId",
        associateBy = Junction(TaxScheduledCrossRef::class)
    )
    val taxes: List<Tax>
)