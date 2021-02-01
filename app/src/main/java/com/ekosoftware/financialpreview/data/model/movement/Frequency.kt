package com.ekosoftware.financialpreview.data.model.movement

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Frequency(

    @ColumnInfo(name = "FreqFrom")
    var from: Int?,

    @ColumnInfo(name = "FreqTo")
    var to: Int?,

    @ColumnInfo(name = "FreqInstallments")
    var installments: Int? = 0,

    @ColumnInfo(name = "FreqMonthsChecked")
    var monthsChecked: String = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"

) : Parcelable

