package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.UnitChargeEntity
import com.apyar.app.domain.model.ChargeStatus
import com.apyar.app.domain.model.InclusionStatus
import com.apyar.app.domain.model.ShareType
import com.apyar.app.domain.model.UnitCharge

fun UnitChargeEntity.toDomain(): UnitCharge {
    val incStatus = try {
        InclusionStatus.valueOf(inclusionStatus)
    } catch (_: Exception) {
        InclusionStatus.INCLUDED
    }

    val payStatus = try {
        ChargeStatus.valueOf(status)
    } catch (_: Exception) {
        ChargeStatus.UNPAID
    }

    val share = try {
        ShareType.valueOf(shareType)
    } catch (_: Exception) {
        ShareType.SHARED
    }

    return UnitCharge(
        id = id,
        chargePeriodId = chargePeriodId,
        unitId = unitId,
        inclusionStatus = incStatus,
        calculatedAmount = calculatedAmount,
        adjustmentAmount = adjustmentAmount,
        finalAmount = finalAmount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = payStatus,
        shareType = share,
        calculationNotes = calculationNotes
    )
}

fun UnitCharge.toEntity(): UnitChargeEntity {
    return UnitChargeEntity(
        id = id,
        chargePeriodId = chargePeriodId,
        unitId = unitId,
        inclusionStatus = inclusionStatus.name,
        calculatedAmount = calculatedAmount,
        adjustmentAmount = adjustmentAmount,
        finalAmount = finalAmount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = status.name,
        shareType = shareType.name,
        calculationNotes = calculationNotes
    )
}
