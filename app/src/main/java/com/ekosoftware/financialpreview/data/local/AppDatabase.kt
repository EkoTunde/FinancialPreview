package com.ekosoftware.financialpreview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.data.model.*
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.scheduled.Scheduled
import com.ekosoftware.financialpreview.data.model.tax.Tax

@Database(
    entities = [
        Account::class,
        Category::class,
        CurrencyConversion::class,
        Movement::class,
        Scheduled::class,
        Tax::class,
        TaxScheduledCrossRef::class
    ], version = 1, exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyConversionDao(): CurrencyConversionDao
    abstract fun movementDao(): MovementDao
    abstract fun scheduledDao(): ScheduledDao
    abstract fun taxDao(): TaxDao
    abstract fun taxScheduledCrossRefDao(): TaxScheduledCrossRefDao
}