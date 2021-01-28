package com.ekosoftware.financialpreview.data.model.settle

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ekosoftware.financialpreview.data.model.movement.Movement
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettleGroupWithMovements(
    @Embedded var settleGroup: SettleGroup,
    @Relation(
        parentColumn = "settleGroupId",
        entity = Movement::class,
        entityColumn = "movementId",
        associateBy = Junction(
            value = SettleGroupMovementsCrossRef::class,
            entityColumn = "movementId",
            parentColumn = "settleGroupId",
        )
    )
    var movements: List<Movement> = emptyList()
) : Parcelable