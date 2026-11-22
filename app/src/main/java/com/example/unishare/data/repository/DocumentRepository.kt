package com.example.unishare.data.repository

import com.example.unishare.data.local.dao.DocumentDao
import com.example.unishare.data.local.entity.DocumentEntity
import com.example.unishare.data.storage.FileStorageManager
import kotlinx.coroutines.flow.Flow
import java.io.File

class DocumentRepository(
    private val documentDao: DocumentDao,
    private val fileStorageManager: FileStorageManager
) {
    fun getDocuments(subjectId: String, documentType: String): Flow<List<DocumentEntity>> =
        documentDao.getDocumentsBySubject(subjectId, documentType)

    fun getDocument(id: String): Flow<DocumentEntity?> =
        documentDao.getDocumentById(id)

    suspend fun getDocumentDirect(id: String): DocumentEntity? =
        documentDao.getDocumentByIdDirect(id)

    fun getDocumentCount(subjectId: String, documentType: String): Flow<Int> =
        documentDao.getDocumentCount(subjectId, documentType)

    suspend fun getDocumentCountDirect(subjectId: String, documentType: String): Int =
        documentDao.getDocumentCountDirect(subjectId, documentType)

    fun getDocumentFile(relativeFilePath: String): File? {
        val file = fileStorageManager.getFile(relativeFilePath)
        return if (file.exists() && file.isFile) file else null
    }

    fun fileExists(relativeFilePath: String): Boolean =
        fileStorageManager.fileExists(relativeFilePath)
}
