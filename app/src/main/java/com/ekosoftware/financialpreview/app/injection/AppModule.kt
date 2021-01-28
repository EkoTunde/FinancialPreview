package com.ekosoftware.financialpreview.app.injection

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ekosoftware.financialpreview.app.Constants.DATABASE_NAME
import com.ekosoftware.financialpreview.app.Constants.PREPOPULATE_ACCOUNT_TYPES
import com.ekosoftware.financialpreview.app.Constants.PREPOPULATE_BASIC_CATEGORIES_DATA
import com.ekosoftware.financialpreview.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
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
                        //provideRoomInstance(context).someDao().insertAccount(*DummyData1.m1Accounts)
                        //provideRoomInstance(context).someDao().insertCategory(*DummyData1.m2Categories)
                        //provideRoomInstance(context).someDao().insertBudget(*DummyData1.m3Budgets)
                        //provideRoomInstance(context).someDao().insertMovement(*DummyData1.m4Movements)
                        //provideRoomInstance(context).someDao().insertSettleGroup(*DummyData1.m5SettleGroups)
                        //provideRoomInstance(context).someDao().insertSettleGroupCrossRef(*DummyData1.m6SettleGroupMovementsCrossRef)
                        //provideRoomInstance(context).someDao().insertRecord(*DummyData1.m7Records)
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
    fun provideCurrencyDao(db: AppDatabase) = db.currencyConversionDao()

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