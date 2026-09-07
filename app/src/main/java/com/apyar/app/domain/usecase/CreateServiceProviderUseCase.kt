package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ProviderRole
import com.apyar.app.domain.model.ServiceCategory
import com.apyar.app.domain.model.ServiceProvider
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ServiceProviderRepository
import java.util.UUID

class CreateServiceProviderUseCase(
    private val serviceProviderRepository: ServiceProviderRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        name: String,
        companyName: String? = null,
        mobileNumber: String,
        secondaryPhone: String? = null,
        email: String? = null,
        address: String? = null,
        serviceCategory: ServiceCategory,
        description: String? = null,
        role: ProviderRole = ProviderRole.PRIMARY_PROVIDER,
        isPrimary: Boolean = true,
        notes: String? = null
    ): Result<ServiceProvider> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_SERVICE_PROVIDERS)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("نام تأمین‌کننده یا سرویس‌کار نمی‌تواند خالی باشد"))
        }

        val trimmedMobile = mobileNumber.trim()
        if (trimmedMobile.isBlank()) {
            return Result.failure(IllegalArgumentException("شماره تماس نمی‌تواند خالی باشد"))
        }

        // Check if provider already exists by mobile
        val existing = serviceProviderRepository.getServiceProviderByMobileNumber(trimmedMobile)
        val provider = existing ?: ServiceProvider(
            id = UUID.randomUUID().toString(),
            name = trimmedName,
            companyName = companyName?.trim(),
            mobileNumber = trimmedMobile,
            secondaryPhone = secondaryPhone?.trim(),
            email = email?.trim(),
            address = address?.trim(),
            serviceCategory = serviceCategory,
            description = description?.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isActive = true
        )

        if (existing == null) {
            serviceProviderRepository.createServiceProvider(provider)
        }

        // If setting as primary, demote existing primary for this category
        if (isPrimary) {
            val currentPrimary = serviceProviderRepository.getPrimaryProviderForCategory(buildingId, serviceCategory)
            if (currentPrimary != null && currentPrimary.provider.id != provider.id) {
                serviceProviderRepository.updateBuildingServiceProvider(
                    currentPrimary.link.copy(isPrimary = false, role = ProviderRole.BACKUP_PROVIDER)
                )
            }
        }

        // Link to building
        val existingLink = serviceProviderRepository.getLinkByBuildingAndProvider(buildingId, provider.id)
        if (existingLink == null) {
            val link = BuildingServiceProvider(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                serviceProviderId = provider.id,
                serviceCategory = serviceCategory,
                role = role,
                isPrimary = isPrimary,
                isActive = true,
                startDate = System.currentTimeMillis(),
                notes = notes?.trim()
            )
            serviceProviderRepository.linkProviderToBuilding(link)
        } else {
            serviceProviderRepository.updateBuildingServiceProvider(
                existingLink.copy(
                    serviceCategory = serviceCategory,
                    role = role,
                    isPrimary = isPrimary,
                    isActive = true,
                    notes = notes?.trim() ?: existingLink.notes
                )
            )
        }

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_SERVICE_PROVIDER",
                entity = "ServiceProvider",
                entityId = provider.id,
                buildingId = buildingId,
                details = "ثبت و انتساب سرویس‌کار ${provider.name} برای دسته ${serviceCategory.titleFa} با نقش ${role.titleFa}"
            )
        )

        return Result.success(provider)
    }
}
