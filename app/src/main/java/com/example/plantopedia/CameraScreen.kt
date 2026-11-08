package com.example.plantopedia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.plantopedia.ui.theme.AppBackground
import com.example.plantopedia.ui.theme.BorderColor
import com.example.plantopedia.ui.theme.CardBackground
import com.example.plantopedia.ui.theme.LightGreenTint
import com.example.plantopedia.ui.theme.PrimaryGreen
import com.example.plantopedia.ui.theme.PrimaryText
import com.example.plantopedia.ui.theme.SecondaryGreen
import com.example.plantopedia.ui.theme.SecondaryText
import com.example.plantopedia.ui.theme.StatusSuccess
import com.example.plantopedia.ui.theme.StatusWarning
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

    // Check camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var cameraInstance by remember {
        mutableStateOf<Camera?>(null)
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var isFlashOn by remember {
        mutableStateOf(false)
    }

    var showHelpDialog by remember {
        mutableStateOf(false)
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

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            capturedImageUri = uri
            prediction = null
        }
    }

    BackHandler {
        if (capturedImageUri != null) {
            capturedImageUri = null
            prediction = null
        } else {
            onBack()
        }
    }

    /*
     * Image confirmation & disease diagnosis screen
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
     * Live Plantix-inspired Camera Viewfinder Screen
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!hasCameraPermission) {
            // Permission request fallback card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.camera_permission_required),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.check_plant_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.camera_grant_permission),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(R.string.back))
                }
            }
        } else {
            // CameraX Live Preview
            AndroidView(
                factory = { ctx: Context ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build()
                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()

                        imageCapture = capture

                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            val cam = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                            cameraInstance = cam
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Scanning Overlay: Semi-dark mask with large central clear frame and white corner brackets
            ScanningFrameOverlay(
                modifier = Modifier.fillMaxSize()
            )

            // TOP CONTROLS: Back button on top-left, Flash toggle on top-right
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Top-left)
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.camera_back),
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Flash Toggle (Top-right)
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable {
                            val cam = cameraInstance
                            if (cam != null && cam.cameraInfo.hasFlashUnit()) {
                                val nextFlashState = !isFlashOn
                                cam.cameraControl.enableTorch(nextFlashState)
                                isFlashOn = nextFlashState
                            } else {
                                // Toggle state indicator even if physical flash unit not present
                                isFlashOn = !isFlashOn
                            }
                        },
                    shape = CircleShape,
                    color = if (isFlashOn) Color(0xFFFFD54F).copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.45f),
                    border = if (isFlashOn) BorderStroke(1.5.dp, Color(0xFFFFD54F)) else null,
                    contentColor = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = if (isFlashOn) stringResource(R.string.camera_flash_on) else stringResource(R.string.camera_flash_off),
                            tint = if (isFlashOn) Color(0xFFFFD54F) else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // BOTTOM CONTROLS & GUIDANCE CONTAINER
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Subtle Guidance Message near bottom of viewfinder
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = stringResource(R.string.camera_guidance_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }

                // Three Bottom Controls: Gallery (Left) | Shutter Capture (Center) | Help (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Gallery Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = stringResource(R.string.camera_gallery),
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    // Centre: Large Circular White Shutter Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable {
                                val capture = imageCapture ?: return@clickable
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
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer ring
                        Canvas(modifier = Modifier.size(80.dp)) {
                            drawCircle(
                                color = Color.White,
                                radius = size.minDimension / 2f,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                            )
                        }
                        // Inner solid white circle
                        Surface(
                            modifier = Modifier.size(66.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {}
                    }

                    // Right: Help Question Mark Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showHelpDialog = true }
                            .padding(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = stringResource(R.string.camera_help),
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Help Dialog explaining the 6 photo-taking guidelines
    if (showHelpDialog) {
        CameraHelpDialog(
            onDismiss = { showHelpDialog = false }
        )
    }
}

/**
 * Scanning frame overlay with dark translucent letterboxing around a large central rectangular
 * viewing area, bordered by four crisp white corner brackets.
 */
@Composable
fun ScanningFrameOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Rectangular frame dimensions centered on screen
        val frameWidth = canvasWidth * 0.82f
        val frameHeight = frameWidth * 1.15f
        val frameLeft = (canvasWidth - frameWidth) / 2f
        val frameTop = (canvasHeight - frameHeight) / 2.35f
        val frameCornerRadius = 16.dp.toPx()

        val frameRect = Rect(
            offset = Offset(frameLeft, frameTop),
            size = Size(frameWidth, frameHeight)
        )

        // Clip out the transparent inner viewing rectangle from the dark mask
        val clipPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = frameRect,
                    cornerRadius = CornerRadius(frameCornerRadius, frameCornerRadius)
                )
            )
        }

        clipPath(clipPath, clipOp = ClipOp.Difference) {
            drawRect(
                color = Color.Black.copy(alpha = 0.45f),
                size = size
            )
        }

        // Draw 4 distinct white corner brackets around scanning area
        val bracketColor = Color.White
        val bracketStroke = 4.dp.toPx()
        val bracketLength = 32.dp.toPx()
        val cornerArcRadius = frameCornerRadius

        // Top-Left Corner
        val topLeftPath = Path().apply {
            moveTo(frameLeft, frameTop + bracketLength)
            lineTo(frameLeft, frameTop + cornerArcRadius)
            quadraticTo(frameLeft, frameTop, frameLeft + cornerArcRadius, frameTop)
            lineTo(frameLeft + bracketLength, frameTop)
        }
        drawPath(
            path = topLeftPath,
            color = bracketColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = bracketStroke,
                cap = StrokeCap.Round
            )
        )

        // Top-Right Corner
        val topRightPath = Path().apply {
            moveTo(frameLeft + frameWidth - bracketLength, frameTop)
            lineTo(frameLeft + frameWidth - cornerArcRadius, frameTop)
            quadraticTo(frameLeft + frameWidth, frameTop, frameLeft + frameWidth, frameTop + cornerArcRadius)
            lineTo(frameLeft + frameWidth, frameTop + bracketLength)
        }
        drawPath(
            path = topRightPath,
            color = bracketColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = bracketStroke,
                cap = StrokeCap.Round
            )
        )

        // Bottom-Left Corner
        val bottomLeftPath = Path().apply {
            moveTo(frameLeft, frameTop + frameHeight - bracketLength)
            lineTo(frameLeft, frameTop + frameHeight - cornerArcRadius)
            quadraticTo(frameLeft, frameTop + frameHeight, frameLeft + cornerArcRadius, frameTop + frameHeight)
            lineTo(frameLeft + bracketLength, frameTop + frameHeight)
        }
        drawPath(
            path = bottomLeftPath,
            color = bracketColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = bracketStroke,
                cap = StrokeCap.Round
            )
        )

        // Bottom-Right Corner
        val bottomRightPath = Path().apply {
            moveTo(frameLeft + frameWidth - bracketLength, frameTop + frameHeight)
            lineTo(frameLeft + frameWidth - cornerArcRadius, frameTop + frameHeight)
            quadraticTo(frameLeft + frameWidth, frameTop + frameHeight, frameLeft + frameWidth, frameTop + frameHeight - cornerArcRadius)
            lineTo(frameLeft + frameWidth, frameTop + frameHeight - bracketLength)
        }
        drawPath(
            path = bottomRightPath,
            color = bracketColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = bracketStroke,
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Help dialog explaining the 6 photo-taking rules for farmers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraHelpDialog(
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LightGreenTint,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = stringResource(R.string.camera_help_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.camera_help_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // The 6 Guidelines
                val tips = listOf(
                    stringResource(R.string.camera_help_tip_1),
                    stringResource(R.string.camera_help_tip_2),
                    stringResource(R.string.camera_help_tip_3),
                    stringResource(R.string.camera_help_tip_4),
                    stringResource(R.string.camera_help_tip_5),
                    stringResource(R.string.camera_help_tip_6)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    tips.forEachIndexed { index, tip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGreenTint, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.camera_got_it),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/*
 * Screen shown after capturing or selecting an image.
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
        color = AppBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Compact Header Bar
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = stringResource(id = R.string.crop_analysis),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Captured plant image card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = stringResource(id = R.string.crop_analysis),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Analysis loading state
                if (isAnalyzing) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryGreen,
                                strokeWidth = 3.dp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = stringResource(id = R.string.analyzing_crop),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryGreen
                            )
                        }
                    }
                }
                // Diagnosis result display
                else if (prediction != null) {
                    val confidence = prediction.confidence * 100
                    val isLowConfidence = confidence < 40

                    if (isLowConfidence) {
                        // Low confidence warning card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardBackground,
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
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusWarning
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(id = R.string.low_confidence_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryText
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = stringResource(id = R.string.low_confidence_instruction),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }
                    } else {
                        // Clean Diagnosis Header Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardBackground,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = diseaseInfo?.crop ?: prediction.crop ?: formatLabel(prediction.label),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )

                                    // Confidence Pill
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = LightGreenTint,
                                        border = BorderStroke(1.dp, BorderColor)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = StatusSuccess,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = stringResource(id = R.string.confidence_label, confidence),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = diseaseInfo?.disease ?: formatLabel(prediction.label),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Symptoms, Treatment, Prevention Breakdown
                        if (diseaseInfo != null) {
                            DiseaseInfoCard(
                                title = stringResource(id = R.string.symptoms_title),
                                content = diseaseInfo.symptoms,
                                icon = Icons.Default.Search
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DiseaseInfoCard(
                                title = stringResource(id = R.string.treatment_title),
                                content = diseaseInfo.treatment,
                                icon = Icons.Default.MedicalServices
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DiseaseInfoCard(
                                title = stringResource(id = R.string.prevention_title),
                                content = diseaseInfo.prevention,
                                icon = Icons.Default.Shield
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons: Retake / Analyze
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetake,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryText
                        )
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
                                fontWeight = FontWeight.SemiBold,
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
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        )
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

                if (prediction != null && !isAnalyzing) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
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
}

@Composable
fun DiseaseInfoCard(
    title: String,
    content: String,
    icon: ImageVector = Icons.Default.Search
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightGreenTint,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = title.replace(Regex("^[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+\\s*"), "").trim(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                lineHeight = 22.sp
            )
        }
    }
}
