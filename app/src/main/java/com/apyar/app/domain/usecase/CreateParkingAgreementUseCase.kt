package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Agreement
import com.apyar.app.domain.model.AgreementDocument
import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.AgreementType
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.PaymentArrangement
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.UnitRepository
import java.util.UUID

class CreateParkingAgreementUseCase(
    private val agreementRepository: AgreementRepository,
    private val parkingRepository: ParkingRepository,
    private val unitRepository: UnitRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        parkingSpaceId: String,
        sourceUnitId: String,
        targetUnitId: String,
        agreementType: AgreementType = AgreementType.PARKING_USE,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null,
        financialArrangement: PaymentArrangement = PaymentArrangement.NO_PAYMENT,
        amount: Long? = null,
        description: String,
        documentTitle: String? = null,
        documentFileRef: String? = null
    ): Result<Agreement> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_PRIVATE_AGREEMENTS)

        if (sourceUnitId == targetUnitId) {
            return Result.failure(IllegalArgumentException("واحد مبدأ و مقصد نمی‌توانند یکسان باشند"))
        }

        val parking = parkingRepository.getParkingSpaceById(parkingSpaceId)
            ?: return Result.failure(IllegalArgumentException("پارکینگ مورد نظر یافت نشد"))

        if (parking.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("پارکینگ به این ساختمان تعلق ندارد"))
        }

        val sourceUnit = unitRepository.getUnitById(sourceUnitId)
            ?: return Result.failure(IllegalArgumentException("واحد مبدأ یافت نشد"))
        val targetUnit = unitRepository.getUnitById(targetUnitId)
            ?: return Result.failure(IllegalArgumentException("واحد مقصد یافت نشد"))

        if (sourceUnit.buildingId != buildingId || targetUnit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("واحدهای توافق باید متعلق به همین ساختمان باشند"))
        }

        if (endDate != null && endDate < startDate) {
            return Result.failure(IllegalArgumentException("تاریخ پایان توافق نمی‌تواند قبل از تاریخ شروع باشد"))
        }

        val agreement = Agreement(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            agreementType = agreementType,
            sourceUnitId = sourceUnitId,
            targetUnitId = targetUnitId,
            relatedParkingId = parkingSpaceId,
            relatedStorageId = null,
            startDate = startDate,
            endDate = endDate,
            financialArrangement = financialArrangement,
            amount = amount,
            description = description.trim(),
            status = AgreementStatus.ACTIVE,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            createdBy = userId
        )

        val created = agreementRepository.createAgreement(agreement)

        // If supporting document is provided
        if (!documentTitle.isNullOrBlank() && !documentFileRef.isNullOrBlank()) {
            agreementRepository.addDocument(
                AgreementDocument(
                    id = UUID.randomUUID().toString(),
                    agreementId = created.id,
                    title = documentTitle.trim(),
                    fileReference = documentFileRef.trim(),
                    createdAt = System.currentTimeMillis(),
                    uploadedBy = userId
                )
            )
        }

        // Record temporary/rental assignment in parking
        parkingRepository.createAssignment(
            com.apyar.app.domain.model.ParkingAssignment(
                id = UUID.randomUUID().toString(),
                parkingSpaceId = parkingSpaceId,
                unitId = targetUnitId,
                assignmentType = if (agreementType == AgreementType.PARKING_RENTAL) AssignmentType.RENTAL else AssignmentType.TEMPORARY,
                startDate = startDate,
                endDate = endDate,
                notes = "توافق ثبت‌شده (${agreementType.titleFa}): $description",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isActive = true
            )
        )

        parkingRepository.updateParkingSpace(
            parking.copy(
                status = SpaceStatus.TEMPORARILY_USED,
                updatedAt = System.currentTimeMillis()
            )
        )

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_PARKING_AGREEMENT",
                entity = "Agreement",
                entityId = created.id,
                buildingId = buildingId,
                details = "ثبت توافق پارکینگ ${parking.code} بین واحد ${sourceUnit.unitNumber} و ${targetUnit.unitNumber} (مالی: ${financialArrangement.titleFa})"
            )
        )

        return Result.success(created)
    }
}
