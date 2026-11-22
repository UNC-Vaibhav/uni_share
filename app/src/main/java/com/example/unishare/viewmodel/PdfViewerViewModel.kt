package com.example.unishare.viewmodel

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unishare.data.local.entity.DocumentEntity
import com.example.unishare.data.repository.DocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class PdfViewerUiState(
    val document: DocumentEntity? = null,
    val pages: List<Bitmap> = emptyList(),
    val pageCount: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val file: File? = null
)

class PdfViewerViewModel(
    private val documentId: String,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfViewerUiState())
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()

    init {
        loadDocumentAndRender()
    }

    fun loadDocumentAndRender() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val doc = documentRepository.getDocumentDirect(documentId)
            if (doc == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Document record not found in local database."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(document = doc) }

            val file = documentRepository.getDocumentFile(doc.relativeFilePath)
            if (file == null || !file.exists() || file.length() == 0L) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "The PDF file is missing from internal storage at: ${doc.relativeFilePath}"
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(file = file) }

            val rendered = renderPdfPages(file)
            if (rendered.isSuccess) {
                val bitmaps = rendered.getOrThrow()
                _uiState.update {
                    it.copy(
                        pages = bitmaps,
                        pageCount = bitmaps.size,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = rendered.exceptionOrNull()?.localizedMessage ?: "Unable to render PDF document."
                    )
                }
            }
        }
    }

    private suspend fun renderPdfPages(file: File): Result<List<Bitmap>> = withContext(Dispatchers.IO) {
        try {
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fileDescriptor)
            val count = renderer.pageCount
            val bitmaps = mutableListOf<Bitmap>()

            for (i in 0 until count) {
                val page = renderer.openPage(i)
                val width = page.width * 2
                val height = page.height * 2
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }

            renderer.close()
            fileDescriptor.close()
            Result.success(bitmaps)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiState.value.pages.forEach { bitmap ->
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }
}
