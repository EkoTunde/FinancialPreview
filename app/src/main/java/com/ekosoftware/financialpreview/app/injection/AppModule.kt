package com.ekosoftware.financialpreview.app.injection

import android.content.Context
import androidx.room.Room
import com.ekosoftware.financialpreview.app.Constants.DATABASE_NAME
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
    fun provideRoomInstance(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideAccountDao(db: AppDatabase) = db.accountDao()

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
    fun provideScheduledDao(db: AppDatabase) = db.scheduledDao()

    @Singleton
    @Provides
    fun provideSettleGroupDao(db: AppDatabase) = db.settleGroupDao()

}