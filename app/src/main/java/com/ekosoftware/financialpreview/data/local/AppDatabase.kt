package com.ekosoftware.financialpreview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.CurrencyConversion
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.account.AccountType
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef

@Database(
    entities = [
        Account::class,
        AccountType::class,
        Budget::class,
        Category::class,
        CurrencyConversion::class,
        Record::class,
        Movement::class,
        SettleGroup::class,
        SettleGroupMovementsCrossRef::class
    ], version = 3, exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyConversionDao(): CurrencyConversionDao
    abstract fun movementDao(): MovementDao
    abstract fun recordDao(): RecordDao
    abstract fun settleGroupDao(): SettleGroupDao
    abstract fun someDao(): SomeDao
}