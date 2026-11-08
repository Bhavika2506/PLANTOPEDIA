package com.example.plantopedia

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class LocationInfo(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameMr: String,
    val latitude: Double,
    val longitude: Double
) {
    fun getDisplayName(context: Context): String {
        return when (UserManager.getLanguage(context)) {
            "hi" -> nameHi
            "mr" -> nameMr
            else -> nameEn
        }
    }
}

val defaultAgriculturalLocations = listOf(
    LocationInfo("pune", "Pune, Maharashtra", "पुणे, महाराष्ट्र", "पुणे, महाराष्ट्र", 18.5204, 73.8567),
    LocationInfo("nashik", "Nashik, Maharashtra", "नासिक, महाराष्ट्र", "नाशिक, महाराष्ट्र", 19.9975, 73.7898),
    LocationInfo("nagpur", "Nagpur, Maharashtra", "नागपुर, महाराष्ट्र", "नागपूर, महाराष्ट्र", 21.1458, 79.0882),
    LocationInfo("aurangabad", "Chhatrapati Sambhajinagar, Maharashtra", "छत्रपति संभाजीनगर, महाराष्ट्र", "छत्रपती संभाजीनगर, महाराष्ट्र", 19.8762, 75.3433),
    LocationInfo("kolhapur", "Kolhapur, Maharashtra", "कोल्हापुर, महाराष्ट्र", "कोल्हापूर, महाराष्ट्र", 16.7050, 74.2433),
    LocationInfo("amravati", "Amravati, Maharashtra", "अमरावती, महाराष्ट्र", "अमरावती, महाराष्ट्र", 20.9374, 77.7796),
    LocationInfo("baramati", "Baramati, Maharashtra", "बारामती, महाराष्ट्र", "बारामती, महाराष्ट्र", 18.1519, 74.5770),
    LocationInfo("solapur", "Solapur, Maharashtra", "सोलापुर, महाराष्ट्र", "सोलापूर, महाराष्ट्र", 17.6599, 75.9064),
    LocationInfo("indore", "Indore, Madhya Pradesh", "इंदौर, मध्य प्रदेश", "इंदूर, मध्य प्रदेश", 22.7196, 75.8577),
    LocationInfo("ludhiana", "Ludhiana, Punjab", "लुधियाना, पंजाब", "लुधियाना, पंजाब", 30.9010, 75.8573),
    LocationInfo("karnal", "Karnal, Haryana", "करनाल, हरियाणा", "कर्नाल, हरियाणा", 29.6857, 76.9905)
)

data class CurrentWeatherData(
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Int,
    val precipitation: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val windDirection: Double
)

data class HourlyForecastItem(
    val time: String,
    val formattedHour: String,
    val temperature: Double,
    val relativeHumidity: Int,
    val precipitationProbability: Int,
    val precipitation: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val windGusts: Double
)

data class WeatherData(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeatherData,
    val hourly: List<HourlyForecastItem>,
    val lastUpdatedMillis: Long
)

object WeatherService {

    private const val PREFS_NAME = "agromedic_weather_cache"
    private const val KEY_CACHED_JSON = "cached_weather_json"
    private const val KEY_LAST_UPDATE = "cached_last_updated"
    private const val KEY_LOC_ID = "selected_location_id"
    private const val KEY_LOC_NAME = "selected_location_name"
    private const val KEY_LOC_LAT = "selected_location_lat"
    private const val KEY_LOC_LON = "selected_location_lon"
    private const val KEY_IS_GPS = "selected_location_is_gps"

    // 30 minutes cache validity
    private const val CACHE_EXPIRY_MS = 30 * 60 * 1000L

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSelectedLocation(context: Context): LocationInfo {
        val prefs = getPrefs(context)
        val locId = prefs.getString(KEY_LOC_ID, null) ?: "pune"
        val matched = defaultAgriculturalLocations.find { it.id == locId }
        if (matched != null) return matched

        val customName = prefs.getString(KEY_LOC_NAME, "Pune, Maharashtra") ?: "Pune, Maharashtra"
        val lat = prefs.getFloat(KEY_LOC_LAT, 18.5204f).toDouble()
        val lon = prefs.getFloat(KEY_LOC_LON, 73.8567f).toDouble()
        return LocationInfo(locId, customName, customName, customName, lat, lon)
    }

    fun setSelectedLocation(context: Context, location: LocationInfo, isGps: Boolean = false) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_LOC_ID, location.id)
            .putString(KEY_LOC_NAME, location.nameEn)
            .putFloat(KEY_LOC_LAT, location.latitude.toFloat())
            .putFloat(KEY_LOC_LON, location.longitude.toFloat())
            .putBoolean(KEY_IS_GPS, isGps)
            .apply()
    }

    fun isUsingGpsLocation(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_GPS, false)
    }

    @SuppressLint("MissingPermission")
    fun detectDeviceLocation(context: Context): LocationInfo? {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
            var bestLocation: Location? = null

            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }

            if (bestLocation != null) {
                return LocationInfo(
                    id = "device_gps",
                    nameEn = "Current Location (${String.format(Locale.US, "%.2f, %.2f", bestLocation.latitude, bestLocation.longitude)})",
                    nameHi = "वर्तमान स्थान (${String.format(Locale.US, "%.2f, %.2f", bestLocation.latitude, bestLocation.longitude)})",
                    nameMr = "सध्याचे स्थान (${String.format(Locale.US, "%.2f, %.2f", bestLocation.latitude, bestLocation.longitude)})",
                    latitude = bestLocation.latitude,
                    longitude = bestLocation.longitude
                )
            }
        } catch (_: Exception) {
            // Permission or hardware exception handled safely
        }
        return null
    }

    suspend fun getWeatherData(
        context: Context,
        forceRefresh: Boolean = false
    ): Result<WeatherData> = withContext(Dispatchers.IO) {
        val prefs = getPrefs(context)
        val selectedLocation = getSelectedLocation(context)
        val cachedJson = prefs.getString(KEY_CACHED_JSON, null)
        val lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0L)
        val now = System.currentTimeMillis()

        // Check if cache is still fresh and matches location coordinates
        if (!forceRefresh && cachedJson != null && (now - lastUpdate) < CACHE_EXPIRY_MS) {
            try {
                val parsed = parseWeatherJson(cachedJson, selectedLocation.getDisplayName(context), selectedLocation.latitude, selectedLocation.longitude, lastUpdate)
                if (Math.abs(parsed.latitude - selectedLocation.latitude) < 0.1 &&
                    Math.abs(parsed.longitude - selectedLocation.longitude) < 0.1) {
                    return@withContext Result.success(parsed)
                }
            } catch (_: Exception) {
                // If parsing fails, fall back to network fetch
            }
        }

        // Fetch from Open-Meteo API
        val url = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=${selectedLocation.latitude}&longitude=${selectedLocation.longitude}&" +
                "current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,wind_direction_10m&" +
                "hourly=temperature_2m,relative_humidity_2m,precipitation_probability,precipitation,weather_code,wind_speed_10m,wind_gusts_10m&" +
                "timezone=auto"

        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "AgroMedic-Android/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                // If request failed but we have cache, return cache
                if (cachedJson != null) {
                    val parsed = parseWeatherJson(cachedJson, selectedLocation.getDisplayName(context), selectedLocation.latitude, selectedLocation.longitude, lastUpdate)
                    return@withContext Result.success(parsed)
                }
                return@withContext Result.failure(Exception("HTTP error: ${response.code}"))
            }

            val bodyString = response.body?.string() ?: throw Exception("Empty weather response")
            val parsedData = parseWeatherJson(bodyString, selectedLocation.getDisplayName(context), selectedLocation.latitude, selectedLocation.longitude, now)

            // Cache successfully fetched JSON
            prefs.edit()
                .putString(KEY_CACHED_JSON, bodyString)
                .putLong(KEY_LAST_UPDATE, now)
                .apply()

            Result.success(parsedData)
        } catch (e: Exception) {
            // Attempt to return cached data if available on network failure
            if (cachedJson != null) {
                try {
                    val parsed = parseWeatherJson(cachedJson, selectedLocation.getDisplayName(context), selectedLocation.latitude, selectedLocation.longitude, lastUpdate)
                    return@withContext Result.success(parsed)
                } catch (_: Exception) {
                    // ignore
                }
            }
            Result.failure(e)
        }
    }

    fun parseWeatherJson(
        jsonString: String,
        locationName: String,
        lat: Double,
        lon: Double,
        lastUpdatedMillis: Long
    ): WeatherData {
        val root = JSONObject(jsonString)
        val latitude = root.optDouble("latitude", lat)
        val longitude = root.optDouble("longitude", lon)

        val currentObj = root.getJSONObject("current")
        val current = CurrentWeatherData(
            time = currentObj.optString("time", ""),
            temperature = currentObj.optDouble("temperature_2m", 0.0),
            apparentTemperature = currentObj.optDouble("apparent_temperature", currentObj.optDouble("temperature_2m", 0.0)),
            relativeHumidity = currentObj.optInt("relative_humidity_2m", 0),
            precipitation = currentObj.optDouble("precipitation", 0.0),
            weatherCode = currentObj.optInt("weather_code", 0),
            windSpeed = currentObj.optDouble("wind_speed_10m", 0.0),
            windDirection = currentObj.optDouble("wind_direction_10m", 0.0)
        )

        val hourlyList = mutableListOf<HourlyForecastItem>()
        if (root.has("hourly")) {
            val hourlyObj = root.getJSONObject("hourly")
            val times = hourlyObj.optJSONArray("time")
            val temps = hourlyObj.optJSONArray("temperature_2m")
            val humidities = hourlyObj.optJSONArray("relative_humidity_2m")
            val rainProbs = hourlyObj.optJSONArray("precipitation_probability")
            val precipitations = hourlyObj.optJSONArray("precipitation")
            val codes = hourlyObj.optJSONArray("weather_code")
            val winds = hourlyObj.optJSONArray("wind_speed_10m")
            val gusts = hourlyObj.optJSONArray("wind_gusts_10m")

            val count = times?.length() ?: 0
            val currentTimeStr = current.time

            var startIndex = 0
            if (currentTimeStr.isNotEmpty() && times != null) {
                for (i in 0 until count) {
                    val t = times.optString(i, "")
                    if (t >= currentTimeStr) {
                        startIndex = i
                        break
                    }
                }
            }

            // Take next 24 hours
            val endIndex = Math.min(count, startIndex + 24)
            for (i in startIndex until endIndex) {
                val rawTime = times?.optString(i, "") ?: ""
                val formattedHour = formatHourString(rawTime)
                val item = HourlyForecastItem(
                    time = rawTime,
                    formattedHour = formattedHour,
                    temperature = temps?.optDouble(i, 0.0) ?: 0.0,
                    relativeHumidity = humidities?.optInt(i, 0) ?: 0,
                    precipitationProbability = rainProbs?.optInt(i, 0) ?: 0,
                    precipitation = precipitations?.optDouble(i, 0.0) ?: 0.0,
                    weatherCode = codes?.optInt(i, 0) ?: 0,
                    windSpeed = winds?.optDouble(i, 0.0) ?: 0.0,
                    windGusts = gusts?.optDouble(i, 0.0) ?: (winds?.optDouble(i, 0.0) ?: 0.0)
                )
                hourlyList.add(item)
            }
        }

        return WeatherData(
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            current = current,
            hourly = hourlyList,
            lastUpdatedMillis = lastUpdatedMillis
        )
    }

    private fun formatHourString(isoString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
            val date = parser.parse(isoString) ?: return isoString
            val formatter = SimpleDateFormat("h a", Locale.US)
            formatter.format(date)
        } catch (_: Exception) {
            if (isoString.contains("T")) {
                isoString.substringAfter("T")
            } else {
                isoString
            }
        }
    }

    fun getWeatherConditionResId(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.string.condition_clear_sky
            1 -> R.string.condition_mainly_clear
            2 -> R.string.condition_partly_cloudy
            3 -> R.string.condition_overcast
            45, 48 -> R.string.condition_fog
            51, 53, 55 -> R.string.condition_drizzle
            61, 63, 65 -> R.string.condition_rain
            80, 81, 82 -> R.string.condition_heavy_rain
            95, 96, 99 -> R.string.condition_thunderstorm
            else -> R.string.condition_partly_cloudy
        }
    }

    fun getWindDirectionCardinal(degrees: Double): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = (((degrees % 360) / 22.5) + 0.5).toInt() % 16
        return directions[index]
    }
}
