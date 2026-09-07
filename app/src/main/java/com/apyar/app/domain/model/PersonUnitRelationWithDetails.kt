package com.apyar.app.domain.model

data class PersonUnitRelationWithDetails(
    val relation: UnitPersonRelation,
    val unit: Unit,
    val building: Building?
)
