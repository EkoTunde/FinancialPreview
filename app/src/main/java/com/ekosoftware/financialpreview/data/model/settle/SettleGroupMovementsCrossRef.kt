package com.ekosoftware.financialpreview.data.model.settle

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "settleGroupMovementsCrossRefTable", primaryKeys = ["settleGroupId", "movementId"])
data class SettleGroupMovementsCrossRef(
    val settleGroupId: String,
    val movementId: String
)