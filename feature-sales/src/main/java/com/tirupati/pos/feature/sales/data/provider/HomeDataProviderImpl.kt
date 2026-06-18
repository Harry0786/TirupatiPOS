package com.tirupati.pos.feature.sales.data.provider

import com.tirupati.pos.core.ui.home.HomeDataProvider
import com.tirupati.pos.core.ui.home.RecentActivityItem
import com.tirupati.pos.feature.sales.data.local.InvoiceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeDataProviderImpl @Inject constructor(
    private val invoiceDao: InvoiceDao
) : HomeDataProvider {

    override fun observeSalesForDate(date: String): Flow<Double> {
        return invoiceDao.observeSalesForDate(date).map { it ?: 0.0 }
    }

    override fun observeBillsCountForDate(date: String): Flow<Int> {
        return invoiceDao.observeBillsCountForDate(date).map { it ?: 0 }
    }

    override fun observeItemsSoldCountForDate(date: String): Flow<Int> {
        return invoiceDao.observeItemsSoldCountForDate(date).map { it ?: 0 }
    }

    override fun observeCustomersCountForDate(date: String): Flow<Int> {
        return invoiceDao.observeCustomersCountForDate(date).map { it ?: 0 }
    }

    override fun observeRecentActivities(limit: Int): Flow<List<RecentActivityItem>> {
        return invoiceDao.observeRecentInvoices(limit).map { list ->
            list.map { inv ->
                RecentActivityItem(
                    id = inv.id,
                    title = inv.invoiceNumber,
                    subtitle = "Sale • ₹${inv.grandTotal}",
                    time = "Recent",
                    type = "sale"
                )
            }
        }
    }
}
