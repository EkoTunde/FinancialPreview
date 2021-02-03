package com.ekosoftware.financialpreview.app.injection

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ekosoftware.financialpreview.app.Constants.DATABASE_NAME
import com.ekosoftware.financialpreview.app.Constants.PREPOPULATE_ACCOUNT_TYPES
import com.ekosoftware.financialpreview.app.Constants.PREPOPULATE_BASIC_CATEGORIES_DATA
import com.ekosoftware.financialpreview.data.DummyData
import com.ekosoftware.financialpreview.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // insert the data on the IO Thread
                    ioThread {
                        provideRoomInstance(context).categoryDao()
                            .insertInitialCategories(*PREPOPULATE_BASIC_CATEGORIES_DATA)
                        provideRoomInstance(context).accountDao()
                            .insertAccountTypes(*PREPOPULATE_ACCOUNT_TYPES)
                        provideRoomInstance(context).someDao().insertCurrency(*DummyData.m0Currencies)
                        provideRoomInstance(context).someDao().insertAccount(*DummyData.m1Accounts)
                        provideRoomInstance(context).someDao().insertCategory(*DummyData.m2Categories)
                        provideRoomInstance(context).someDao().insertBudget(*DummyData.m3Budgets)
                        provideRoomInstance(context).someDao().insertMovement(*DummyData.m4Movements)
                        provideRoomInstance(context).someDao().insertSettleGroup(*DummyData.m5SettleGroups)
                        provideRoomInstance(context).someDao().insertSettleGroupCrossRef(*DummyData.m6SettleGroupMovementsCrossRef)
                        provideRoomInstance(context).someDao().insertRecord(*DummyData.m7Records)
                    }
                }
            })
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideAccountDao(db: AppDatabase) = db.accountDao()

    @Singleton
    @Provides
    fun provideBudgetGroupDao(db: AppDatabase) = db.budgetDao()

    @Singleton
    @Provides
    fun provideCategoryDao(db: AppDatabase) = db.categoryDao()

    @Singleton
    @Provides
    fun provideCurrencyDao(db: AppDatabase) = db.currencyDao()

    @Singleton
    @Provides
    fun provideMovementDao(db: AppDatabase) = db.movementDao()

    @Singleton
    @Provides
    fun provideRecordDao(db: AppDatabase) = db.recordDao()

    @Singleton
    @Provides
    fun provideSettleGroupDao(db: AppDatabase) = db.settleGroupDao()

    @Singleton
    @Provides
    fun provideSomeDao(db: AppDatabase) = db.someDao()
}