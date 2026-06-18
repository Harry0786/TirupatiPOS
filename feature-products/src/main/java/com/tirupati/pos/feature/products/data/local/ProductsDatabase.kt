package com.tirupati.pos.feature.products.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalCompany::class,
        LocalProduct::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ProductsDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun productDao(): ProductDao
}
