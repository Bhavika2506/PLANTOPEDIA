package com.example.plantopedia

import com.example.plantopedia.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object GeminiService {

    private const val API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .callTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    suspend fun getAdvice(question: String): String {

        return withContext(Dispatchers.IO) {

            try {

                // Get API key from local.properties through BuildConfig
                val apiKey = BuildConfig.GEMINI_API_KEY.trim()

                // Check if API key exists
                if (apiKey.isBlank()) {
                    return@withContext "Gemini API key is missing."
                }

                // =====================================================
                // PROMPT
                // =====================================================

                val prompt = """
                    You are AgroMedic, an AI agricultural advisor.

                    Your job is to help farmers and plant owners understand
                    crop diseases, plant symptoms, treatment, prevention,
                    watering, fertilizers, pests, and general crop care.

                    Give practical, clear, and easy-to-understand advice.

                    Important:
                    - Do not make up facts.
                    - If the information is insufficient, say what additional
                      information would be useful.
                    - Prefer simple language that a farmer can understand.
                    - Give actionable steps.
                    - Mention when professional agricultural advice may be needed.

                    User question:
                    $question
                """.trimIndent()


                // =====================================================
                // JSON REQUEST BODY
                // =====================================================

                val partsArray = JSONArray().apply {

                    put(
                        JSONObject().apply {
                            put("text", prompt)
                        }
                    )
                }

                val contentsArray = JSONArray().apply {

                    put(
                        JSONObject().apply {
                            put("parts", partsArray)
                        }
                    )
                }

                val requestJson = JSONObject().apply {
                    put("contents", contentsArray)
                }


                // =====================================================
                // REQUEST BODY
                // =====================================================

                val requestBody = requestJson
                    .toString()
                    .toRequestBody(
                        "application/json".toMediaType()
                    )


                // =====================================================
                // HTTP REQUEST
                // =====================================================

                val request = Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .addHeader(
                        "x-goog-api-key",
                        apiKey
                    )
                    .build()


                // =====================================================
                // SEND REQUEST
                // =====================================================

                client.newCall(request).execute().use { response ->

                    val responseBody =
                        response.body?.string()


                    // =================================================
                    // ERROR RESPONSE
                    // =================================================

                    if (!response.isSuccessful) {

                        return@withContext buildString {

                            append("Gemini API Error\n\n")
                            append("HTTP Status: ")
                            append(response.code)
                            append("\n\n")

                            if (!responseBody.isNullOrBlank()) {
                                append(responseBody)
                            } else {
                                append("No response body received.")
                            }
                        }
                    }


                    // =================================================
                    // EMPTY RESPONSE
                    // =================================================

                    if (responseBody.isNullOrBlank()) {

                        return@withContext "Gemini returned an empty response."
                    }


                    // =================================================
                    // PARSE RESPONSE
                    // =================================================

                    val jsonResponse =
                        JSONObject(responseBody)

                    val candidates =
                        jsonResponse.optJSONArray("candidates")

                    if (candidates == null || candidates.length() == 0) {

                        return@withContext "Gemini returned no answer.\n\n$responseBody"
                    }

                    val firstCandidate =
                        candidates.getJSONObject(0)

                    val content =
                        firstCandidate.optJSONObject("content")

                    if (content == null) {

                        return@withContext "Gemini response did not contain content.\n\n$responseBody"
                    }

                    val parts =
                        content.optJSONArray("parts")

                    if (parts == null || parts.length() == 0) {

                        return@withContext "Gemini response did not contain text.\n\n$responseBody"
                    }

                    val text =
                        parts
                            .getJSONObject(0)
                            .optString("text", "")

                    if (text.isBlank()) {

                        return@withContext "Gemini returned an empty answer."
                    }

                    text.trim()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                "Connection error:\n${e.message}"
            }
        }
    }
}