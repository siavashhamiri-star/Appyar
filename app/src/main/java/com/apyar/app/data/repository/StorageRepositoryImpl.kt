package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.StorageAssignmentDao
import com.apyar.app.data.local.dao.StorageUnitDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.StorageAssignment
import com.apyar.app.domain.model.StorageUnit
import com.apyar.app.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StorageRepositoryImpl(
    private val storageUnitDao: StorageUnitDao,
    private val storageAssignmentDao: StorageAssignmentDao
) : StorageRepository {

    override suspend fun createStorageUnit(storageUnit: StorageUnit): StorageUnit {
        storageUnitDao.insertStorageUnit(storageUnit.toEntity())
        return storageUnit
    }

    override suspend fun updateStorageUnit(storageUnit: StorageUnit) {
        storageUnitDao.updateStorageUnit(storageUnit.toEntity())
    }

    override suspend fun getStorageUnitById(id: String): StorageUnit? {
        return storageUnitDao.getStorageUnitById(id)?.toDomain()
    }

    override suspend fun getStorageUnitByCode(buildingId: String, code: String): StorageUnit? {
        return storageUnitDao.getStorageUnitByCode(buildingId, code)?.toDomain()
    }

    override fun getStorageUnitsByBuilding(buildingId: String): Flow<List<StorageUnit>> {
        return storageUnitDao.observeStorageUnitsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createAssignment(assignment: StorageAssignment): StorageAssignment {
        storageAssignmentDao.deactivateActiveAssignments(
            storageUnitId = assignment.storageUnitId,
            endDate = assignment.startDate,
            updatedAt = assignment.createdAt
        )
        storageAssignmentDao.insertAssignment(assignment.toEntity())
        return assignment
    }

    override suspend fun updateAssignment(assignment: StorageAssignment) {
        storageAssignmentDao.updateAssignment(assignment.toEntity())
    }

    override suspend fun getActiveAssignmentByStorageId(storageUnitId: String): StorageAssignment? {
        return storageAssignmentDao.getActiveAssignmentByStorageId(storageUnitId)?.toDomain()
    }

    override fun getAssignmentsByStorageId(storageUnitId: String): Flow<List<StorageAssignment>> {
        return storageAssignmentDao.observeAssignmentsByStorageId(storageUnitId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAssignmentsByUnitId(unitId: String): Flow<List<StorageAssignment>> {
        return storageAssignmentDao.observeAssignmentsByUnitId(unitId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
