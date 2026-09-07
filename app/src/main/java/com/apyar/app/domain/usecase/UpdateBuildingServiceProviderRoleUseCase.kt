package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ProviderRole
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ServiceProviderRepository

class UpdateBuildingServiceProviderRoleUseCase(
    private val serviceProviderRepository: ServiceProviderRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        linkId: String,
        role: ProviderRole,
        isPrimary: Boolean,
        isActive: Boolean = true,
        notes: String? = null
    ): Result<BuildingServiceProvider> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_SERVICE_PROVIDERS)

        val link = serviceProviderRepository.getBuildingServiceProviderById(linkId)
            ?: return Result.failure(IllegalArgumentException("ارتباط سرویس‌کار با ساختمان یافت نشد"))

        if (link.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("ارتباط مربوط به این ساختمان نیست"))
        }

        if (isPrimary && !link.isPrimary) {
            val currentPrimary = serviceProviderRepository.getPrimaryProviderForCategory(buildingId, link.serviceCategory)
            if (currentPrimary != null && currentPrimary.link.id != link.id) {
                serviceProviderRepository.updateBuildingServiceProvider(
                    currentPrimary.link.copy(isPrimary = false, role = ProviderRole.BACKUP_PROVIDER)
                )
            }
        }

        val updated = link.copy(
            role = role,
            isPrimary = isPrimary,
            isActive = isActive,
            endDate = if (!isActive) System.currentTimeMillis() else null,
            notes = notes?.trim() ?: link.notes
        )

        serviceProviderRepository.updateBuildingServiceProvider(updated)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "UPDATE_SERVICE_PROVIDER_ROLE",
                entity = "BuildingServiceProvider",
                entityId = updated.id,
                buildingId = buildingId,
                details = "تغییر نقش سرویس‌کار به ${role.titleFa} (اصلی: $isPrimary, فعال: $isActive)"
            )
        )

        return Result.success(updated)
    }
}
