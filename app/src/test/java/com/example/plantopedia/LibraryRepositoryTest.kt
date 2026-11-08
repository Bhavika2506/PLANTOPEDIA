package com.example.plantopedia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryRepositoryTest {

    @Test
    fun testAllSixteenCropsPresent() {
        val crops = LibraryRepository.crops
        assertEquals(16, crops.size)

        val expectedCrops = listOf(
            "Rice", "Wheat", "Cotton", "Sugarcane", "Maize",
            "Tomato", "Potato", "Onion", "Soybean", "Groundnut",
            "Turmeric", "Chilli", "Banana", "Mango", "Grapes", "Chickpea"
        )

        expectedCrops.forEach { expectedId ->
            val crop = LibraryRepository.getCropById(expectedId)
            assertNotNull("Crop $expectedId should exist in repository", crop)
            assertTrue("Crop $expectedId should have English name", crop!!.nameEn.isNotBlank())
            assertTrue("Crop $expectedId should have Hindi name", crop.nameHi.isNotBlank())
            assertTrue("Crop $expectedId should have Marathi name", crop.nameMr.isNotBlank())
            assertTrue("Crop $expectedId should have scientific name", crop.scientificName.isNotBlank())
            assertTrue("Crop $expectedId should have suitable climate", crop.suitableClimate.isNotBlank())
            assertTrue("Crop $expectedId should have ideal temperature", crop.idealTemperature.isNotBlank())
            assertTrue("Crop $expectedId should have soil requirements", crop.soilRequirements.isNotBlank())
            assertTrue("Crop $expectedId should have soil pH", crop.soilPh.isNotBlank())
            assertTrue("Crop $expectedId should have water requirements", crop.waterRequirements.isNotBlank())
            assertTrue("Crop $expectedId should have sowing season", crop.sowingSeason.isNotBlank())
            assertTrue("Crop $expectedId should have growing duration", crop.growingDuration.isNotBlank())
            assertTrue("Crop $expectedId should have fertilizer requirements", crop.fertilizerRequirements.isNotBlank())
            assertTrue("Crop $expectedId should have common pests and diseases", crop.commonPestsAndDiseases.isNotBlank())
            assertTrue("Crop $expectedId should have harvesting info", crop.harvestingInformation.isNotBlank())
            assertTrue("Crop $expectedId should have companion crops", crop.companionCrops.isNotBlank())
            assertTrue("Crop $expectedId should have crop rotation", crop.cropRotation.isNotBlank())
        }
    }

    @Test
    fun testDiseasesForCrops() {
        val tomatoDiseases = LibraryRepository.getDiseasesForCrop("Tomato")
        assertTrue("Tomato should have stage-wise diseases", tomatoDiseases.isNotEmpty())

        val disease = LibraryRepository.getDiseaseById("tom_damping_off")
        assertNotNull(disease)
        assertEquals("Seedling Stage", disease!!.stage)
        assertTrue(disease.symptomsEn.isNotBlank())
        assertTrue(disease.causesEn.isNotBlank())
        assertTrue(disease.conditionsEn.isNotBlank())
        assertTrue(disease.preventionEn.isNotBlank())
        assertTrue(disease.treatmentEn.isNotBlank())
        assertTrue(disease.managementEn.isNotBlank())
    }

    @Test
    fun testCultivationTips() {
        val tomatoTips = LibraryRepository.getTipsForCrop("Tomato")
        assertTrue("Should have cultivation tips for Tomato", tomatoTips.isNotEmpty())

        val taskTips = tomatoTips.filter { it.categoryType == "TASK" }
        val stageTips = tomatoTips.filter { it.categoryType == "STAGE" }

        assertTrue("Should contain task-based tips", taskTips.isNotEmpty())
        assertTrue("Should contain stage-based tips", stageTips.isNotEmpty())

        val tip = LibraryRepository.getTipById("tom_task_site_selection")
        assertNotNull(tip)
        assertTrue(tip!!.detailedStepsEn.isNotEmpty())
        assertTrue(tip.detailedStepsHi.isNotEmpty())
        assertTrue(tip.detailedStepsMr.isNotEmpty())
    }
}
