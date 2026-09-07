package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ServiceCategory
import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ServiceProviderRepository
import com.apyar.app.domain.repository.ServiceRecordRepository
import java.util.UUID

class CreateServiceRecordUseCase(
    private val serviceRecordRepository: ServiceRecordRepository,
    private val serviceProviderRepository: ServiceProviderRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        serviceProviderId: String,
        serviceCategory: ServiceCategory,
        title: String,
        description: String,
        scheduledAt: Long? = null,
        notes: String? = null
    ): Result<ServiceRecord> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.CREATE_SERVICE_RECORD)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        val provider = serviceProviderRepository.getServiceProviderById(serviceProviderId)
            ?: return Result.failure(IllegalArgumentException("سرویس‌کار مورد نظر یافت نشد"))

        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("عنوان خدمت نمی‌تواند خالی باشد"))
        }

        val trimmedDesc = description.trim()
        if (trimmedDesc.isBlank()) {
            return Result.failure(IllegalArgumentException("توضیحات خدمت نمی‌تواند خالی باشد"))
        }

        val record = ServiceRecord(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            serviceProviderId = serviceProviderId,
            serviceCategory = serviceCategory,
            title = trimmedTitle,
            description = trimmedDesc,
            requestedAt = System.currentTimeMillis(),
            scheduledAt = scheduledAt,
            completedAt = null,
            status = if (scheduledAt != null) ServiceRecordStatus.SCHEDULED else ServiceRecordStatus.REQUESTED,
            createdBy = userId,
            notes = notes?.trim()
        )

        val created = serviceRecordRepository.createServiceRecord(record)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_SERVICE_RECORD",
                entity = "ServiceRecord",
                entityId = created.id,
                buildingId = buildingId,
                details = "ثبت درخواست خدمت '${created.title}' برای سرویس‌کار ${provider.name}"
            )
        )

        return Result.success(created)
    }
}
