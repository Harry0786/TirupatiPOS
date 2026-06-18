package com.tirupati.pos.feature.products.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirupati.pos.core.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao : BaseDao<LocalCompany> {
    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun observeCompanies(): Flow<List<LocalCompany>>

    @Query("SELECT * FROM companies WHERE id = :id")
    suspend fun getCompany(id: String): LocalCompany?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<LocalCompany>)

    @Query("SELECT id FROM companies")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM companies WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)
}

@Dao
interface ProductDao : BaseDao<LocalProduct> {
    @Query("SELECT * FROM products ORDER BY itemName ASC")
    fun observeProducts(): Flow<List<LocalProduct>>

    @Query("SELECT * FROM products WHERE companyId = :companyId ORDER BY itemName ASC")
    fun observeProductsByCompany(companyId: String): Flow<List<LocalProduct>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProduct(id: String): LocalProduct?

    @Query("SELECT * FROM products WHERE itemCode LIKE '%' || :query || '%' OR itemName LIKE '%' || :query || '%'")
    suspend fun searchProducts(query: String): List<LocalProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<LocalProduct>)

    @Query("SELECT id FROM products")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM products WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)
}
