package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.StorageUnitWithDetails
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.StorageRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.first

class GetStorageUnitByIdUseCase(
    private val storageRepository: StorageRepository,
    private val unitRepository: UnitRepository,
    private val agreementRepository: AgreementRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        storageUnitId: String
    ): Result<StorageUnitWithDetails> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_STORAGE_DATA)

        val storageUnit = storageRepository.getStorageUnitById(storageUnitId)
            ?: return Result.failure(IllegalArgumentException("انباری مورد نظر یافت نشد"))

        if (storageUnit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم تطابق شناسه ساختمان"))
        }

        val currentAssignment = storageRepository.getActiveAssignmentByStorageId(storageUnitId)
        val assignedUnit = currentAssignment?.let { unitRepository.getUnitById(it.unitId) }
        val activeAgreements = agreementRepository.getActiveAgreementsForStorage(storageUnitId)
        val activeAgreement = activeAgreements.firstOrNull { it.status == AgreementStatus.ACTIVE }
        val tempUserUnit = activeAgreement?.let { unitRepository.getUnitById(it.targetUnitId) }
        val history = storageRepository.getAssignmentsByStorageId(storageUnitId).first()

        return Result.success(
            StorageUnitWithDetails(
                unit = storageUnit,
                currentAssignment = currentAssignment,
                assignedUnitNumber = assignedUnit?.unitNumber,
                activeAgreement = activeAgreement,
                temporaryUserUnitNumber = tempUserUnit?.unitNumber,
                assignmentHistory = history
            )
        )
    }
}
