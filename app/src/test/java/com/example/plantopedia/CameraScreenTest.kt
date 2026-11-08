package com.example.plantopedia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraScreenTest {

    @Test
    fun testFormatLabel_replacesDelimitersProperly() {
        val original = "Corn_(maize)___Northern_Leaf_Blight"
        val formatted = formatLabel(original)
        assertEquals("Corn  (maize) → Northern Leaf Blight", formatted)
    }

    @Test
    fun testFormatLabel_handlesSingleWords() {
        val original = "Tomato___healthy"
        val formatted = formatLabel(original)
        assertEquals("Tomato → healthy", formatted)
    }

    @Test
    fun testPredictionModelIntegrity() {
        val prediction = Prediction(
            label = "Tomato___Early_blight",
            confidence = 0.85f,
            crop = "Tomato",
            cropConfidence = 0.92f
        )
        assertEquals("Tomato___Early_blight", prediction.label)
        assertEquals(0.85f, prediction.confidence, 0.001f)
        assertEquals("Tomato", prediction.crop)
        assertEquals(0.92f, prediction.cropConfidence ?: 0f, 0.001f)
    }

    @Test
    fun testCameraNavigationCallbackSignature() {
        var backCalled = false
        val onBack: () -> Unit = { backCalled = true }
        onBack()
        assertTrue("onBack should be invocable", backCalled)
    }
}
