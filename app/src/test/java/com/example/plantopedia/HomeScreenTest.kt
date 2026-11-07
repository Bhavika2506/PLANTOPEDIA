package com.example.plantopedia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class HomeScreenTest {

    @Test
    fun testFormatCurrentDate_english() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 6)
        }
        val formatted = formatCurrentDate("en", calendar.time)
        assertEquals("6 Nov", formatted)
    }

    @Test
    fun testFormatCurrentDate_hindi() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 6)
        }
        val formatted = formatCurrentDate("hi", calendar.time)
        assertNotNull(formatted)
        assertTrue("Formatted date should not be empty", formatted.isNotEmpty())
    }

    @Test
    fun testFormatCurrentDate_marathi() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 6)
        }
        val formatted = formatCurrentDate("mr", calendar.time)
        assertNotNull(formatted)
        assertTrue("Formatted date should not be empty", formatted.isNotEmpty())
        assertTrue("Formatted date should contain Marathi numeral or day representation", formatted.contains("६") || formatted.contains("6"))
    }

    @Test
    fun testAvailableCropOptions() {
        assertTrue("Available crop options should not be empty", availableCropOptions.isNotEmpty())
        val tomato = availableCropOptions.find { it.id == "Tomato" }
        assertNotNull(tomato)
        assertEquals("Tomato", tomato?.nameEn)
        assertEquals("टमाटर", tomato?.nameHi)
        assertEquals("टोमॅटो", tomato?.nameMr)
    }
}
