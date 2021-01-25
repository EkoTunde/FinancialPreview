package com.ekosoftware.financialpreview.app.injection

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.App
import com.ekosoftware.financialpreview.app.Constants.DATABASE_NAME
import com.ekosoftware.financialpreview.app.Constants.PREPOPULATE_DATA
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.local.AppDatabase
import com.ekosoftware.financialpreview.data.model.Category
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
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
                        provideRoomInstance(context).categoryDao().insertInitialCategories(
                            *PREPOPULATE_DATA
                        )
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
}