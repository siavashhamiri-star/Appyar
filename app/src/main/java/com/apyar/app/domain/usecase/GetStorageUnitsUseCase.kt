package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.StorageUnitWithDetails
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.StorageRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetStorageUnitsUseCase(
    private val storageRepository: StorageRepository,
    private val unitRepository: UnitRepository,
    private val agreementRepository: AgreementRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Flow<List<StorageUnitWithDetails>> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_STORAGE_DATA)

        return storageRepository.getStorageUnitsByBuilding(buildingId).map { units ->
            units.map { storageUnit ->
                val currentAssignment = storageRepository.getActiveAssignmentByStorageId(storageUnit.id)
                val assignedUnit = currentAssignment?.let { unitRepository.getUnitById(it.unitId) }
                val activeAgreements = agreementRepository.getActiveAgreementsForStorage(storageUnit.id)
                val activeAgreement = activeAgreements.firstOrNull { it.status == AgreementStatus.ACTIVE }
                val tempUserUnit = activeAgreement?.let { unitRepository.getUnitById(it.targetUnitId) }

                StorageUnitWithDetails(
                    unit = storageUnit,
                    currentAssignment = currentAssignment,
                    assignedUnitNumber = assignedUnit?.unitNumber,
                    activeAgreement = activeAgreement,
                    temporaryUserUnitNumber = tempUserUnit?.unitNumber
                )
            }
        }
    }
}
