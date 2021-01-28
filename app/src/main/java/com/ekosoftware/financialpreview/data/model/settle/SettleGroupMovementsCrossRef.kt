package com.ekosoftware.financialpreview.data.model.settle

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "settleGroupMovementsCrossRefTable",primaryKeys = ["settleGroupId", "movementId"])
data class SettleGroupMovementsCrossRef(
    /*@ColumnInfo(name = "settleGroupId")*/ val settleGroupId: String,
    /*@ColumnInfo(name = "movementId", index = true) */val movementId: String
)