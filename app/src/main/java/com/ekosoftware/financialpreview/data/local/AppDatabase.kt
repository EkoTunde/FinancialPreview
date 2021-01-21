package com.ekosoftware.financialpreview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.CurrencyConversion
import com.ekosoftware.financialpreview.data.model.Record
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef

@Database(
    entities = [
        Account::class,
        Category::class,
        CurrencyConversion::class,
        Record::class,
        Movement::class,
        SettleGroup::class,
        SettleGroupMovementsCrossRef::class
    ], version = 5, exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyConversionDao(): CurrencyConversionDao
    abstract fun movementDao(): RegistryDao
    abstract fun scheduledDao(): MovementDao
    abstract fun settleGroupDao(): SettleGroupDao
}