package com.ekosoftware.financialpreview.app

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.Category
import java.util.*

object Constants {

    // Room database name
    const val DATABASE_NAME = "financial_preview_database"

    val PREPOPULATE_DATA = arrayOf(
        Category(
            id = UUID.randomUUID().toString(),
            name = Strings.get(R.string.category_simple_adjustment),
            iconResId = R.drawable.ic_check,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = UUID.randomUUID().toString(),
            name = Strings.get(R.string.category_simple_record),
            iconResId = R.drawable.ic_movement,
            colorResId = R.color.materialColor4
        )
    )
}