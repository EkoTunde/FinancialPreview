package com.ekosoftware.financialpreview.data.model.record

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekosoftware.financialpreview.data.model.movement.Movement
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "records")
data class Record(

    @PrimaryKey
    @ColumnInfo(name = "recordId", index = true)
    var id: String = "Nan",

    @ColumnInfo(name = "recordDate")
    var date: Date? = null,

    @ColumnInfo(name = "recordName")
    var name: String?,

    @Embedded(prefix = "recordOld_")
    var movement: Movement? = null,

    @ColumnInfo(name = "recordAmount")
    var amount: Long,

    @ColumnInfo(name = "recordCurrencyCode")
    var currencyCode: String,

    @ColumnInfo(name = "recordAccountId")
    var accountId: String,

    @ColumnInfo(name = "recordCurrentPendingMovementId")
    var currentPendingMovementId: String?,

    @ColumnInfo(name = "recordAccountIdOut")
    var accountIdOut: String?,

    @ColumnInfo(name = "recordCategoryId")
    var categoryId: String?,

    @ColumnInfo(name = "recordDebtorName")
    var debtorName: String?,

    @ColumnInfo(name = "recordLenderName")
    var lenderName: String?,

    @ColumnInfo(name = "recordDescription")
    var description: String?,

    @ColumnInfo(name = "recordBudgetId")
    var budgetId: String?,

    ) : Parcelable

