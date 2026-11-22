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
        val existingGeu = database.universityDao().getUniversityByIdDirect("geu")
        if (existingGeu != null) {
            syncAssetDocuments()
            return@withContext
        }

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
                SubjectEntity("${uni.id}_bcs101", cseBranchId, sem1Id, "Programming for Problem Solving", "BCS101", "C programming fundamentals, flowcharts, arrays, functions, and pointers."),
                SubjectEntity("${uni.id}_bas101", cseBranchId, sem1Id, "Engineering Mathematics - I", "BAS101", "Calculus, matrices, linear algebra, and differential equations."),
                SubjectEntity("${uni.id}_bcs201", cseBranchId, sem2Id, "Advanced C & Data Structures", "BCS201", "Dynamic memory allocation, stacks, queues, and linked lists."),
                SubjectEntity("${uni.id}_bas201", cseBranchId, sem2Id, "Engineering Mathematics - II", "BAS201", "Vector calculus, Fourier series, and Laplace transforms."),
                SubjectEntity("${uni.id}_bcs301", cseBranchId, sem3Id, "Data Structures & Algorithms", "BCS301", "Trees, graphs, hashing, sorting algorithms, and complexity analysis."),
                SubjectEntity("${uni.id}_bcs302", cseBranchId, sem3Id, "Object Oriented Programming (Java)", "BCS302", "Classes, inheritance, polymorphism, interfaces, and exception handling."),
                SubjectEntity("${uni.id}_bcs303", cseBranchId, sem3Id, "Discrete Mathematical Structures", "BCS303", "Set theory, propositional logic, graph theory, and combinatorics."),
                SubjectEntity("${uni.id}_bcs401", cseBranchId, sem4Id, "Operating Systems", "BCS401", "Process management, CPU scheduling, synchronization, and memory management."),
                SubjectEntity("${uni.id}_bcs402", cseBranchId, sem4Id, "Design & Analysis of Algorithms", "BCS402", "Divide and conquer, greedy methods, dynamic programming, and NP-completeness."),
                SubjectEntity("${uni.id}_bcs403", cseBranchId, sem4Id, "Database Management Systems", "BCS403", "Relational models, SQL queries, ER modeling, normalization, and transactions."),
                SubjectEntity("${uni.id}_bcs501", cseBranchId, sem5Id, "Computer Networks", "BCS501", "OSI reference layers, TCP/IP, IP addressing, routing algorithms, and socket APIs."),
                SubjectEntity("${uni.id}_bcs502", cseBranchId, sem5Id, "Software Engineering", "BCS502", "Agile methodologies, software architecture, design patterns, and unit testing.")
            )
            database.academicDao().insertSubjects(subjects)

            val documents = mutableListOf<DocumentEntity>()
            val currentTime = System.currentTimeMillis()

            for (subject in subjects) {
                val notesTitle = "${subject.subjectCode} - Complete Lecture Notes"
                val notesFileName = "${subject.id}_notes.pdf"
                val notesRelativePath = "${uni.id}/notes/$notesFileName"
                val notesPdfBytes = createSamplePdf(
                    title = notesTitle,
                    subtitle = "${uni.name} - ${subject.subjectName} (${subject.subjectCode})",
                    category = "Course Notes & Study Material",
                    modules = listOf(
                        "Unit 1: Fundamentals, Architecture & Theoretical Models",
                        "Unit 2: Detailed Concepts, Derivations & Code Examples",
                        "Unit 3: Complex Applications, Design Patterns & Optimizations",
                        "Unit 4: Review Problems, Formulas & Frequently Asked Questions"
                    )
                )
                fileStorageManager.writeBytes(notesRelativePath, notesPdfBytes)
                documents.add(
                    DocumentEntity(
                        id = "${subject.id}_notes_doc",
                        universityId = uni.id,
                        branchId = subject.branchId,
                        semesterId = subject.semesterId,
                        subjectId = subject.id,
                        documentTitle = notesTitle,
                        description = "Lecture notes covering key concepts, diagrams, and definitions for ${subject.subjectCode}.",
                        documentType = "NOTES",
                        fileName = notesFileName,
                        relativeFilePath = notesRelativePath,
                        fileSize = notesPdfBytes.size.toLong(),
                        addedAt = currentTime
                    )
                )

                val syllabusTitle = "${subject.subjectCode} - Official Course Scheme & Syllabus"
                val syllabusFileName = "${subject.id}_syllabus.pdf"
                val syllabusRelativePath = "${uni.id}/syllabus/$syllabusFileName"
                val syllabusPdfBytes = createSamplePdf(
                    title = syllabusTitle,
                    subtitle = "${uni.name} - Academic Scheme for ${subject.subjectCode}",
                    category = "Official University Syllabus",
                    modules = listOf(
                        "Section 1: Course Overview, Prerequisites & Credit Allocation",
                        "Section 2: Unit-wise Detailed Syllabus Breakdown & Teaching Hours",
                        "Section 3: Internal Assessment Pattern, Lab Experiments & Viva",
                        "Section 4: Standard Recommended Textbooks and Reference Authors"
                    )
                )
                fileStorageManager.writeBytes(syllabusRelativePath, syllabusPdfBytes)
                documents.add(
                    DocumentEntity(
                        id = "${subject.id}_syllabus_doc",
                        universityId = uni.id,
                        branchId = subject.branchId,
                        semesterId = subject.semesterId,
                        subjectId = subject.id,
                        documentTitle = syllabusTitle,
                        description = "Approved university syllabus copy, credit structure, and exam scheme for ${subject.subjectCode}.",
                        documentType = "SYLLABUS",
                        fileName = syllabusFileName,
                        relativeFilePath = syllabusRelativePath,
                        fileSize = syllabusPdfBytes.size.toLong(),
                        addedAt = currentTime
                    )
                )

                val pyqTitle = "${subject.subjectCode} - End Term Question Paper (2024)"
                val pyqFileName = "${subject.id}_pyq_2024.pdf"
                val pyqRelativePath = "${uni.id}/pyq/$pyqFileName"
                val pyqPdfBytes = createSamplePdf(
                    title = pyqTitle,
                    subtitle = "${uni.name} - University Examination Paper",
                    category = "Previous Year Question Paper (PYQ)",
                    modules = listOf(
                        "Part A: Compulsory Short Answer Questions (20 Marks)",
                        "Part B: Analytical & Derivation Problems - Unit 1 & Unit 2 (30 Marks)",
                        "Part C: Design & Algorithm Implementation - Unit 3 (25 Marks)",
                        "Part D: Case Study, Comprehensive Solutions - Unit 4 (25 Marks)"
                    )
                )
                fileStorageManager.writeBytes(pyqRelativePath, pyqPdfBytes)
                documents.add(
                    DocumentEntity(
                        id = "${subject.id}_pyq_doc",
                        universityId = uni.id,
                        branchId = subject.branchId,
                        semesterId = subject.semesterId,
                        subjectId = subject.id,
                        documentTitle = pyqTitle,
                        description = "Previous Year University Question Paper with standard marking scheme for ${subject.subjectCode}.",
                        documentType = "PYQ",
                        fileName = pyqFileName,
                        relativeFilePath = pyqRelativePath,
                        fileSize = pyqPdfBytes.size.toLong(),
                        addedAt = currentTime
                    )
                )
            }

            database.documentDao().insertDocuments(documents)
        }

        syncAssetDocuments()
    }

    private suspend fun syncAssetDocuments() {
        val assetManager = context.assets
        val colleges = listOf("geu", "gehu", "dit", "upes", "doon", "utu")
        val types = listOf(
            Triple("notes", "NOTES", "Lecture Notes"),
            Triple("syllabus", "SYLLABUS", "Syllabus"),
            Triple("pyq", "PYQ", "Question Paper")
        )

        for (collegeId in colleges) {
            for ((folderName, docType, label) in types) {
                val assetPath = "documents/$collegeId/$folderName"
                val files = try {
                    assetManager.list(assetPath) ?: emptyArray()
                } catch (_: Exception) {
                    emptyArray()
                }

                for (fileName in files) {
                    if (!fileName.endsWith(".pdf", ignoreCase = true)) continue

                    val relativePath = "$collegeId/$folderName/$fileName"
                    val targetFile = fileStorageManager.getFile(relativePath)
                    if (!targetFile.exists()) {
                        try {
                            assetManager.open("$assetPath/$fileName").use { input ->
                                targetFile.parentFile?.let { if (!it.exists()) it.mkdirs() }
                                FileOutputStream(targetFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                        } catch (_: Exception) {
                            continue
                        }
                    }

                    val codePrefix = fileName.substringBefore("_").substringBefore(".").uppercase()
                    val matchingSubjectId = "${collegeId}_${codePrefix.lowercase()}"
                    val subject = database.academicDao().getSubjectByIdDirect(matchingSubjectId)

                    val docId = "${collegeId}_${folderName}_${fileName.substringBeforeLast(".")}"
                    val existing = database.documentDao().getDocumentByIdDirect(docId)

                    if (existing == null && subject != null) {
                        val docTitle = "$codePrefix - ${fileName.substringBeforeLast(".").replace("_", " ")}"
                        val entity = DocumentEntity(
                            id = docId,
                            universityId = collegeId,
                            branchId = subject.branchId,
                            semesterId = subject.semesterId,
                            subjectId = subject.id,
                            documentTitle = docTitle,
                            description = "$label for $codePrefix ($fileName)",
                            documentType = docType,
                            fileName = fileName,
                            relativeFilePath = relativePath,
                            fileSize = targetFile.length(),
                            addedAt = System.currentTimeMillis()
                        )
                        database.documentDao().insertDocuments(listOf(entity))
                    }
                }
            }
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
        canvas.drawText("UniShare Offline Academic Companion - Dehradun Colleges", 40f, 810f, paintFooter)
        canvas.drawText("Page 1 of 1", 500f, 810f, paintFooter)

        document.finishPage(page)

        val outputStream = ByteArrayOutputStream()
        document.writeTo(outputStream)
        document.close()
        return outputStream.toByteArray()
    }
}
