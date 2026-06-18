package com.tirupati.pos.feature.products.data.repository

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncQueue
import com.tirupati.pos.feature.products.data.local.CompanyDao
import com.tirupati.pos.feature.products.domain.model.Company
import com.tirupati.pos.feature.products.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyRepositoryImpl @Inject constructor(
    private val companyDao: CompanyDao,
    private val syncQueue: SyncQueue,
    private val syncManager: SyncManager
) : CompanyRepository {

    override fun observeCompanies(): Flow<List<Company>> =
        companyDao.observeCompanies().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getCompany(id: String): Company? {
        return companyDao.getCompany(id)?.toDomain()
    }

    override suspend fun saveCompany(company: Company) {
        val local = company.toLocal()
        companyDao.insert(local)

        val op = PendingOperation(
            id = UUID.randomUUID().toString(),
            operationType = "INSERT",
            entityType = "companies",
            entityId = company.id,
            payloadJson = Json.encodeToString(Company.serializer(), company),
            timestamp = System.currentTimeMillis()
        )
        syncQueue.enqueue(op)
        syncManager.requestSync()
    }

    override suspend fun deleteCompany(id: String) {
        // Find existing to delete
        val company = getCompany(id)
        if (company != null) {
            companyDao.delete(company.toLocal())
            
            val op = PendingOperation(
                id = UUID.randomUUID().toString(),
                operationType = "DELETE",
                entityType = "companies",
                entityId = id,
                payloadJson = null,
                timestamp = System.currentTimeMillis()
            )
            syncQueue.enqueue(op)
            syncManager.requestSync()
        }
    }
}
