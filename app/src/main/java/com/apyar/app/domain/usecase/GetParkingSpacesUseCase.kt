package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.ParkingSpace
import com.apyar.app.domain.model.ParkingSpaceWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetParkingSpacesUseCase(
    private val parkingRepository: ParkingRepository,
    private val unitRepository: UnitRepository,
    private val agreementRepository: AgreementRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Flow<List<ParkingSpaceWithDetails>> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_PARKING_DATA)

        return parkingRepository.getParkingSpacesByBuilding(buildingId).map { spaces ->
            spaces.map { space ->
                val currentAssignment = parkingRepository.getActiveAssignmentByParkingId(space.id)
                val assignedUnit = currentAssignment?.let { unitRepository.getUnitById(it.unitId) }
                val activeAgreements = agreementRepository.getActiveAgreementsForParking(space.id)
                val activeAgreement = activeAgreements.firstOrNull { it.status == AgreementStatus.ACTIVE }
                val tempUserUnit = activeAgreement?.let { unitRepository.getUnitById(it.targetUnitId) }

                ParkingSpaceWithDetails(
                    space = space,
                    currentAssignment = currentAssignment,
                    assignedUnitNumber = assignedUnit?.unitNumber,
                    activeAgreement = activeAgreement,
                    temporaryUserUnitNumber = tempUserUnit?.unitNumber
                )
            }
        }
    }
}
