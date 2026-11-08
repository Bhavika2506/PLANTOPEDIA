package com.example.plantopedia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherTest {

    @Test
    fun testWeatherJsonParsing() {
        val json = """
            {
                "latitude": 18.52,
                "longitude": 73.86,
                "current": {
                    "time": "2026-11-08T14:00",
                    "temperature_2m": 27.5,
                    "apparent_temperature": 28.1,
                    "relative_humidity_2m": 58,
                    "precipitation": 0.0,
                    "weather_code": 1,
                    "wind_speed_10m": 9.2,
                    "wind_direction_10m": 120.0
                },
                "hourly": {
                    "time": ["2026-11-08T14:00", "2026-11-08T15:00", "2026-11-08T16:00"],
                    "temperature_2m": [27.5, 26.8, 25.4],
                    "relative_humidity_2m": [58, 62, 65],
                    "precipitation_probability": [5, 10, 15],
                    "precipitation": [0.0, 0.0, 0.0],
                    "weather_code": [1, 2, 2],
                    "wind_speed_10m": [9.2, 8.5, 7.8],
                    "wind_gusts_10m": [12.0, 11.5, 10.2]
                }
            }
        """.trimIndent()

        val parsed = WeatherService.parseWeatherJson(
            jsonString = json,
            locationName = "Pune, Maharashtra",
            lat = 18.52,
            lon = 73.86,
            lastUpdatedMillis = 1700000000000L
        )

        assertEquals("Pune, Maharashtra", parsed.locationName)
        assertEquals(27.5, parsed.current.temperature, 0.01)
        assertEquals(58, parsed.current.relativeHumidity)
        assertEquals(9.2, parsed.current.windSpeed, 0.01)
        assertEquals(3, parsed.hourly.size)
        assertEquals("2 PM", parsed.hourly[0].formattedHour)
    }

    @Test
    fun testSprayingGuidance_OptimalConditions() {
        val weatherData = WeatherData(
            locationName = "Pune",
            latitude = 18.52,
            longitude = 73.86,
            current = CurrentWeatherData(
                time = "2026-11-08T09:00",
                temperature = 22.0,
                apparentTemperature = 22.5,
                relativeHumidity = 60,
                precipitation = 0.0,
                weatherCode = 1,
                windSpeed = 8.0,
                windDirection = 90.0
            ),
            hourly = listOf(
                HourlyForecastItem("2026-11-08T09:00", "9 AM", 22.0, 60, 5, 0.0, 1, 8.0, 10.0),
                HourlyForecastItem("2026-11-08T10:00", "10 AM", 24.0, 55, 5, 0.0, 1, 9.0, 12.0),
                HourlyForecastItem("2026-11-08T11:00", "11 AM", 26.0, 52, 10, 0.0, 1, 10.0, 13.0)
            ),
            lastUpdatedMillis = 1000L
        )

        val fakeResolver = object : StringResolver {
            override fun getString(resId: Int): String {
                return when (resId) {
                    R.string.wind_label -> "Wind Speed"
                    R.string.humidity_label -> "Humidity"
                    R.string.rainfall_label -> "Rainfall"
                    R.string.exp_optimal -> "Optimal conditions"
                    else -> "String_$resId"
                }
            }
        }

        val assessment = SprayingGuidanceEngine.evaluate(weatherData, fakeResolver)
        assertEquals(SprayingStatus.SUITABLE, assessment.status)
        assertEquals(R.string.exp_optimal, assessment.summaryExplanationResId)
    }

    @Test
    fun testSprayingGuidance_HighWindNotSuitable() {
        val weatherData = WeatherData(
            locationName = "Pune",
            latitude = 18.52,
            longitude = 73.86,
            current = CurrentWeatherData(
                time = "2026-11-08T14:00",
                temperature = 25.0,
                apparentTemperature = 25.0,
                relativeHumidity = 55,
                precipitation = 0.0,
                weatherCode = 1,
                windSpeed = 24.5, // High wind
                windDirection = 90.0
            ),
            hourly = listOf(
                HourlyForecastItem("2026-11-08T14:00", "2 PM", 25.0, 55, 5, 0.0, 1, 24.5, 30.0)
            ),
            lastUpdatedMillis = 1000L
        )

        val fakeResolver = object : StringResolver {
            override fun getString(resId: Int): String = "Text_$resId"
        }

        val assessment = SprayingGuidanceEngine.evaluate(weatherData, fakeResolver)
        assertEquals(SprayingStatus.NOT_SUITABLE, assessment.status)
        assertEquals(R.string.exp_high_wind, assessment.summaryExplanationResId)
    }

    @Test
    fun testSprayingGuidance_RainExpectedNotSuitable() {
        val weatherData = WeatherData(
            locationName = "Pune",
            latitude = 18.52,
            longitude = 73.86,
            current = CurrentWeatherData(
                time = "2026-11-08T14:00",
                temperature = 24.0,
                apparentTemperature = 24.0,
                relativeHumidity = 75,
                precipitation = 0.0,
                weatherCode = 3,
                windSpeed = 8.0,
                windDirection = 180.0
            ),
            hourly = listOf(
                HourlyForecastItem("2026-11-08T14:00", "2 PM", 24.0, 75, 70, 1.5, 61, 8.0, 12.0)
            ),
            lastUpdatedMillis = 1000L
        )

        val fakeResolver = object : StringResolver {
            override fun getString(resId: Int): String = "Text_$resId"
        }

        val assessment = SprayingGuidanceEngine.evaluate(weatherData, fakeResolver)
        assertEquals(SprayingStatus.NOT_SUITABLE, assessment.status)
        assertEquals(R.string.exp_rain_expected, assessment.summaryExplanationResId)
    }

    @Test
    fun testSprayingGuidance_NullWeatherData() {
        val fakeResolver = object : StringResolver {
            override fun getString(resId: Int): String = "Text_$resId"
        }

        val assessment = SprayingGuidanceEngine.evaluate(null, fakeResolver)
        assertEquals(SprayingStatus.INSUFFICIENT_DATA, assessment.status)
        assertEquals(R.string.exp_insufficient_data, assessment.summaryExplanationResId)
    }

    @Test
    fun testWindDirectionCardinal() {
        assertEquals("N", WeatherService.getWindDirectionCardinal(0.0))
        assertEquals("E", WeatherService.getWindDirectionCardinal(90.0))
        assertEquals("S", WeatherService.getWindDirectionCardinal(180.0))
        assertEquals("W", WeatherService.getWindDirectionCardinal(270.0))
    }
}
