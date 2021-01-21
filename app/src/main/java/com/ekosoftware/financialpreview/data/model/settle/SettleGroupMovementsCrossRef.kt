package com.ekosoftware.financialpreview.data.model.settle

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "settleGroupMovementsCrossRefTable",primaryKeys = ["settleGroupId", "movementId"])
data class SettleGroupMovementsCrossRef(
    @ColumnInfo(name = "settleGroupIdxRef") val settleGroupId: Int,
    @ColumnInfo(name = "movementIdxRef", index = true) val movementId: Int
)