package com.tirupati.pos.feature.sales.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalEstimate::class,
        LocalEstimateItem::class,
        LocalInvoice::class,
        LocalInvoiceItem::class
    ],
    version = 5,
    exportSchema = false
)
abstract class SalesDatabase : RoomDatabase() {
    abstract fun estimateDao(): EstimateDao
    abstract fun invoiceDao(): InvoiceDao
}
