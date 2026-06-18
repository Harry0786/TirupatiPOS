package com.tirupati.pos.feature.sales.di

import android.content.Context
import androidx.room.Room
import com.tirupati.pos.feature.sales.data.local.EstimateDao
import com.tirupati.pos.feature.sales.data.local.InvoiceDao
import com.tirupati.pos.feature.sales.data.local.ProductDao
import com.tirupati.pos.feature.sales.data.local.SalesDatabase
import com.tirupati.pos.feature.sales.data.repository.SalesRepositoryImpl
import com.tirupati.pos.feature.sales.domain.repository.SalesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SalesRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSalesRepository(impl: SalesRepositoryImpl): SalesRepository

    @Binds
    @Singleton
    abstract fun bindHomeDataProvider(impl: com.tirupati.pos.feature.sales.data.provider.HomeDataProviderImpl): com.tirupati.pos.core.ui.home.HomeDataProvider

    @Binds
    @dagger.multibindings.IntoSet
    abstract fun bindSalesSyncHandler(impl: com.tirupati.pos.feature.sales.sync.SalesSyncHandler): com.tirupati.pos.core.sync.DownwardSyncHandler
}

@Module
@InstallIn(SingletonComponent::class)
object SalesDatabaseModule {

    @Provides
    @Singleton
    fun provideSalesDatabase(@ApplicationContext context: Context): SalesDatabase {
        return Room.databaseBuilder(
            context,
            SalesDatabase::class.java,
            "sales_tirupati.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideEstimateDao(db: SalesDatabase): EstimateDao = db.estimateDao()

    @Provides
    fun provideInvoiceDao(db: SalesDatabase): InvoiceDao = db.invoiceDao()

    @Provides
    fun provideProductDao(db: SalesDatabase): ProductDao = db.productDao()
}
