package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.StorageRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.first

class GetAgreementByIdUseCase(
    private val agreementRepository: AgreementRepository,
    private val unitRepository: UnitRepository,
    private val parkingRepository: ParkingRepository,
    private val storageRepository: StorageRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        agreementId: String
    ): Result<AgreementWithDetails> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_PRIVATE_AGREEMENTS)

        val agreement = agreementRepository.getAgreementById(agreementId)
            ?: return Result.failure(IllegalArgumentException("توافق مورد نظر یافت نشد"))

        if (agreement.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم تطابق شناسه ساختمان"))
        }

        val sourceUnit = unitRepository.getUnitById(agreement.sourceUnitId)
        val targetUnit = unitRepository.getUnitById(agreement.targetUnitId)
        val parking = agreement.relatedParkingId?.let { parkingRepository.getParkingSpaceById(it) }
        val storage = agreement.relatedStorageId?.let { storageRepository.getStorageUnitById(it) }
        val documents = agreementRepository.getDocumentsByAgreement(agreement.id).first()

        return Result.success(
            AgreementWithDetails(
                agreement = agreement,
                sourceUnitNumber = sourceUnit?.unitNumber ?: "نامشخص",
                targetUnitNumber = targetUnit?.unitNumber ?: "نامشخص",
                relatedParkingCode = parking?.code,
                relatedStorageCode = storage?.code,
                documents = documents
            )
        )
    }
}
