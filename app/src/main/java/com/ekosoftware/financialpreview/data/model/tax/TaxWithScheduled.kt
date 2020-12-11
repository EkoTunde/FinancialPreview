package com.ekosoftware.financialpreview.data.model.tax

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ekosoftware.financialpreview.data.model.TaxScheduledCrossRef
import com.ekosoftware.financialpreview.data.model.scheduled.Scheduled

data class TaxWithScheduled(
    @Embedded val tax: Tax,
    @Relation(
        parentColumn = "taxId",
        entityColumn = "scheduledId",
        associateBy = Junction(TaxScheduledCrossRef::class)
    )
    val schedules: List<Scheduled>
) {
    fun total(from: Int, to: Int): Double = schedules.filter { it.from >= from && it.to <= to }
        .sumOf { it.leftAmount * (tax.percentage / 100) }
}