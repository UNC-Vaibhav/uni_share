package com.example.unishare.data.storage

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.unishare.data.local.database.AppDatabase
import com.example.unishare.data.local.entity.BranchEntity
import com.example.unishare.data.local.entity.DocumentEntity
import com.example.unishare.data.local.entity.SemesterEntity
import com.example.unishare.data.local.entity.SubjectEntity
import com.example.unishare.data.local.entity.UniversityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class InitialDataSeeder(
    private val context: Context,
    private val database: AppDatabase,
    private val fileStorageManager: FileStorageManager
) {
    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val universities = listOf(
            UniversityEntity("geu", "Graphic Era Deemed to be University", "GEU", "ic_logo_geu"),
            UniversityEntity("gehu", "Graphic Era Hill University", "GEHU", "ic_logo_gehu"),
            UniversityEntity("dit", "DIT University", "DIT", "ic_logo_dit"),
            UniversityEntity("upes", "UPES - University of Petroleum & Energy Studies", "UPES", "ic_logo_upes"),
            UniversityEntity("doon", "Doon University", "DU", "ic_logo_doon"),
            UniversityEntity("utu", "Uttarakhand Technical University", "UTU", "ic_logo_utu")
        )
        database.universityDao().insertUniversities(universities)

        for (uni in universities) {
            val semesters = (1..8).map { num ->
                SemesterEntity(
                    id = "${uni.id}_sem_$num",
                    universityId = uni.id,
                    semesterNumber = num,
                    displayName = "Semester $num"
                )
            }
            database.academicDao().insertSemesters(semesters)

            val branches = listOf(
                BranchEntity("${uni.id}_cse", uni.id, "Computer Science & Engineering", "CSE"),
                BranchEntity("${uni.id}_it", uni.id, "Information Technology", "IT"),
                BranchEntity("${uni.id}_ece", uni.id, "Electronics & Communication", "ECE"),
                BranchEntity("${uni.id}_me", uni.id, "Mechanical Engineering", "ME"),
                BranchEntity("${uni.id}_ce", uni.id, "Civil Engineering", "CE")
            )
            database.academicDao().insertBranches(branches)

            val cseBranchId = "${uni.id}_cse"
            val sem1Id = "${uni.id}_sem_1"
            val sem2Id = "${uni.id}_sem_2"
            val sem3Id = "${uni.id}_sem_3"
            val sem4Id = "${uni.id}_sem_4"
            val sem5Id = "${uni.id}_sem_5"

            val subjects = listOf(
                SubjectEntity("${uni.id}_tcs101", cseBranchId, sem1Id, "Fundamentals of Computer & Programming", "TCS-101", "Introduction to computers, algorithms, problem solving, and C programming."),
                SubjectEntity("${uni.id}_tee101", cseBranchId, sem1Id, "Basic Electrical Engineering", "TEE-101", "DC circuits, AC fundamentals, magnetic circuits, transformers, and electrical machines."),
                SubjectEntity("${uni.id}_thu101", cseBranchId, sem1Id, "Professional Communication", "THU-101", "Technical communication, grammar, vocabulary, report writing, and presentation skills."),
                SubjectEntity("${uni.id}_tma101", cseBranchId, sem1Id, "Engineering Mathematics - I", "TMA-101", "Matrices, differential calculus, partial differentiation, and multiple integrals."),
                SubjectEntity("${uni.id}_tph101", cseBranchId, sem1Id, "Engineering Physics", "TPH-101", "Relativistic mechanics, wave optics, quantum mechanics, and lasers."),
                SubjectEntity("${uni.id}_hsmc101", cseBranchId, sem1Id, "Design Thinking Lab & HSMC", "HSMC-101", "Design thinking principles, empathy mapping, ideation, and human values."),

                SubjectEntity("${uni.id}_tcs201", cseBranchId, sem2Id, "Data Structures using C", "TCS-201", "Arrays, stacks, queues, linked lists, and basic sorting."),
                SubjectEntity("${uni.id}_tma201", cseBranchId, sem2Id, "Engineering Mathematics - II", "TMA-201", "Differential equations, Laplace transforms, and Fourier series."),
                SubjectEntity("${uni.id}_tec201", cseBranchId, sem2Id, "Basic Electronics Engineering", "TEC-201", "Semiconductor diodes, transistors, amplifiers, and operational amplifiers."),

                SubjectEntity("${uni.id}_bcs301", cseBranchId, sem3Id, "Data Structures & Algorithms", "BCS-301", "Trees, graphs, hashing, sorting algorithms, and complexity analysis."),
                SubjectEntity("${uni.id}_bcs302", cseBranchId, sem3Id, "Object Oriented Programming (Java)", "BCS-302", "Classes, inheritance, polymorphism, interfaces, and exception handling."),
                SubjectEntity("${uni.id}_bcs303", cseBranchId, sem3Id, "Discrete Mathematical Structures", "BCS-303", "Set theory, propositional logic, graph theory, and combinatorics."),
                SubjectEntity("${uni.id}_bcs401", cseBranchId, sem4Id, "Operating Systems", "BCS-401", "Process management, CPU scheduling, synchronization, and memory management."),
                SubjectEntity("${uni.id}_bcs402", cseBranchId, sem4Id, "Design & Analysis of Algorithms", "BCS-402", "Divide and conquer, greedy methods, dynamic programming, and NP-completeness."),
                SubjectEntity("${uni.id}_bcs403", cseBranchId, sem4Id, "Database Management Systems", "BCS-403", "Relational models, SQL queries, ER modeling, normalization, and transactions."),
                SubjectEntity("${uni.id}_bcs501", cseBranchId, sem5Id, "Computer Networks", "BCS-501", "OSI reference layers, TCP/IP, IP addressing, routing algorithms, and socket APIs."),
                SubjectEntity("${uni.id}_bcs502", cseBranchId, sem5Id, "Software Engineering", "BCS-502", "Agile methodologies, software architecture, design patterns, and unit testing.")
            )
            database.academicDao().insertSubjects(subjects)
        }

        syncAllAssetDocuments()
    }

    private suspend fun syncAllAssetDocuments() {
        val assetManager = context.assets
        val allAssetPdfPaths = listAssetFilesRecursively("documents")

        val colleges = listOf("geu", "gehu", "dit", "upes", "doon", "utu")

        for (fullAssetPath in allAssetPdfPaths) {
            val normalizedPath = fullAssetPath.replace("\\", "/")
            val pathSegments = normalizedPath.split("/")
            if (pathSegments.size < 3) continue

            val collegeSegment = pathSegments.find { seg ->
                colleges.any { it.equals(seg, ignoreCase = true) }
            }?.lowercase() ?: continue

            val docType = when {
                normalizedPath.contains("/notes/", ignoreCase = true) -> "NOTES"
                normalizedPath.contains("/syllabus/", ignoreCase = true) -> "SYLLABUS"
                normalizedPath.contains("/pyq/", ignoreCase = true) -> "PYQ"
                else -> "NOTES"
            }

            val rawFileName = pathSegments.last().removeSuffix("__")
            val cleanSubjectCode = extractSubjectCode(normalizedPath)
            val subjectKey = cleanSubjectCode.replace("-", "").lowercase()
            val targetSubjectId = "${collegeSegment}_$subjectKey"

            val subject = database.academicDao().getSubjectByIdDirect(targetSubjectId)
                ?: database.academicDao().getSubjectByIdDirect("${collegeSegment}_tcs101")

            val destRelativePath = "$collegeSegment/${docType.lowercase()}/${cleanSubjectCode}_$rawFileName"
            val targetFile = fileStorageManager.getFile(destRelativePath)

            if (!targetFile.exists() || targetFile.length() == 0L) {
                try {
                    assetManager.open(fullAssetPath).use { input ->
                        targetFile.parentFile?.let { if (!it.exists()) it.mkdirs() }
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (_: Exception) {
                    continue
                }
            }

            if (subject != null && targetFile.exists() && targetFile.length() > 0L) {
                val docId = "${collegeSegment}_${cleanSubjectCode}_${docType.lowercase()}_${rawFileName.hashCode()}"
                val cleanTitle = buildDocumentTitle(cleanSubjectCode, rawFileName, docType)

                val entity = DocumentEntity(
                    id = docId,
                    universityId = collegeSegment,
                    branchId = subject.branchId,
                    semesterId = subject.semesterId,
                    subjectId = subject.id,
                    documentTitle = cleanTitle,
                    description = "$cleanSubjectCode ${docType.lowercase().replaceFirstChar { it.uppercase() }} - ${subject.subjectName}",
                    documentType = docType,
                    fileName = rawFileName,
                    relativeFilePath = destRelativePath,
                    fileSize = targetFile.length(),
                    addedAt = System.currentTimeMillis()
                )
                database.documentDao().insertDocuments(listOf(entity))
            }
        }

        seedDefaultFallbackPdfs()
    }

    private suspend fun seedDefaultFallbackPdfs() {
        val colleges = listOf("geu", "gehu")
        val sampleSubjects = listOf("tcs101", "tee101", "thu101", "tma101", "tph101")
        val currentTime = System.currentTimeMillis()

        for (collegeId in colleges) {
            val uni = database.universityDao().getUniversityByIdDirect(collegeId) ?: continue
            for (subKey in sampleSubjects) {
                val subject = database.academicDao().getSubjectByIdDirect("${collegeId}_$subKey") ?: continue

                val existingNotes = database.documentDao().getDocumentCountDirect(subject.id, "NOTES")
                if (existingNotes == 0) {
                    val title = "${subject.subjectCode} - Complete Lecture Notes"
                    val fileName = "${subject.id}_sample_notes.pdf"
                    val relativePath = "$collegeId/notes/$fileName"
                    val bytes = createSamplePdf(
                        title = title,
                        subtitle = "${uni.name} - ${subject.subjectName} (${subject.subjectCode})",
                        category = "Course Notes",
                        modules = listOf(
                            "Unit 1: Fundamentals, Architecture & Theoretical Models",
                            "Unit 2: Detailed Concepts, Derivations & Code Examples",
                            "Unit 3: Complex Applications, Design Patterns & Optimizations",
                            "Unit 4: Review Problems, Formulas & Frequently Asked Questions"
                        )
                    )
                    fileStorageManager.writeBytes(relativePath, bytes)
                    database.documentDao().insertDocuments(
                        listOf(
                            DocumentEntity(
                                id = "${subject.id}_fallback_notes",
                                universityId = collegeId,
                                branchId = subject.branchId,
                                semesterId = subject.semesterId,
                                subjectId = subject.id,
                                documentTitle = title,
                                description = "Comprehensive notes covering exam concepts and derivations.",
                                documentType = "NOTES",
                                fileName = fileName,
                                relativeFilePath = relativePath,
                                fileSize = bytes.size.toLong(),
                                addedAt = currentTime
                            )
                        )
                    )
                }

                val existingSyllabus = database.documentDao().getDocumentCountDirect(subject.id, "SYLLABUS")
                if (existingSyllabus == 0) {
                    val title = "${subject.subjectCode} - Official Course Scheme & Syllabus"
                    val fileName = "${subject.id}_sample_syllabus.pdf"
                    val relativePath = "$collegeId/syllabus/$fileName"
                    val bytes = createSamplePdf(
                        title = title,
                        subtitle = "${uni.name} - Academic Scheme for ${subject.subjectCode}",
                        category = "Official Syllabus",
                        modules = listOf(
                            "Section 1: Course Overview, Prerequisites & Credit Allocation",
                            "Section 2: Unit-wise Detailed Syllabus Breakdown & Teaching Hours",
                            "Section 3: Internal Assessment Pattern, Lab Experiments & Viva",
                            "Section 4: Standard Recommended Textbooks and Reference Authors"
                        )
                    )
                    fileStorageManager.writeBytes(relativePath, bytes)
                    database.documentDao().insertDocuments(
                        listOf(
                            DocumentEntity(
                                id = "${subject.id}_fallback_syllabus",
                                universityId = collegeId,
                                branchId = subject.branchId,
                                semesterId = subject.semesterId,
                                subjectId = subject.id,
                                documentTitle = title,
                                description = "Approved course scheme, credit structure, and exam pattern.",
                                documentType = "SYLLABUS",
                                fileName = fileName,
                                relativeFilePath = relativePath,
                                fileSize = bytes.size.toLong(),
                                addedAt = currentTime
                            )
                        )
                    )
                }

                val existingPyq = database.documentDao().getDocumentCountDirect(subject.id, "PYQ")
                if (existingPyq == 0) {
                    val title = "${subject.subjectCode} - End Term Question Paper (2024)"
                    val fileName = "${subject.id}_sample_pyq.pdf"
                    val relativePath = "$collegeId/pyq/$fileName"
                    val bytes = createSamplePdf(
                        title = title,
                        subtitle = "${uni.name} - University Examination Paper",
                        category = "Previous Year Question Paper (PYQ)",
                        modules = listOf(
                            "Part A: Compulsory Short Answer Questions (20 Marks)",
                            "Part B: Analytical & Derivation Problems - Unit 1 & 2 (30 Marks)",
                            "Part C: Design & Algorithm Implementation - Unit 3 (25 Marks)",
                            "Part D: Comprehensive Case Study & Solutions - Unit 4 (25 Marks)"
                        )
                    )
                    fileStorageManager.writeBytes(relativePath, bytes)
                    database.documentDao().insertDocuments(
                        listOf(
                            DocumentEntity(
                                id = "${subject.id}_fallback_pyq",
                                universityId = collegeId,
                                branchId = subject.branchId,
                                semesterId = subject.semesterId,
                                subjectId = subject.id,
                                documentTitle = title,
                                description = "Previous Year University Question Paper with marking scheme.",
                                documentType = "PYQ",
                                fileName = fileName,
                                relativeFilePath = relativePath,
                                fileSize = bytes.size.toLong(),
                                addedAt = currentTime
                            )
                        )
                    )
                }
            }
        }
    }

    private fun listAssetFilesRecursively(assetPath: String): List<String> {
        val result = mutableListOf<String>()
        val entries = try {
            context.assets.list(assetPath) ?: emptyArray()
        } catch (_: Exception) {
            emptyArray()
        }
        for (entry in entries) {
            val subPath = if (assetPath.isEmpty()) entry else "$assetPath/$entry"
            val subEntries = try {
                context.assets.list(subPath) ?: emptyArray()
            } catch (_: Exception) {
                emptyArray()
            }
            if (subEntries.isEmpty()) {
                if (entry.endsWith(".pdf", ignoreCase = true) || entry.endsWith(".pdf__", ignoreCase = true)) {
                    result.add(subPath)
                }
            } else {
                result.addAll(listAssetFilesRecursively(subPath))
            }
        }
        return result
    }

    private fun extractSubjectCode(path: String): String {
        val regex = Regex("(TCS|TEE|THU|TMA|TPH|HSMC|TEC|TME|TEV|BCS|BAS|BEE|CS)[-_ ]?([0-9]{3})", RegexOption.IGNORE_CASE)
        val match = regex.find(path)
        return if (match != null) {
            val prefix = match.groupValues[1].uppercase()
            val num = match.groupValues[2]
            "$prefix-$num"
        } else {
            "TCS-101"
        }
    }

    private fun buildDocumentTitle(subjectCode: String, fileName: String, docType: String): String {
        val nameWithoutExt = fileName.substringBeforeLast(".")
            .replace("_", " ")
            .replace("-", " ")
            .trim()
        val typeLabel = when (docType) {
            "NOTES" -> "Notes"
            "SYLLABUS" -> "Syllabus"
            "PYQ" -> "Question Paper"
            else -> "Document"
        }
        return if (nameWithoutExt.contains(subjectCode, ignoreCase = true)) {
            nameWithoutExt
        } else {
            "$subjectCode $typeLabel - $nameWithoutExt"
        }
    }

    private fun createSamplePdf(
        title: String,
        subtitle: String,
        category: String,
        modules: List<String>
    ): ByteArray {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.rgb(26, 35, 126)
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val paintSub = Paint().apply {
            color = Color.rgb(69, 90, 100)
            textSize = 13f
            isAntiAlias = true
        }

        val paintCategory = Paint().apply {
            color = Color.rgb(0, 105, 92)
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val paintHeader = Paint().apply {
            color = Color.rgb(33, 33, 33)
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val paintBody = Paint().apply {
            color = Color.rgb(55, 71, 79)
            textSize = 11f
            isAntiAlias = true
        }

        val paintLine = Paint().apply {
            color = Color.rgb(197, 202, 233)
            strokeWidth = 2f
        }

        canvas.drawText(title, 40f, 50f, paintTitle)
        canvas.drawText(subtitle, 40f, 72f, paintSub)
        canvas.drawText("Document Type: $category", 40f, 90f, paintCategory)
        canvas.drawLine(40f, 105f, 555f, 105f, paintLine)

        var y = 140f
        for (module in modules) {
            canvas.drawText(module, 40f, y, paintHeader)
            y += 24f

            val lorem = "Covers essential principles, derivations, and official university syllabus patterns."
            canvas.drawText(lorem, 40f, y, paintBody)
            y += 18f

            val bullet1 = "• Detailed theoretical foundation and practical code implementations."
            val bullet2 = "• Examination problem solving, numerical derivation, and architectural diagrams."
            val bullet3 = "• Previous year question paper solutions and standard marking scheme."
            canvas.drawText(bullet1, 55f, y, paintBody)
            y += 18f
            canvas.drawText(bullet2, 55f, y, paintBody)
            y += 18f
            canvas.drawText(bullet3, 55f, y, paintBody)
            y += 32f
        }

        val paintFooter = Paint().apply {
            color = Color.rgb(120, 144, 156)
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawLine(40f, 790f, 555f, 790f, paintLine)
        canvas.drawText("UniShare Offline Academic Companion - Graphic Era", 40f, 810f, paintFooter)
        canvas.drawText("Page 1 of 1", 500f, 810f, paintFooter)

        document.finishPage(page)

        val outputStream = ByteArrayOutputStream()
        document.writeTo(outputStream)
        document.close()
        return outputStream.toByteArray()
    }
}
