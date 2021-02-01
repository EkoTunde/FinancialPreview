package com.ekosoftware.financialpreview.app

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.account.AccountType
import com.ekosoftware.financialpreview.data.model.budget.Budget
import java.util.*

object Constants {

    // Room database name
    const val DATABASE_NAME = "financial_preview_database"

    const val CATEGORY_ADJUSTMENT_ID = "0"
    const val CATEGORY_SIMPLE_RECORD_ID = "1"
    const val CATEGORY_TRANSFER_ID = "2"
    const val CATEGORY_DEBT_LOAN_ID = "3"

    val PREPOPULATE_BASIC_CATEGORIES_DATA = arrayOf(
        Category(
            id = CATEGORY_ADJUSTMENT_ID,
            name = Strings.get(R.string.category_simple_adjustment),
            description = "",
            iconResId = R.drawable.ic_check,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = CATEGORY_SIMPLE_RECORD_ID,
            name = Strings.get(R.string.category_simple_record),
            description = "",
            iconResId = R.drawable.ic_movement,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = CATEGORY_TRANSFER_ID,
            name = Strings.get(R.string.category_transfer),
            description = "",
            iconResId = R.drawable.ic_bank_transfer,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = CATEGORY_DEBT_LOAN_ID,
            name = Strings.get(R.string.category_share),
            description = "",
            iconResId = R.drawable.category_share,
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



    const val SETTLE_TYPE_MOVEMENT = 1
    const val SETTLE_TYPE_GROUP = 2
    const val SETTLE_TYPE_SIMPLE_RECORD = 3
    const val SETTLE_TYPE_ADJUSTMENT = 4
    const val SETTLE_TYPE_TRANSFER = 5
    const val SETTLE_TYPE_LOAN_DEBT = 6

    const val nan = "NaN"
}