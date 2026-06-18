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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimates(estimates: List<LocalEstimate>)

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

    @Query("SELECT id FROM estimates")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM estimates WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("DELETE FROM estimate_items WHERE estimateId IN (:ids)")
    suspend fun deleteItemsByIds(ids: List<String>)
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<LocalInvoice>)

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteItems(invoiceId: String)

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY srNo ASC")
    suspend fun getItems(invoiceId: String): List<LocalInvoiceItem>

    @Transaction
    suspend fun saveInvoiceWithItems(invoice: LocalInvoice, items: List<LocalInvoiceItem>) {
        insert(invoice)
        deleteItems(invoice.id)
        insertItems(items)
    }

    @Query("SELECT id FROM invoices")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM invoices WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("DELETE FROM invoice_items WHERE invoiceId IN (:ids)")
    suspend fun deleteItemsByIds(ids: List<String>)

    @Query("SELECT SUM(grandTotal) FROM invoices WHERE date = :date")
    fun observeSalesForDate(date: String): Flow<Double?>

    @Query("SELECT COUNT(*) FROM invoices WHERE date = :date")
    fun observeBillsCountForDate(date: String): Flow<Int?>

    @Query("SELECT SUM(quantity) FROM invoice_items WHERE invoiceId IN (SELECT id FROM invoices WHERE date = :date)")
    fun observeItemsSoldCountForDate(date: String): Flow<Int?>

    @Query("SELECT COUNT(DISTINCT customerName) FROM invoices WHERE date = :date")
    fun observeCustomersCountForDate(date: String): Flow<Int?>

    @Query("SELECT * FROM invoices ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentInvoices(limit: Int = 5): Flow<List<LocalInvoice>>
}
