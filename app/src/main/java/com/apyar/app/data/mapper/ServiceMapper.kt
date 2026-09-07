package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.BuildingBillEntity
import com.apyar.app.data.local.entity.BuildingServiceProviderEntity
import com.apyar.app.data.local.entity.InvoiceEntity
import com.apyar.app.data.local.entity.MaintenanceRecordEntity
import com.apyar.app.data.local.entity.PaymentRecordEntity
import com.apyar.app.data.local.entity.ServiceProviderEntity
import com.apyar.app.data.local.entity.ServiceRecordEntity
import com.apyar.app.domain.model.BillPaymentStatus
import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoicePaymentStatus
import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.model.PaymentMethod
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.model.ProviderRole
import com.apyar.app.domain.model.ServiceCategory
import com.apyar.app.domain.model.ServiceProvider
import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus

fun ServiceProviderEntity.toDomain(): ServiceProvider {
    return ServiceProvider(
        id = id,
        name = name,
        companyName = companyName,
        mobileNumber = mobileNumber,
        secondaryPhone = secondaryPhone,
        email = email,
        address = address,
        serviceCategory = try { ServiceCategory.valueOf(serviceCategory) } catch (e: Exception) { ServiceCategory.OTHER },
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun ServiceProvider.toEntity(): ServiceProviderEntity {
    return ServiceProviderEntity(
        id = id,
        name = name,
        companyName = companyName,
        mobileNumber = mobileNumber,
        secondaryPhone = secondaryPhone,
        email = email,
        address = address,
        serviceCategory = serviceCategory.name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun BuildingServiceProviderEntity.toDomain(): BuildingServiceProvider {
    return BuildingServiceProvider(
        id = id,
        buildingId = buildingId,
        serviceProviderId = serviceProviderId,
        serviceCategory = try { ServiceCategory.valueOf(serviceCategory) } catch (e: Exception) { ServiceCategory.OTHER },
        role = try { ProviderRole.valueOf(role) } catch (e: Exception) { ProviderRole.PRIMARY_PROVIDER },
        isPrimary = isPrimary,
        isActive = isActive,
        startDate = startDate,
        endDate = endDate,
        notes = notes
    )
}

fun BuildingServiceProvider.toEntity(): BuildingServiceProviderEntity {
    return BuildingServiceProviderEntity(
        id = id,
        buildingId = buildingId,
        serviceProviderId = serviceProviderId,
        serviceCategory = serviceCategory.name,
        role = role.name,
        isPrimary = isPrimary,
        isActive = isActive,
        startDate = startDate,
        endDate = endDate,
        notes = notes
    )
}

fun ServiceRecordEntity.toDomain(): ServiceRecord {
    return ServiceRecord(
        id = id,
        buildingId = buildingId,
        serviceProviderId = serviceProviderId,
        serviceCategory = try { ServiceCategory.valueOf(serviceCategory) } catch (e: Exception) { ServiceCategory.OTHER },
        title = title,
        description = description,
        requestedAt = requestedAt,
        scheduledAt = scheduledAt,
        completedAt = completedAt,
        status = try { ServiceRecordStatus.valueOf(status) } catch (e: Exception) { ServiceRecordStatus.REQUESTED },
        createdBy = createdBy,
        notes = notes
    )
}

fun ServiceRecord.toEntity(): ServiceRecordEntity {
    return ServiceRecordEntity(
        id = id,
        buildingId = buildingId,
        serviceProviderId = serviceProviderId,
        serviceCategory = serviceCategory.name,
        title = title,
        description = description,
        requestedAt = requestedAt,
        scheduledAt = scheduledAt,
        completedAt = completedAt,
        status = status.name,
        createdBy = createdBy,
        notes = notes
    )
}

fun MaintenanceRecordEntity.toDomain(): MaintenanceRecord {
    return MaintenanceRecord(
        id = id,
        buildingId = buildingId,
        serviceRecordId = serviceRecordId,
        title = title,
        description = description,
        equipmentType = equipmentType,
        location = location,
        performedAt = performedAt,
        nextServiceDate = nextServiceDate,
        performedBy = performedBy,
        cost = cost,
        notes = notes
    )
}

fun MaintenanceRecord.toEntity(): MaintenanceRecordEntity {
    return MaintenanceRecordEntity(
        id = id,
        buildingId = buildingId,
        serviceRecordId = serviceRecordId,
        title = title,
        description = description,
        equipmentType = equipmentType,
        location = location,
        performedAt = performedAt,
        nextServiceDate = nextServiceDate,
        performedBy = performedBy,
        cost = cost,
        notes = notes
    )
}

fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = id,
        buildingId = buildingId,
        serviceProviderId = serviceProviderId,
        serviceRecordId = serviceRecordId,
        invoiceNumber = invoiceNumber,
        issueDate = issueDate,
        dueDate = dueDate,
        amount = amount,
        description = description,
        paymentStatus = try { InvoicePaymentStatus.valueOf(paymentStatus) } catch (e: Exception) { InvoicePaymentStatus.UNPAID },
        createdBy = createdBy
    )
}

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        buildingId = buildingId,
        serviceProviderId = serviceProviderId,
        serviceRecordId = serviceRecordId,
        invoiceNumber = invoiceNumber,
        issueDate = issueDate,
        dueDate = dueDate,
        amount = amount,
        description = description,
        paymentStatus = paymentStatus.name,
        createdBy = createdBy
    )
}

fun PaymentRecordEntity.toDomain(): PaymentRecord {
    return PaymentRecord(
        id = id,
        buildingId = buildingId,
        invoiceId = invoiceId,
        amount = amount,
        paidAt = paidAt,
        paymentMethod = try { PaymentMethod.valueOf(paymentMethod) } catch (e: Exception) { PaymentMethod.BANK_TRANSFER },
        referenceNumber = referenceNumber,
        recordedBy = recordedBy,
        notes = notes
    )
}

fun PaymentRecord.toEntity(): PaymentRecordEntity {
    return PaymentRecordEntity(
        id = id,
        buildingId = buildingId,
        invoiceId = invoiceId,
        amount = amount,
        paidAt = paidAt,
        paymentMethod = paymentMethod.name,
        referenceNumber = referenceNumber,
        recordedBy = recordedBy,
        notes = notes
    )
}

fun BuildingBillEntity.toDomain(): BuildingBill {
    return BuildingBill(
        id = id,
        buildingId = buildingId,
        billType = try { BillType.valueOf(billType) } catch (e: Exception) { BillType.OTHER },
        providerName = providerName,
        billingPeriod = billingPeriod,
        amount = amount,
        issueDate = issueDate,
        dueDate = dueDate,
        paidAt = paidAt,
        paymentStatus = try { BillPaymentStatus.valueOf(paymentStatus) } catch (e: Exception) { BillPaymentStatus.UNPAID },
        referenceNumber = referenceNumber,
        createdBy = createdBy
    )
}

fun BuildingBill.toEntity(): BuildingBillEntity {
    return BuildingBillEntity(
        id = id,
        buildingId = buildingId,
        billType = billType.name,
        providerName = providerName,
        billingPeriod = billingPeriod,
        amount = amount,
        issueDate = issueDate,
        dueDate = dueDate,
        paidAt = paidAt,
        paymentStatus = paymentStatus.name,
        referenceNumber = referenceNumber,
        createdBy = createdBy
    )
}
