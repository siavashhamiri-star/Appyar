package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.ParkingAssignmentDao
import com.apyar.app.data.local.dao.ParkingSpaceDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.ParkingAssignment
import com.apyar.app.domain.model.ParkingSpace
import com.apyar.app.domain.repository.ParkingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParkingRepositoryImpl(
    private val parkingSpaceDao: ParkingSpaceDao,
    private val parkingAssignmentDao: ParkingAssignmentDao
) : ParkingRepository {

    override suspend fun createParkingSpace(parkingSpace: ParkingSpace): ParkingSpace {
        parkingSpaceDao.insertParkingSpace(parkingSpace.toEntity())
        return parkingSpace
    }

    override suspend fun updateParkingSpace(parkingSpace: ParkingSpace) {
        parkingSpaceDao.updateParkingSpace(parkingSpace.toEntity())
    }

    override suspend fun getParkingSpaceById(id: String): ParkingSpace? {
        return parkingSpaceDao.getParkingSpaceById(id)?.toDomain()
    }

    override suspend fun getParkingSpaceByCode(buildingId: String, code: String): ParkingSpace? {
        return parkingSpaceDao.getParkingSpaceByCode(buildingId, code)?.toDomain()
    }

    override fun getParkingSpacesByBuilding(buildingId: String): Flow<List<ParkingSpace>> {
        return parkingSpaceDao.observeParkingSpacesByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createAssignment(assignment: ParkingAssignment): ParkingAssignment {
        // Deactivate previous active assignments for this space to maintain history integrity
        parkingAssignmentDao.deactivateActiveAssignments(
            parkingSpaceId = assignment.parkingSpaceId,
            endDate = assignment.startDate,
            updatedAt = assignment.createdAt
        )
        parkingAssignmentDao.insertAssignment(assignment.toEntity())
        return assignment
    }

    override suspend fun updateAssignment(assignment: ParkingAssignment) {
        parkingAssignmentDao.updateAssignment(assignment.toEntity())
    }

    override suspend fun getActiveAssignmentByParkingId(parkingSpaceId: String): ParkingAssignment? {
        return parkingAssignmentDao.getActiveAssignmentByParkingId(parkingSpaceId)?.toDomain()
    }

    override fun getAssignmentsByParkingId(parkingSpaceId: String): Flow<List<ParkingAssignment>> {
        return parkingAssignmentDao.observeAssignmentsByParkingId(parkingSpaceId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAssignmentsByUnitId(unitId: String): Flow<List<ParkingAssignment>> {
        return parkingAssignmentDao.observeAssignmentsByUnitId(unitId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
