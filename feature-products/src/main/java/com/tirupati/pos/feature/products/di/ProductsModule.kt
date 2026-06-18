package com.tirupati.pos.feature.products.di

import android.content.Context
import androidx.room.Room
import com.tirupati.pos.feature.products.data.local.CompanyDao
import com.tirupati.pos.feature.products.data.local.ProductDao
import com.tirupati.pos.feature.products.data.local.ProductsDatabase
import com.tirupati.pos.feature.products.data.repository.CompanyRepositoryImpl
import com.tirupati.pos.feature.products.data.repository.ProductRepositoryImpl
import com.tirupati.pos.feature.products.domain.repository.CompanyRepository
import com.tirupati.pos.feature.products.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsDatabaseModule {

    @Provides
    @Singleton
    fun provideProductsDatabase(
        @ApplicationContext context: Context
    ): ProductsDatabase {
        return Room.databaseBuilder(
            context,
            ProductsDatabase::class.java,
            "products_db"
        ).build()
    }

    @Provides
    fun provideCompanyDao(database: ProductsDatabase): CompanyDao {
        return database.companyDao()
    }

    @Provides
    fun provideProductDao(database: ProductsDatabase): ProductDao {
        return database.productDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyRepository(
        companyRepositoryImpl: CompanyRepositoryImpl
    ): CompanyRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @dagger.multibindings.IntoSet
    abstract fun bindProductsSyncHandler(impl: com.tirupati.pos.feature.products.sync.ProductsSyncHandler): com.tirupati.pos.core.sync.DownwardSyncHandler
}
