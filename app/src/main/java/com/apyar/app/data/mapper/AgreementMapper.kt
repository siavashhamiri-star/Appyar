package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.AgreementDocumentEntity
import com.apyar.app.data.local.entity.AgreementEntity
import com.apyar.app.domain.model.Agreement
import com.apyar.app.domain.model.AgreementDocument
import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.AgreementType
import com.apyar.app.domain.model.DocumentType
import com.apyar.app.domain.model.PaymentArrangement

fun AgreementEntity.toDomain(): Agreement {
    return Agreement(
        id = id,
        buildingId = buildingId,
        agreementType = try { AgreementType.valueOf(agreementType) } catch (e: Exception) { AgreementType.OTHER },
        sourceUnitId = sourceUnitId,
        targetUnitId = targetUnitId,
        relatedParkingId = relatedParkingId,
        relatedStorageId = relatedStorageId,
        startDate = startDate,
        endDate = endDate,
        financialArrangement = try { PaymentArrangement.valueOf(financialArrangement) } catch (e: Exception) { PaymentArrangement.NO_PAYMENT },
        amount = amount,
        description = description,
        status = try { AgreementStatus.valueOf(status) } catch (e: Exception) { AgreementStatus.ACTIVE },
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy
    )
}

fun Agreement.toEntity(): AgreementEntity {
    return AgreementEntity(
        id = id,
        buildingId = buildingId,
        agreementType = agreementType.name,
        sourceUnitId = sourceUnitId,
        targetUnitId = targetUnitId,
        relatedParkingId = relatedParkingId,
        relatedStorageId = relatedStorageId,
        startDate = startDate,
        endDate = endDate,
        financialArrangement = financialArrangement.name,
        amount = amount,
        description = description,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy
    )
}

fun AgreementDocumentEntity.toDomain(): AgreementDocument {
    return AgreementDocument(
        id = id,
        agreementId = agreementId,
        documentType = try { DocumentType.valueOf(documentType) } catch (e: Exception) { DocumentType.OTHER },
        title = title,
        description = description,
        fileReference = fileReference,
        createdAt = createdAt,
        uploadedBy = uploadedBy
    )
}

fun AgreementDocument.toEntity(): AgreementDocumentEntity {
    return AgreementDocumentEntity(
        id = id,
        agreementId = agreementId,
        documentType = documentType.name,
        title = title,
        description = description,
        fileReference = fileReference,
        createdAt = createdAt,
        uploadedBy = uploadedBy
    )
}
