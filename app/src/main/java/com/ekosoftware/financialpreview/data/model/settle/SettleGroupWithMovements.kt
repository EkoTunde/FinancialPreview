package com.ekosoftware.financialpreview.data.model.settle

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ekosoftware.financialpreview.data.model.movement.Movement


data class SettleGroupWithMovements(
    @Embedded val settleGroup: SettleGroup,
    @Relation(
        parentColumn = "settleGroupId",
        entityColumn = "movementId",
        associateBy = Junction(
            SettleGroupMovementsCrossRef::class,
            entityColumn = "movementId",
            parentColumn = "settleGroupId",
        )
    )
    var movements: List<Movement> = emptyList()
)