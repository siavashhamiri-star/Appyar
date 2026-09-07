package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.AgreementDao
import com.apyar.app.data.local.dao.AgreementDocumentDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.Agreement
import com.apyar.app.domain.model.AgreementDocument
import com.apyar.app.domain.repository.AgreementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AgreementRepositoryImpl(
    private val agreementDao: AgreementDao,
    private val agreementDocumentDao: AgreementDocumentDao
) : AgreementRepository {

    override suspend fun createAgreement(agreement: Agreement): Agreement {
        agreementDao.insertAgreement(agreement.toEntity())
        return agreement
    }

    override suspend fun updateAgreement(agreement: Agreement) {
        agreementDao.updateAgreement(agreement.toEntity())
    }

    override suspend fun getAgreementById(id: String): Agreement? {
        return agreementDao.getAgreementById(id)?.toDomain()
    }

    override fun getAgreementsByBuilding(buildingId: String): Flow<List<Agreement>> {
        return agreementDao.observeAgreementsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAgreementsByUnit(unitId: String): Flow<List<Agreement>> {
        return agreementDao.observeAgreementsByUnitId(unitId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getActiveAgreementsForParking(parkingId: String): List<Agreement> {
        return agreementDao.getActiveAgreementsForParking(parkingId).map { it.toDomain() }
    }

    override suspend fun getActiveAgreementsForStorage(storageId: String): List<Agreement> {
        return agreementDao.getActiveAgreementsForStorage(storageId).map { it.toDomain() }
    }

    override suspend fun addDocument(document: AgreementDocument): AgreementDocument {
        agreementDocumentDao.insertDocument(document.toEntity())
        return document
    }

    override fun getDocumentsByAgreement(agreementId: String): Flow<List<AgreementDocument>> {
        return agreementDocumentDao.observeDocumentsByAgreementId(agreementId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
