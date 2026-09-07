package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.StorageRepository

class CancelAgreementUseCase(
    private val agreementRepository: AgreementRepository,
    private val parkingRepository: ParkingRepository,
    private val storageRepository: StorageRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        agreementId: String,
        reason: String? = null
    ): Result<Unit> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_PRIVATE_AGREEMENTS)

        val agreement = agreementRepository.getAgreementById(agreementId)
            ?: return Result.failure(IllegalArgumentException("توافق مورد نظر یافت نشد"))

        if (agreement.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("توافق به این ساختمان تعلق ندارد"))
        }

        val updated = agreement.copy(
            status = AgreementStatus.CANCELLED,
            updatedAt = System.currentTimeMillis()
        )
        agreementRepository.updateAgreement(updated)

        // Restore parking space if applicable
        agreement.relatedParkingId?.let { parkingId ->
            val parking = parkingRepository.getParkingSpaceById(parkingId)
            if (parking != null) {
                val activeAssignment = parkingRepository.getActiveAssignmentByParkingId(parkingId)
                if (activeAssignment != null && activeAssignment.unitId == agreement.targetUnitId) {
                    parkingRepository.updateAssignment(
                        activeAssignment.copy(
                            isActive = false,
                            endDate = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                parkingRepository.updateParkingSpace(
                    parking.copy(
                        status = SpaceStatus.AVAILABLE,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        // Restore storage unit if applicable
        agreement.relatedStorageId?.let { storageId ->
            val storage = storageRepository.getStorageUnitById(storageId)
            if (storage != null) {
                val activeAssignment = storageRepository.getActiveAssignmentByStorageId(storageId)
                if (activeAssignment != null && activeAssignment.unitId == agreement.targetUnitId) {
                    storageRepository.updateAssignment(
                        activeAssignment.copy(
                            isActive = false,
                            endDate = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                storageRepository.updateStorageUnit(
                    storage.copy(
                        status = SpaceStatus.AVAILABLE,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CANCEL_AGREEMENT",
                entity = "Agreement",
                entityId = agreement.id,
                buildingId = buildingId,
                details = "لغو توافق شناسه ${agreement.id} (${reason ?: "بدون توضیحات اضافی"})"
            )
        )

        return Result.success(Unit)
    }
}
