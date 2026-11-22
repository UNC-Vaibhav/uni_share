package com.example.unishare

import com.example.unishare.data.local.entity.UniversityEntity
import com.example.unishare.data.model.AcademicYear
import com.example.unishare.ui.components.formatFileSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun academicYear_fromDisplayName_matchesCorrectly() {
        assertEquals(AcademicYear.FIRST_YEAR, AcademicYear.fromDisplayName("First Year"))
        assertEquals(AcademicYear.SECOND_YEAR, AcademicYear.fromDisplayName("second year"))
        assertEquals(AcademicYear.THIRD_YEAR, AcademicYear.fromDisplayName("THIRD YEAR"))
        assertEquals(AcademicYear.FOURTH_YEAR, AcademicYear.fromDisplayName("Fourth Year"))
        assertNull(AcademicYear.fromDisplayName("Fifth Year"))
    }

    @Test
    fun universitySearch_filtersDehradunColleges() {
        val universities = listOf(
            UniversityEntity("geu", "Graphic Era Deemed to be University", "GEU", "ic_logo_geu"),
            UniversityEntity("gehu", "Graphic Era Hill University", "GEHU", "ic_logo_gehu"),
            UniversityEntity("dit", "DIT University", "DIT", "ic_logo_dit"),
            UniversityEntity("upes", "UPES - University of Petroleum & Energy Studies", "UPES", "ic_logo_upes")
        )

        val queryGraphic = "graphic"
        val filteredGraphic = universities.filter {
            it.name.lowercase().contains(queryGraphic) || it.abbreviation.lowercase().contains(queryGraphic)
        }
        assertEquals(2, filteredGraphic.size)

        val queryHill = "hill"
        val filteredHill = universities.filter {
            it.name.lowercase().contains(queryHill) || it.abbreviation.lowercase().contains(queryHill)
        }
        assertEquals(1, filteredHill.size)
        assertEquals("Graphic Era Hill University", filteredHill.first().name)

        val queryGeu = "geu"
        val filteredGeu = universities.filter {
            it.name.lowercase().contains(queryGeu) || it.abbreviation.lowercase().contains(queryGeu)
        }
        assertEquals(1, filteredGeu.size)
        assertEquals("Graphic Era Deemed to be University", filteredGeu.first().name)
    }

    @Test
    fun formatFileSize_convertsBytesAccurately() {
        assertEquals("0 B", formatFileSize(0))
        assertEquals("100 KB", formatFileSize(100 * 1024))
        assertEquals("2.5 MB", formatFileSize((2.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun subjectCodePrefix_extractionFromFilename() {
        val filename1 = "BCS101_Notes_Unit1.pdf"
        val code1 = filename1.substringBefore("_").substringBefore(".").uppercase()
        assertEquals("BCS101", code1)

        val filename2 = "BCS301_EndTerm_2024.pdf"
        val code2 = filename2.substringBefore("_").substringBefore(".").uppercase()
        assertEquals("BCS301", code2)
    }
}
