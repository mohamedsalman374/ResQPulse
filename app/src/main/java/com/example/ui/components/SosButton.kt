package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ResQRed
import com.example.ui.theme.ResQRedDark
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun SosButton(
    modifier: Modifier = Modifier,
    onSosTriggered: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isHolding by remember { mutableStateOf(false) }
    val holdProgress = remember { Animatable(0f) }
    var holdJob by remember { mutableStateOf<Job?>(null) }

    // Pulsing animation for outer ambient aura
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    fun triggerHaptic(durationMs: Long = 50) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .size(280.dp)
            .testTag("sos_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer faint ring 1
        Box(
            modifier = Modifier
                .size(270.dp * (if (isHolding) 1.08f else pulseScale))
                .clip(CircleShape)
                .background(Color(0xFFE2283C).copy(alpha = 0.05f))
        )

        // Ring 2
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2283C).copy(alpha = 0.08f))
        )

        // Ring 3
        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2283C).copy(alpha = 0.12f))
        )

        // Progress ring canvas when holding
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            if (holdProgress.value > 0f) {
                drawArc(
                    color = Color(0xFFDC2626),
                    startAngle = -90f,
                    sweepAngle = holdProgress.value * 360f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // The Main Red SOS Button
        Box(
            modifier = Modifier
                .size(180.dp)
                .shadow(
                    elevation = if (isHolding) 16.dp else 10.dp,
                    shape = CircleShape,
                    spotColor = ResQRed,
                    ambientColor = ResQRed
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF4D5E),
                            ResQRed,
                            ResQRedDark
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isHolding = true
                            triggerHaptic(40)
                            holdJob = coroutineScope.launch {
                                holdProgress.snapTo(0f)
                                holdProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = 3000,
                                        easing = LinearEasing
                                    )
                                )
                                if (holdProgress.value >= 1f) {
                                    triggerHaptic(200)
                                    onSosTriggered()
                                }
                            }
                            try {
                                awaitRelease()
                            } finally {
                                isHolding = false
                                holdJob?.cancel()
                                coroutineScope.launch {
                                    holdProgress.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 250)
                                    )
                                }
                            }
                        }
                    )
                }
                .testTag("sos_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SOS",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHolding) "Keep holding..." else "Tap & hold for 3\nseconds",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
