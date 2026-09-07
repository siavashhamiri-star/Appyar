package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.AgreementWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.StorageRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetBuildingAgreementsUseCase(
    private val agreementRepository: AgreementRepository,
    private val unitRepository: UnitRepository,
    private val parkingRepository: ParkingRepository,
    private val storageRepository: StorageRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Flow<List<AgreementWithDetails>> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_PRIVATE_AGREEMENTS)

        val currentTime = System.currentTimeMillis()

        return agreementRepository.getAgreementsByBuilding(buildingId).map { agreements ->
            agreements.map { agreement ->
                // Auto-expire agreement if end date passed
                var actualAgreement = agreement
                if (agreement.status == AgreementStatus.ACTIVE && agreement.endDate != null && agreement.endDate < currentTime) {
                    actualAgreement = agreement.copy(
                        status = AgreementStatus.EXPIRED,
                        updatedAt = currentTime
                    )
                    agreementRepository.updateAgreement(actualAgreement)
                }

                val sourceUnit = unitRepository.getUnitById(actualAgreement.sourceUnitId)
                val targetUnit = unitRepository.getUnitById(actualAgreement.targetUnitId)
                val parking = actualAgreement.relatedParkingId?.let { parkingRepository.getParkingSpaceById(it) }
                val storage = actualAgreement.relatedStorageId?.let { storageRepository.getStorageUnitById(it) }
                val documents = agreementRepository.getDocumentsByAgreement(actualAgreement.id).first()

                AgreementWithDetails(
                    agreement = actualAgreement,
                    sourceUnitNumber = sourceUnit?.unitNumber ?: "نامشخص",
                    targetUnitNumber = targetUnit?.unitNumber ?: "نامشخص",
                    relatedParkingCode = parking?.code,
                    relatedStorageCode = storage?.code,
                    documents = documents
                )
            }
        }
    }
}
