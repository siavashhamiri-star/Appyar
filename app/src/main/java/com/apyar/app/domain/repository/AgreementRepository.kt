package com.apyar.app.domain.repository

import com.apyar.app.domain.model.Agreement
import com.apyar.app.domain.model.AgreementDocument
import kotlinx.coroutines.flow.Flow

interface AgreementRepository {
    suspend fun createAgreement(agreement: Agreement): Agreement
    suspend fun updateAgreement(agreement: Agreement)
    suspend fun getAgreementById(id: String): Agreement?
    fun getAgreementsByBuilding(buildingId: String): Flow<List<Agreement>>
    fun getAgreementsByUnit(unitId: String): Flow<List<Agreement>>
    suspend fun getActiveAgreementsForParking(parkingId: String): List<Agreement>
    suspend fun getActiveAgreementsForStorage(storageId: String): List<Agreement>
    
    suspend fun addDocument(document: AgreementDocument): AgreementDocument
    fun getDocumentsByAgreement(agreementId: String): Flow<List<AgreementDocument>>
}
