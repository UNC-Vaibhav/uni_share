package com.example.unishare.data.storage

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileStorageManager(private val context: Context) {

    fun getDocumentsRoot(): File {
        val dir = File(context.filesDir, "documents")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getFile(relativeFilePath: String): File {
        return File(getDocumentsRoot(), relativeFilePath)
    }

    fun fileExists(relativeFilePath: String): Boolean {
        val file = getFile(relativeFilePath)
        return file.exists() && file.isFile && file.length() > 0
    }

    fun writeBytes(relativeFilePath: String, bytes: ByteArray): File {
        val file = getFile(relativeFilePath)
        file.parentFile?.let {
            if (!it.exists()) it.mkdirs()
        }
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    fun copyFromStream(relativeFilePath: String, inputStream: InputStream): File {
        val file = getFile(relativeFilePath)
        file.parentFile?.let {
            if (!it.exists()) it.mkdirs()
        }
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    fun deleteFile(relativeFilePath: String): Boolean {
        val file = getFile(relativeFilePath)
        return if (file.exists()) file.delete() else true
    }
}
