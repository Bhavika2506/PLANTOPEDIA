package com.example.plantopedia

import android.content.Context

enum class SprayingStatus {
    SUITABLE,
    USE_CAUTION,
    NOT_SUITABLE,
    INSUFFICIENT_DATA
}

enum class FactorRating {
    FAVORABLE,
    CAUTION,
    UNFAVORABLE
}

data class SprayingFactor(
    val name: String,
    val currentValue: String,
    val recommendedRange: String,
    val rating: FactorRating,
    val explanation: String
)

data class UpcomingSprayingSlot(
    val titleResId: Int,
    val timeLabel: String,
    val status: SprayingStatus,
    val temperature: String,
    val wind: String,
    val rainProb: String
)

data class SprayingAssessment(
    val status: SprayingStatus,
    val summaryExplanationResId: Int,
    val factors: List<SprayingFactor>,
    val upcomingSlots: List<UpcomingSprayingSlot>
)

interface StringResolver {
    fun getString(resId: Int): String
}

object SprayingGuidanceEngine {

    fun evaluate(weatherData: WeatherData?, context: Context): SprayingAssessment {
        return evaluate(weatherData, object : StringResolver {
            override fun getString(resId: Int): String = context.getString(resId)
        })
    }

    fun evaluate(weatherData: WeatherData?, stringResolver: StringResolver): SprayingAssessment {
        if (weatherData == null) {
            return SprayingAssessment(
                status = SprayingStatus.INSUFFICIENT_DATA,
                summaryExplanationResId = R.string.exp_insufficient_data,
                factors = emptyList(),
                upcomingSlots = emptyList()
            )
        }

        val current = weatherData.current
        val hourly = weatherData.hourly

        // Factor 1: Temperature (Ideal: 15-28°C, Caution: 10-15°C or 28-32°C, Unfavorable: >32°C or <10°C)
        val temp = current.temperature
        val tempRating = when {
            temp in 15.0..28.0 -> FactorRating.FAVORABLE
            temp in 10.0..14.9 || temp in 28.1..32.0 -> FactorRating.CAUTION
            else -> FactorRating.UNFAVORABLE
        }
        val tempExplanation = when {
            temp > 32.0 -> stringResolver.getString(R.string.exp_high_temp)
            temp < 10.0 -> stringResolver.getString(R.string.exp_low_temp)
            temp in 28.1..32.0 -> "Moderate heat may increase droplet evaporation. Consider early morning spraying."
            temp in 10.0..14.9 -> "Cooler weather may slow down pesticide penetration."
            else -> "Optimal temperature range for foliar application."
        }

        // Factor 2: Wind Speed & Gusts (Ideal: 3-15 km/h, Caution: 15-20 km/h or <3 km/h, Unfavorable: >20 km/h)
        val wind = current.windSpeed
        val nextGust = hourly.firstOrNull()?.windGusts ?: wind
        val windRating = when {
            wind > 20.0 || nextGust > 25.0 -> FactorRating.UNFAVORABLE
            wind in 3.0..15.0 && nextGust <= 20.0 -> FactorRating.FAVORABLE
            else -> FactorRating.CAUTION
        }
        val windExplanation = when {
            wind > 20.0 || nextGust > 25.0 -> stringResolver.getString(R.string.exp_high_wind)
            wind < 3.0 -> stringResolver.getString(R.string.exp_calm_wind)
            wind in 15.0..20.0 -> "Breezy conditions. Use low-drift nozzles to avoid chemical drift."
            else -> "Favorable gentle breeze prevents inversion and minimizes drift."
        }

        // Factor 3: Humidity (Ideal: 50-70%, Caution: 40-50% or 70-85%, Unfavorable: <40% or >85%)
        val humidity = current.relativeHumidity
        val humidityRating = when {
            humidity in 50..70 -> FactorRating.FAVORABLE
            humidity in 40..49 || humidity in 71..85 -> FactorRating.CAUTION
            else -> FactorRating.UNFAVORABLE
        }
        val humidityExplanation = when {
            humidity > 85 -> stringResolver.getString(R.string.exp_high_humidity)
            humidity < 40 -> stringResolver.getString(R.string.exp_low_humidity)
            humidity in 71..85 -> "Elevated humidity slows drying; ensure proper plant spacing."
            humidity in 40..49 -> "Slightly dry air; fine droplets may evaporate somewhat faster."
            else -> "Ideal atmospheric moisture for pesticide absorption."
        }

        // Factor 4: Rainfall & Rain Probability (Ideal: 0 mm & <20%, Caution: 20-40%, Unfavorable: >0 mm or >40%)
        val currentRain = current.precipitation
        val nextFewHoursRainProb = hourly.take(3).maxOfOrNull { it.precipitationProbability } ?: 0
        val nextFewHoursPrecip = hourly.take(3).sumOf { it.precipitation }
        val rainRating = when {
            currentRain > 0.1 || nextFewHoursPrecip > 0.5 || nextFewHoursRainProb >= 40 -> FactorRating.UNFAVORABLE
            nextFewHoursRainProb in 20..39 -> FactorRating.CAUTION
            else -> FactorRating.FAVORABLE
        }
        val rainExplanation = when {
            currentRain > 0.1 -> "Active precipitation detected. Spraying will wash off target leaves."
            nextFewHoursPrecip > 0.5 || nextFewHoursRainProb >= 40 -> stringResolver.getString(R.string.exp_rain_expected)
            nextFewHoursRainProb in 20..39 -> "Moderate chance of rain showers within 3 hours. Check rainfast requirements."
            else -> "Dry weather expected. Minimal risk of chemical wash-off."
        }

        // Determine Overall Current Status
        val overallStatus = when {
            windRating == FactorRating.UNFAVORABLE ||
            tempRating == FactorRating.UNFAVORABLE ||
            rainRating == FactorRating.UNFAVORABLE ||
            humidityRating == FactorRating.UNFAVORABLE -> SprayingStatus.NOT_SUITABLE

            windRating == FactorRating.CAUTION ||
            tempRating == FactorRating.CAUTION ||
            rainRating == FactorRating.CAUTION ||
            humidityRating == FactorRating.CAUTION -> SprayingStatus.USE_CAUTION

            else -> SprayingStatus.SUITABLE
        }

        val summaryExplanationResId = when (overallStatus) {
            SprayingStatus.NOT_SUITABLE -> {
                when {
                    rainRating == FactorRating.UNFAVORABLE -> R.string.exp_rain_expected
                    windRating == FactorRating.UNFAVORABLE -> R.string.exp_high_wind
                    temp > 32.0 -> R.string.exp_high_temp
                    temp < 10.0 -> R.string.exp_low_temp
                    humidity > 85 -> R.string.exp_high_humidity
                    humidity < 40 -> R.string.exp_low_humidity
                    else -> R.string.exp_high_wind
                }
            }
            SprayingStatus.USE_CAUTION -> {
                when {
                    wind < 3.0 -> R.string.exp_calm_wind
                    wind in 15.0..20.0 -> R.string.exp_high_wind
                    temp in 28.1..32.0 -> R.string.exp_high_temp
                    rainRating == FactorRating.CAUTION -> R.string.exp_rain_expected
                    else -> R.string.exp_optimal
                }
            }
            SprayingStatus.SUITABLE -> R.string.exp_optimal
            SprayingStatus.INSUFFICIENT_DATA -> R.string.exp_insufficient_data
        }

        val factorsList = listOf(
            SprayingFactor(
                name = stringResolver.getString(R.string.wind_label),
                currentValue = "${current.windSpeed} km/h (Gusts: ${nextGust} km/h)",
                recommendedRange = "3 - 15 km/h",
                rating = windRating,
                explanation = windExplanation
            ),
            SprayingFactor(
                name = "Temperature",
                currentValue = "${current.temperature}°C",
                recommendedRange = "15°C - 28°C",
                rating = tempRating,
                explanation = tempExplanation
            ),
            SprayingFactor(
                name = stringResolver.getString(R.string.humidity_label),
                currentValue = "${current.relativeHumidity}%",
                recommendedRange = "50% - 70%",
                rating = humidityRating,
                explanation = humidityExplanation
            ),
            SprayingFactor(
                name = stringResolver.getString(R.string.rainfall_label),
                currentValue = "${current.precipitation} mm (${nextFewHoursRainProb}% chance)",
                recommendedRange = "0 mm (<20% chance)",
                rating = rainRating,
                explanation = rainExplanation
            )
        )

        // Evaluate Upcoming Slots: Now, Next 2h, Next 4h, Later today (+6h/8h)
        val upcomingSlots = mutableListOf<UpcomingSprayingSlot>()

        // Now
        upcomingSlots.add(
            UpcomingSprayingSlot(
                titleResId = R.string.spraying_now,
                timeLabel = hourly.firstOrNull()?.formattedHour ?: "Now",
                status = overallStatus,
                temperature = "${current.temperature}°C",
                wind = "${current.windSpeed} km/h",
                rainProb = "${hourly.firstOrNull()?.precipitationProbability ?: 0}%"
            )
        )

        // Next 2h
        val slot2h = hourly.getOrNull(2)
        if (slot2h != null) {
            upcomingSlots.add(
                UpcomingSprayingSlot(
                    titleResId = R.string.spraying_next_2h,
                    timeLabel = slot2h.formattedHour,
                    status = evaluateSlotStatus(slot2h),
                    temperature = "${slot2h.temperature}°C",
                    wind = "${slot2h.windSpeed} km/h",
                    rainProb = "${slot2h.precipitationProbability}%"
                )
            )
        }

        // Next 4h
        val slot4h = hourly.getOrNull(4)
        if (slot4h != null) {
            upcomingSlots.add(
                UpcomingSprayingSlot(
                    titleResId = R.string.spraying_next_4h,
                    timeLabel = slot4h.formattedHour,
                    status = evaluateSlotStatus(slot4h),
                    temperature = "${slot4h.temperature}°C",
                    wind = "${slot4h.windSpeed} km/h",
                    rainProb = "${slot4h.precipitationProbability}%"
                )
            )
        }

        // Later Today (+7h)
        val slotLater = hourly.getOrNull(7) ?: hourly.lastOrNull()
        if (slotLater != null) {
            upcomingSlots.add(
                UpcomingSprayingSlot(
                    titleResId = R.string.spraying_later_today,
                    timeLabel = slotLater.formattedHour,
                    status = evaluateSlotStatus(slotLater),
                    temperature = "${slotLater.temperature}°C",
                    wind = "${slotLater.windSpeed} km/h",
                    rainProb = "${slotLater.precipitationProbability}%"
                )
            )
        }

        return SprayingAssessment(
            status = overallStatus,
            summaryExplanationResId = summaryExplanationResId,
            factors = factorsList,
            upcomingSlots = upcomingSlots
        )
    }

    private fun evaluateSlotStatus(slot: HourlyForecastItem): SprayingStatus {
        val temp = slot.temperature
        val wind = slot.windSpeed
        val gust = slot.windGusts
        val humidity = slot.relativeHumidity
        val rainProb = slot.precipitationProbability
        val precip = slot.precipitation

        if (wind > 20.0 || gust > 25.0 || temp > 32.0 || temp < 10.0 ||
            precip > 0.1 || rainProb >= 40 || humidity > 85 || humidity < 40) {
            return SprayingStatus.NOT_SUITABLE
        }

        if (wind < 3.0 || wind in 15.0..20.0 || temp in 28.1..32.0 || temp in 10.0..14.9 ||
            rainProb in 20..39 || humidity in 40..49 || humidity in 71..85) {
            return SprayingStatus.USE_CAUTION
        }

        return SprayingStatus.SUITABLE
    }
}
