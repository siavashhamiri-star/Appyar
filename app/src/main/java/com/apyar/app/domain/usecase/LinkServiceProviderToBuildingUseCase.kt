package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ProviderRole
import com.apyar.app.domain.model.ServiceCategory
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ServiceProviderRepository
import java.util.UUID

class LinkServiceProviderToBuildingUseCase(
    private val serviceProviderRepository: ServiceProviderRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        serviceProviderId: String,
        serviceCategory: ServiceCategory,
        role: ProviderRole = ProviderRole.PRIMARY_PROVIDER,
        isPrimary: Boolean = true,
        notes: String? = null
    ): Result<BuildingServiceProvider> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_SERVICE_PROVIDERS)

        val provider = serviceProviderRepository.getServiceProviderById(serviceProviderId)
            ?: return Result.failure(IllegalArgumentException("سرویس‌کار مورد نظر یافت نشد"))

        if (isPrimary) {
            val currentPrimary = serviceProviderRepository.getPrimaryProviderForCategory(buildingId, serviceCategory)
            if (currentPrimary != null && currentPrimary.provider.id != serviceProviderId) {
                serviceProviderRepository.updateBuildingServiceProvider(
                    currentPrimary.link.copy(isPrimary = false, role = ProviderRole.BACKUP_PROVIDER)
                )
            }
        }

        val existingLink = serviceProviderRepository.getLinkByBuildingAndProvider(buildingId, serviceProviderId)
        val link = if (existingLink != null) {
            val updated = existingLink.copy(
                serviceCategory = serviceCategory,
                role = role,
                isPrimary = isPrimary,
                isActive = true,
                notes = notes?.trim() ?: existingLink.notes
            )
            serviceProviderRepository.updateBuildingServiceProvider(updated)
            updated
        } else {
            val created = BuildingServiceProvider(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                serviceProviderId = serviceProviderId,
                serviceCategory = serviceCategory,
                role = role,
                isPrimary = isPrimary,
                isActive = true,
                startDate = System.currentTimeMillis(),
                notes = notes?.trim()
            )
            serviceProviderRepository.linkProviderToBuilding(created)
        }

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "LINK_SERVICE_PROVIDER",
                entity = "BuildingServiceProvider",
                entityId = link.id,
                buildingId = buildingId,
                details = "اتصال سرویس‌کار ${provider.name} به ساختمان با نقش ${role.titleFa} در دسته ${serviceCategory.titleFa}"
            )
        )

        return Result.success(link)
    }
}
