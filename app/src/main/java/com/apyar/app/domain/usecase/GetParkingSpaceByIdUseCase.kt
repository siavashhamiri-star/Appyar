package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.ParkingSpaceWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.first

class GetParkingSpaceByIdUseCase(
    private val parkingRepository: ParkingRepository,
    private val unitRepository: UnitRepository,
    private val agreementRepository: AgreementRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        parkingSpaceId: String
    ): Result<ParkingSpaceWithDetails> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_PARKING_DATA)

        val space = parkingRepository.getParkingSpaceById(parkingSpaceId)
            ?: return Result.failure(IllegalArgumentException("پارکینگ مورد نظر یافت نشد"))

        if (space.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم تطابق شناسه ساختمان"))
        }

        val currentAssignment = parkingRepository.getActiveAssignmentByParkingId(parkingSpaceId)
        val assignedUnit = currentAssignment?.let { unitRepository.getUnitById(it.unitId) }
        val activeAgreements = agreementRepository.getActiveAgreementsForParking(parkingSpaceId)
        val activeAgreement = activeAgreements.firstOrNull { it.status == AgreementStatus.ACTIVE }
        val tempUserUnit = activeAgreement?.let { unitRepository.getUnitById(it.targetUnitId) }
        val history = parkingRepository.getAssignmentsByParkingId(parkingSpaceId).first()

        return Result.success(
            ParkingSpaceWithDetails(
                space = space,
                currentAssignment = currentAssignment,
                assignedUnitNumber = assignedUnit?.unitNumber,
                activeAgreement = activeAgreement,
                temporaryUserUnitNumber = tempUserUnit?.unitNumber,
                assignmentHistory = history
            )
        )
    }
}
