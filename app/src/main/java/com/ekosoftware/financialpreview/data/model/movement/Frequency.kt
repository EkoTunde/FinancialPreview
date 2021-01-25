package com.ekosoftware.financialpreview.data.model.movement

import androidx.room.ColumnInfo

data class Frequency(

    @ColumnInfo(name = "FreqTo")
    var from: Int?,

    @ColumnInfo(name = "FreqFrom")
    var to: Int?,

    @ColumnInfo(name = "FreqInstallments")
    var installments: Int?,

    @ColumnInfo(name = "FreqMonthsChecked")
    var monthsChecked: String = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
)

