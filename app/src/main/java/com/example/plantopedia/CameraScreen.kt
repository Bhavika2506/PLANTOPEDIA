package com.example.plantopedia

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.plantopedia.ui.theme.DarkForestGreen
import com.example.plantopedia.ui.theme.EarthBorder
import com.example.plantopedia.ui.theme.MutedSlateText
import com.example.plantopedia.ui.theme.NearBlackText
import com.example.plantopedia.ui.theme.StatusError
import com.example.plantopedia.ui.theme.StatusSuccess
import com.example.plantopedia.ui.theme.StatusWarning
import com.example.plantopedia.ui.theme.WarmBeigeBackground
import com.example.plantopedia.ui.theme.WarmWhiteSurface
import java.io.File

fun formatLabel(label: String): String {
    return label
        .replace("___", " → ")
        .replace("_", " ")
        .replace("(", " (")
        .replace(")", ")")
        .trim()
}

@Composable
fun CameraScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var capturedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var prediction by remember {
        mutableStateOf<Prediction?>(null)
    }

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    val classifier = remember {
        CropDiseaseClassifier(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            classifier.close()
        }
    }

    /*
     * Once an image has been captured, show the captured image screen.
     */
    if (capturedImageUri != null) {
        CapturedImageScreen(
            imageUri = capturedImageUri!!,
            prediction = prediction,
            isAnalyzing = isAnalyzing,
            onBack = onBack,
            onRetake = {
                capturedImageUri = null
                prediction = null
            },
            onAnalyze = {
                if (!isAnalyzing) {
                    isAnalyzing = true

                    Thread {
                        try {
                            val inputStream =
                                context.contentResolver.openInputStream(
                                    capturedImageUri!!
                                )

                            val bitmap =
                                BitmapFactory.decodeStream(inputStream)

                            inputStream?.close()

                            if (bitmap != null) {
                                val result = classifier.classify(bitmap)
                                prediction = result

                                if (
                                    result.label != "Unknown crop" &&
                                    result.label != "Uncertain disease" &&
                                    result.label != "Unable to confidently identify disease"
                                ) {
                                    ScanHistory.save(
                                        context = context,
                                        prediction = result
                                    )
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isAnalyzing = false
                        }
                    }.start()
                }
            }
        )
        return
    }

    /*
     * Live Camera Preview Screen
     */
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(
            factory = { ctx: Context ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    val capture = ImageCapture.Builder().build()

                    imageCapture = capture

                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        /*
         * Translucent Back Button on top of live camera (Dark Forest Green translucent)
         */
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .clip(CircleShape)
                .align(Alignment.TopStart),
            color = DarkForestGreen.copy(alpha = 0.7f),
            contentColor = Color.White
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.camera_back)
                )
            }
        }

        /*
         * Clean Capture Button Overlay (Dark Forest Green Pill with camera icon)
         */
        Button(
            onClick = {
                val capture = imageCapture ?: return@Button
                val photoFile = File(
                    context.cacheDir,
                    "crop_${System.currentTimeMillis()}.jpg"
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                capture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            capturedImageUri = Uri.fromFile(photoFile)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .size(76.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkForestGreen,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = stringResource(id = R.string.camera_capture),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

/*
 * Screen shown after capturing an image matching Reference Screens 4 & 5.
 */
@Composable
fun CapturedImageScreen(
    imageUri: Uri,
    prediction: Prediction?,
    isAnalyzing: Boolean,
    onBack: () -> Unit,
    onRetake: () -> Unit,
    onAnalyze: () -> Unit
) {
    val context = LocalContext.current

    val diseaseInfo = prediction?.let {
        DiseaseDatabase.get(it.label, context)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = WarmBeigeBackground // Warm beige background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // Top Navigation Bar Row with back icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkForestGreen
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = stringResource(id = R.string.crop_analysis),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NearBlackText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            /*
             * Captured plant image card matching Reference Screen 4 & 5 styling
             */
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(20.dp),
                color = WarmWhiteSurface,
                border = BorderStroke(1.dp, EarthBorder),
                shadowElevation = 0.dp
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = stringResource(id = R.string.crop_analysis),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /*
             * Analysis loading state
             */
            if (isAnalyzing) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = WarmWhiteSurface,
                    border = BorderStroke(1.dp, EarthBorder),
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = DarkForestGreen
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(id = R.string.analyzing_crop),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkForestGreen
                        )
                    }
                }
            }
            /*
             * Diagnosis/result display
             */
            else if (prediction != null) {
                val confidence = prediction.confidence * 100
                val isLowConfidence = confidence < 40

                if (isLowConfidence) {
                    // Low confidence warning card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = WarmWhiteSurface,
                            contentColor = StatusWarning
                        ),
                        border = BorderStroke(1.dp, StatusWarning)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StatusWarning,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = stringResource(id = R.string.low_confidence_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusWarning
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(id = R.string.low_confidence_desc),
                                style = MaterialTheme.typography.bodyLarge,
                                color = NearBlackText
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(id = R.string.confidence_label, confidence),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = StatusWarning
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(id = R.string.low_confidence_instruction),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedSlateText
                            )
                        }
                    }
                } else {
                    // Normal Diagnosis Header Card with botanical chips
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = WarmWhiteSurface,
                            contentColor = NearBlackText
                        ),
                        border = BorderStroke(1.dp, EarthBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Plant / Crop Name Header
                            Text(
                                text = diseaseInfo?.crop ?: prediction.crop ?: formatLabel(prediction.label),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = NearBlackText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = diseaseInfo?.disease ?: formatLabel(prediction.label),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkForestGreen
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tags / Chips Row matching Reference Screen 5 with responsive wrap
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Dark Green Pill Chip
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = DarkForestGreen
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.disease_identified),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }

                                // Confidence Status Pill Chip
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = StatusSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.confidence_label, confidence),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkForestGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    /*
                     * Disease Info breakdown cards (Symptoms, Treatment, Prevention)
                     */
                    if (diseaseInfo != null) {
                        DiseaseInfoCard(
                            title = stringResource(id = R.string.symptoms_title),
                            content = diseaseInfo.symptoms,
                            icon = Icons.Default.Search
                        )

                        DiseaseInfoCard(
                            title = stringResource(id = R.string.treatment_title),
                            content = diseaseInfo.treatment,
                            icon = Icons.Default.MedicalServices
                        )

                        DiseaseInfoCard(
                            title = stringResource(id = R.string.prevention_title),
                            content = diseaseInfo.prevention,
                            icon = Icons.Default.Shield
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, EarthBorder)
                        ) {
                            Text(
                                text = stringResource(id = R.string.disease_info_not_available),
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MutedSlateText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /*
             * Retake + Analyze Buttons (min 52dp height, responsive to script lengths)
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, DarkForestGreen),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkForestGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.retake).replace("↻", "").trim(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Button(
                    onClick = onAnalyze,
                    enabled = !isAnalyzing && prediction == null,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.analyze).replace("🔬", "").trim(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Back to Home button after analysis is ready (min 52dp height, Dark Forest Green pill)
            if (prediction != null && !isAnalyzing) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.back_to_home).replace("←", "").trim(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DiseaseInfoCard(
    title: String,
    content: String,
    icon: ImageVector = Icons.Default.Search
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarmWhiteSurface,
            contentColor = NearBlackText
        ),
        border = BorderStroke(1.dp, EarthBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DarkForestGreen,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = NearBlackText,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}