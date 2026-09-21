package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Hospital
import com.example.ui.components.HospitalDetailDialog
import com.example.ui.theme.ResQRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val sampleHospitals = listOf(
    Hospital(
        id = "1",
        name = "City Care Multispeciality Hospital",
        distance = "1.2 km",
        duration = "4 mins",
        address = "42 Medical Enclave, Main Bypass Road",
        phone = "+91 98400 12345",
        specialties = listOf("24/7 Trauma Care", "ICU & CCU", "Cardiac Cath Lab", "Emergency Ambulance"),
        gridX = 0.65f,
        gridY = 0.35f
    ),
    Hospital(
        id = "2",
        name = "Kauvery Hospital",
        distance = "2.8 km",
        duration = "8 mins",
        address = "Cantonment Road, Near Central Terminal",
        phone = "+91 98400 67890",
        specialties = listOf("Emergency Medicine", "Neuro ICU", "Burn Care Unit"),
        gridX = 0.75f,
        gridY = 0.44f
    ),
    Hospital(
        id = "3",
        name = "Apollo Speciality Clinic",
        distance = "4.1 km",
        duration = "12 mins",
        address = "Collector Office Road",
        phone = "+91 98400 11223",
        specialties = listOf("24/7 Urgent Care", "Orthopedic Emergency"),
        gridX = 0.35f,
        gridY = 0.70f
    )
)

@Composable
fun MapScreen(
    onLocationShareToggled: (Boolean) -> Unit = {}
) {
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var selectedHospital by remember { mutableStateOf<Hospital?>(null) }
    var activeHospitalIndex by remember { mutableStateOf(0) }
    var isSharingLocation by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (selectedHospital != null) {
        HospitalDetailDialog(
            hospital = selectedHospital!!,
            onDismiss = { selectedHospital = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("map_screen")
    ) {
        Text(
            text = "Nearby hospitals",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Live location, nearby hospitals and location sharing.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Map Canvas Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEFF8F2))
                .border(1.dp, Color(0xFFDCF0E2), RoundedCornerShape(24.dp))
                .testTag("map_canvas_container")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSpacing = 28.dp.toPx() * zoomLevel
                val gridColor = Color(0xFFD7ECD9)

                // Grid lines
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }

                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // User Location: Blue Dot with Pulse Ring at center
                val userX = size.width * 0.43f
                val userY = size.height * 0.47f

                drawCircle(
                    color = Color(0xFF3B82F6).copy(alpha = 0.25f),
                    radius = 20.dp.toPx() * zoomLevel,
                    center = Offset(userX, userY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 10.dp.toPx(),
                    center = Offset(userX, userY)
                )
                drawCircle(
                    color = Color(0xFF2563EB),
                    radius = 6.dp.toPx(),
                    center = Offset(userX, userY)
                )

                // Draw Hospital Pins
                sampleHospitals.forEach { hosp ->
                    val pinX = size.width * hosp.gridX
                    val pinY = size.height * hosp.gridY

                    // Pin Marker
                    val pinPath = Path().apply {
                        moveTo(pinX, pinY)
                        cubicTo(
                            pinX - 12f, pinY - 14f,
                            pinX - 14f, pinY - 26f,
                            pinX, pinY - 26f
                        )
                        cubicTo(
                            pinX + 14f, pinY - 26f,
                            pinX + 12f, pinY - 14f,
                            pinX, pinY
                        )
                        close()
                    }
                    drawPath(path = pinPath, color = Color(0xFFDC2626))
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(pinX, pinY - 16f)
                    )
                }
            }

            // Zoom In / Out control widget (Screenshot 1)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { if (zoomLevel < 1.8f) zoomLevel += 0.2f },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom in",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.width(28.dp), color = Color(0xFFE2E8F0))
                    IconButton(
                        onClick = { if (zoomLevel > 0.6f) zoomLevel -= 0.2f },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom out",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hospital Card (Screenshot 1 & 8)
        val currentHospital = sampleHospitals[activeHospitalIndex]
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedHospital = currentHospital }
                .testTag("hospital_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pink icon with hospital cross
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = ResQRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentHospital.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${currentHospital.distance} · ${currentHospital.duration}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View Hospital",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Coordinates text (Screenshot 8)
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your location: ",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Text(
                text = "10.7903° N, 78.7047° E",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live location sharing card (Screenshot 8)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4FE)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_location_share_card")
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Live location sharing",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isSharingLocation)
                        "Currently sharing live GPS location with Mother and Father."
                    else
                        "Let your trusted contacts see your real-time location.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isSharingLocation = !isSharingLocation
                        onLocationShareToggled(isSharingLocation)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSharingLocation) Color(0xFF059669) else Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_location_button")
                ) {
                    Text(
                        text = if (isSharingLocation) "Sharing Active (Tap to Stop)" else "Share now",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
