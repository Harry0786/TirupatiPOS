package com.tirupati.pos.feature.products.domain.repository

import com.tirupati.pos.feature.products.domain.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun observeCompanies(): Flow<List<Company>>
    suspend fun getCompany(id: String): Company?
    suspend fun saveCompany(company: Company)
    suspend fun deleteCompany(id: String)
}
