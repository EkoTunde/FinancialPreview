package com.ekosoftware.financialpreview.data.model

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings

data class SimpleDisplayableData(
    var id: String,
    var name: String,
    var description: String? = null,
    var color: Int? = null,
    var iconResId: Int? = null
)