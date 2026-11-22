package com.example.unishare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unishare.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE subjectId = :subjectId AND documentType = :documentType ORDER BY addedAt DESC")
    fun getDocumentsBySubject(subjectId: String, documentType: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    fun getDocumentById(id: String): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentByIdDirect(id: String): DocumentEntity?

    @Query("SELECT COUNT(*) FROM documents WHERE subjectId = :subjectId AND documentType = :documentType")
    fun getDocumentCount(subjectId: String, documentType: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM documents WHERE subjectId = :subjectId AND documentType = :documentType")
    suspend fun getDocumentCountDirect(subjectId: String, documentType: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>): List<Long>

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: String): Int
}
