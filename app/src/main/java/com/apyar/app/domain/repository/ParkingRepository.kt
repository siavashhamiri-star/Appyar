package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ParkingAssignment
import com.apyar.app.domain.model.ParkingSpace
import kotlinx.coroutines.flow.Flow

interface ParkingRepository {
    suspend fun createParkingSpace(parkingSpace: ParkingSpace): ParkingSpace
    suspend fun updateParkingSpace(parkingSpace: ParkingSpace)
    suspend fun getParkingSpaceById(id: String): ParkingSpace?
    suspend fun getParkingSpaceByCode(buildingId: String, code: String): ParkingSpace?
    fun getParkingSpacesByBuilding(buildingId: String): Flow<List<ParkingSpace>>
    
    suspend fun createAssignment(assignment: ParkingAssignment): ParkingAssignment
    suspend fun updateAssignment(assignment: ParkingAssignment)
    suspend fun getActiveAssignmentByParkingId(parkingSpaceId: String): ParkingAssignment?
    fun getAssignmentsByParkingId(parkingSpaceId: String): Flow<List<ParkingAssignment>>
    fun getAssignmentsByUnitId(unitId: String): Flow<List<ParkingAssignment>>
}
