package com.example.plantopedia

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.exp

data class Prediction(
    val label: String,
    val confidence: Float,
    val crop: String? = null,
    val cropConfidence: Float? = null
)

class CropDiseaseClassifier(
    private val context: Context
) {

    companion object {
        private const val IMAGE_SIZE = 224

        private const val CROP_MODEL = "plantopedia_crop.tflite"
        private const val DISEASE_MODEL = "plantopedia_model.tflite"

        private const val CROP_LABELS = "crop_labels.txt"
        private const val DISEASE_LABELS = "labels.txt"

        private const val CROP_THRESHOLD = 0.70f
        private const val DISEASE_THRESHOLD = 0.40f
    }

    private val cropInterpreter: Interpreter
    private val diseaseInterpreter: Interpreter

    private val cropLabels: List<String>
    private val diseaseLabels: List<String>

    init {

        cropInterpreter = Interpreter(
            loadModel(CROP_MODEL)
        )

        diseaseInterpreter = Interpreter(
            loadModel(DISEASE_MODEL)
        )

        cropLabels = loadLabels(CROP_LABELS)
        diseaseLabels = loadLabels(DISEASE_LABELS)

        println("================================")
        println("AgroMedic AI loaded")
        println("Crop labels: $cropLabels")
        println("Disease labels: ${diseaseLabels.size}")
        println("================================")
    }

    // =========================================================
    // MAIN CLASSIFICATION
    // =========================================================

    fun classify(bitmap: Bitmap): Prediction {

        // -----------------------------------------------------
        // STEP 1 — IDENTIFY CROP
        // -----------------------------------------------------

        val cropInput = preprocess(bitmap)

        val cropOutput =
            Array(1) { FloatArray(cropLabels.size) }

        cropInterpreter.run(
            cropInput,
            cropOutput
        )
        println("========== CROP SCORES ==========")

        for (i in cropLabels.indices) {

            println(
                "${cropLabels[i]} = " +
                        "${cropOutput[0][i] * 100}%"
            )
        }

        println("=================================")

        val cropResult =
            getBestPrediction(
                cropOutput[0],
                cropLabels
            )

        val cropName = cropResult.first
        val cropConfidence = cropResult.second

        println(
            "CROP: $cropName " +
                    "${cropConfidence * 100}%"
        )

        // -----------------------------------------------------
        // STEP 2 — DON'T GUESS IF CROP IS UNCERTAIN
        // -----------------------------------------------------

        if (cropConfidence < CROP_THRESHOLD) {

            return Prediction(
                label = "Unknown crop",
                confidence = cropConfidence,
                crop = "Unknown",
                cropConfidence = cropConfidence
            )
        }

        // -----------------------------------------------------
        // STEP 3 — RUN DISEASE MODEL
        // -----------------------------------------------------

        val diseaseInput = preprocess(bitmap)

        val diseaseOutput =
            Array(1) {
                FloatArray(diseaseLabels.size)
            }

        diseaseInterpreter.run(
            diseaseInput,
            diseaseOutput
        )

        val diseaseResult =
            getBestPrediction(
                diseaseOutput[0],
                diseaseLabels
            )

        val diseaseLabel = diseaseResult.first
        val diseaseConfidence = diseaseResult.second

        println(
            "DISEASE: $diseaseLabel " +
                    "${diseaseConfidence * 100}%"
        )

        // -----------------------------------------------------
        // STEP 4 — VERIFY DISEASE BELONGS TO CROP
        // -----------------------------------------------------

        val diseaseCrop =
            getCropFromDiseaseLabel(
                diseaseLabel
            )

        if (
            diseaseCrop != null &&
            !sameCrop(
                cropName,
                diseaseCrop
            )
        ) {

            println(
                "WARNING: Crop/disease mismatch"
            )

            return Prediction(
                label = "Unable to confidently identify disease",
                confidence = diseaseConfidence,
                crop = cropName,
                cropConfidence = cropConfidence
            )
        }

        // -----------------------------------------------------
        // STEP 5 — LOW DISEASE CONFIDENCE
        // -----------------------------------------------------

        if (diseaseConfidence < DISEASE_THRESHOLD) {

            return Prediction(
                label = "Uncertain disease",
                confidence = diseaseConfidence,
                crop = cropName,
                cropConfidence = cropConfidence
            )
        }

        // -----------------------------------------------------
        // FINAL RESULT
        // -----------------------------------------------------

        return Prediction(
            label = diseaseLabel,
            confidence = diseaseConfidence,
            crop = cropName,
            cropConfidence = cropConfidence
        )
    }

    // =========================================================
    // IMAGE PREPROCESSING
    // =========================================================

    private fun preprocess(
        bitmap: Bitmap
    ): ByteBuffer {

        val resizedBitmap =
            Bitmap.createScaledBitmap(
                bitmap,
                IMAGE_SIZE,
                IMAGE_SIZE,
                true
            )

        val inputBuffer =
            ByteBuffer.allocateDirect(
                4 *
                        IMAGE_SIZE *
                        IMAGE_SIZE *
                        3
            )

        inputBuffer.order(
            ByteOrder.nativeOrder()
        )

        val pixels =
            IntArray(
                IMAGE_SIZE *
                        IMAGE_SIZE
            )

        resizedBitmap.getPixels(
            pixels,
            0,
            IMAGE_SIZE,
            0,
            0,
            IMAGE_SIZE,
            IMAGE_SIZE
        )

        for (pixel in pixels) {

            val r =
                ((pixel shr 16) and 0xFF).toFloat()

            val g =
                ((pixel shr 8) and 0xFF).toFloat()

            val b =
                (pixel and 0xFF).toFloat()

            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        inputBuffer.rewind()

        return inputBuffer
    }
    // =========================================================
    // BEST PREDICTION
    // =========================================================

    private fun getBestPrediction(
        probabilities: FloatArray,
        labels: List<String>
    ): Pair<String, Float> {

        var bestIndex = 0
        var bestValue = probabilities[0]

        for (i in 1 until probabilities.size) {

            if (probabilities[i] > bestValue) {

                bestValue =
                    probabilities[i]

                bestIndex = i
            }
        }

        return Pair(
            labels[bestIndex],
            bestValue
        )
    }

    // =========================================================
    // EXTRACT CROP FROM DISEASE LABEL
    // =========================================================

    private fun getCropFromDiseaseLabel(
        label: String
    ): String? {

        return when {

            label.startsWith("Apple___") ->
                "Apple"

            label.startsWith("Corn_(maize)___") ->
                "Corn"

            label.startsWith("Grape___") ->
                "Grape"

            label.startsWith("Pepper,_bell___") ->
                "Pepper"

            label.startsWith("Potato___") ->
                "Potato"

            label.startsWith("Tomato___") ->
                "Tomato"

            else ->
                null
        }
    }

    // =========================================================
    // COMPARE CROP NAMES
    // =========================================================

    private fun sameCrop(
        crop1: String,
        crop2: String
    ): Boolean {

        return crop1.equals(
            crop2,
            ignoreCase = true
        )
    }

    // =========================================================
    // LOAD TFLITE MODEL
    // =========================================================

    private fun loadModel(
        filename: String
    ): ByteBuffer {

        val fileDescriptor =
            context.assets.openFd(filename)

        val inputStream =
            FileInputStream(
                fileDescriptor.fileDescriptor
            )

        val fileChannel =
            inputStream.channel

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    // =========================================================
    // LOAD LABELS
    // =========================================================

    private fun loadLabels(
        filename: String
    ): List<String> {

        return context.assets
            .open(filename)
            .bufferedReader()
            .readLines()
            .map {
                it.trim()
            }
            .filter {
                it.isNotEmpty()
            }
    }

    // =========================================================
    // CLOSE MODELS
    // =========================================================

    fun close() {

        cropInterpreter.close()

        diseaseInterpreter.close()
    }
}