package com.ekosoftware.financialpreview.app

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.AccountType
import java.util.*

object Constants {

    // Room database name
    const val DATABASE_NAME = "financial_preview_database"

    val PREPOPULATE_BASIC_CATEGORIES_DATA = arrayOf(
        Category(
            id = UUID.randomUUID().toString(),
            name = Strings.get(R.string.category_simple_adjustment),
            description = "",
            iconResId = R.drawable.ic_check,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = UUID.randomUUID().toString(),
            name = Strings.get(R.string.category_simple_record),
            description = "",
            iconResId = R.drawable.ic_movement,
            colorResId = R.color.materialColor4
        )
    )

    val PREPOPULATE_ACCOUNT_TYPES = arrayOf(
        AccountType(1, R.string.account_type_general),
        AccountType(2, R.string.account_type_cash),
        AccountType(3, R.string.account_type_saving),
        AccountType(4, R.string.account_type_checking),
        AccountType(5, R.string.account_type_other),
        AccountType(6, R.string.account_type_digital),
        AccountType(7, R.string.account_type_safe),
        AccountType(8, R.string.pending_budgets),
        AccountType(9, R.string.settle_groups),
    )
}