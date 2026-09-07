package com.apyar.app.domain.repository

import com.apyar.app.domain.model.StorageAssignment
import com.apyar.app.domain.model.StorageUnit
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    suspend fun createStorageUnit(storageUnit: StorageUnit): StorageUnit
    suspend fun updateStorageUnit(storageUnit: StorageUnit)
    suspend fun getStorageUnitById(id: String): StorageUnit?
    suspend fun getStorageUnitByCode(buildingId: String, code: String): StorageUnit?
    fun getStorageUnitsByBuilding(buildingId: String): Flow<List<StorageUnit>>
    
    suspend fun createAssignment(assignment: StorageAssignment): StorageAssignment
    suspend fun updateAssignment(assignment: StorageAssignment)
    suspend fun getActiveAssignmentByStorageId(storageUnitId: String): StorageAssignment?
    fun getAssignmentsByStorageId(storageUnitId: String): Flow<List<StorageAssignment>>
    fun getAssignmentsByUnitId(unitId: String): Flow<List<StorageAssignment>>
}
