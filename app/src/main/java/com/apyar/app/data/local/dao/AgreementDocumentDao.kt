package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apyar.app.data.local.entity.AgreementDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgreementDocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: AgreementDocumentEntity)

    @Query("SELECT * FROM agreement_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: String): AgreementDocumentEntity?

    @Query("SELECT * FROM agreement_documents WHERE agreementId = :agreementId ORDER BY createdAt DESC")
    fun observeDocumentsByAgreementId(agreementId: String): Flow<List<AgreementDocumentEntity>>

    @Query("SELECT * FROM agreement_documents WHERE agreementId = :agreementId ORDER BY createdAt DESC")
    suspend fun getDocumentsByAgreementId(agreementId: String): List<AgreementDocumentEntity>
}
