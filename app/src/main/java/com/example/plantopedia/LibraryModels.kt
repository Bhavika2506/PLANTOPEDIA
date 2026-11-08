package com.example.plantopedia

import android.content.Context

data class CropLibraryItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameMr: String,
    val scientificName: String,
    val category: String, // Cereal, Cash Crop, Vegetable, Fruit, Pulse, Spice
    val emoji: String,
    val assetImagePath: String? = null,
    val overviewEn: String,
    val overviewHi: String,
    val overviewMr: String,
    val suitableClimate: String,
    val idealTemperature: String,
    val soilRequirements: String,
    val soilPh: String,
    val waterRequirements: String,
    val sowingSeason: String,
    val growingDuration: String,
    val fertilizerRequirements: String,
    val commonPestsAndDiseases: String,
    val harvestingInformation: String,
    val companionCrops: String,
    val cropRotation: String
)

data class DiseasePestItem(
    val id: String,
    val cropId: String,
    val nameEn: String,
    val nameHi: String,
    val nameMr: String,
    val isPest: Boolean,
    val stage: String, // Seedling, Vegetative, Flowering, Fruiting, Harvesting
    val assetImagePath: String? = null,
    val symptomsEn: String,
    val symptomsHi: String,
    val symptomsMr: String,
    val causesEn: String,
    val causesHi: String,
    val causesMr: String,
    val conditionsEn: String,
    val conditionsHi: String,
    val conditionsMr: String,
    val preventionEn: String,
    val preventionHi: String,
    val preventionMr: String,
    val treatmentEn: String,
    val treatmentHi: String,
    val treatmentMr: String,
    val managementEn: String,
    val managementHi: String,
    val managementMr: String
)

data class CultivationTipItem(
    val id: String,
    val cropId: String,
    val titleEn: String,
    val titleHi: String,
    val titleMr: String,
    val categoryType: String, // "TASK" or "STAGE"
    val categoryKey: String, // e.g. "Plant Selection", "Irrigation", "Vegetative Stage"
    val iconName: String,
    val summaryEn: String,
    val summaryHi: String,
    val summaryMr: String,
    val detailedStepsEn: List<String>,
    val detailedStepsHi: List<String>,
    val detailedStepsMr: List<String>
)
