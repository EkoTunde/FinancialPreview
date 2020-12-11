package com.ekosoftware.financialpreview.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class used to get debtors and creditors from movements database
 */
@Parcelize data class Friend(var name: String) : Parcelable