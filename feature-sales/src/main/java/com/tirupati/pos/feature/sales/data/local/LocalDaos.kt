package com.tirupati.pos.feature.sales.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tirupati.pos.core.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao : BaseDao<LocalEstimate> {
    @Query("SELECT * FROM estimates ORDER BY createdAt DESC")
    fun observeEstimates(): Flow<List<LocalEstimate>>

    @Query("SELECT * FROM estimates WHERE id = :id")
    fun observeEstimate(id: String): Flow<LocalEstimate?>

    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getEstimate(id: String): LocalEstimate?

    @Query("UPDATE estimates SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<LocalEstimateItem>)

    @Query("DELETE FROM estimate_items WHERE estimateId = :estimateId")
    suspend fun deleteItems(estimateId: String)

    @Query("SELECT * FROM estimate_items WHERE estimateId = :estimateId ORDER BY srNo ASC")
    fun observeItems(estimateId: String): Flow<List<LocalEstimateItem>>

    @Query("SELECT * FROM estimate_items WHERE estimateId = :estimateId ORDER BY srNo ASC")
    suspend fun getItems(estimateId: String): List<LocalEstimateItem>

    @Transaction
    suspend fun saveEstimateWithItems(estimate: LocalEstimate, items: List<LocalEstimateItem>) {
        insert(estimate)
        deleteItems(estimate.id)
        insertItems(items)
    }
}

@Dao
interface InvoiceDao : BaseDao<LocalInvoice> {
    @Query("SELECT * FROM invoices WHERE id = :id")
    fun observeInvoice(id: String): Flow<LocalInvoice?>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoice(id: String): LocalInvoice?

    @Query("UPDATE invoices SET paymentMethod = :paymentMethod, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePayment(id: String, paymentMethod: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<LocalInvoiceItem>)

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY srNo ASC")
    suspend fun getItems(invoiceId: String): List<LocalInvoiceItem>

    @Transaction
    suspend fun saveInvoiceWithItems(invoice: LocalInvoice, items: List<LocalInvoiceItem>) {
        insert(invoice)
        insertItems(items)
    }
}

@Dao
interface ProductDao : BaseDao<LocalProduct> {
    @Query("SELECT * FROM local_products ORDER BY itemName ASC")
    fun observeProducts(): Flow<List<LocalProduct>>

    @Query("SELECT * FROM local_products WHERE itemCode LIKE '%' || :query || '%' OR itemName LIKE '%' || :query || '%'")
    suspend fun searchProducts(query: String): List<LocalProduct>

    @Query("SELECT COUNT(*) FROM local_products")
    suspend fun getProductCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<LocalProduct>)
}
