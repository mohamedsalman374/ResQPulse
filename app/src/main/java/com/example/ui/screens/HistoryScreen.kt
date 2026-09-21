package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HistoryItem
import com.example.data.HistoryType
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val defaultHistoryItems = listOf(
    HistoryItem(
        id = "1",
        title = "Location shared with Mother, Father",
        timeDescription = "Yesterday · 8:14 PM · 12 min",
        type = HistoryType.LOCATION_SHARED,
        dotColor = Color(0xFF22C55E)
    ),
    HistoryItem(
        id = "2",
        title = "Nearby hospital viewed — City Care Multispeciality",
        timeDescription = "Yesterday · 8:12 PM",
        type = HistoryType.HOSPITAL_VIEWED,
        dotColor = Color(0xFF2563EB)
    ),
    HistoryItem(
        id = "3",
        title = "Emergency contact added — Friend (Emergency)",
        timeDescription = "3 days ago",
        type = HistoryType.CONTACT_ADDED,
        dotColor = Color(0xFF22C55E)
    )
)

@Composable
fun HistoryScreen(
    items: List<HistoryItem> = defaultHistoryItems
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("history_screen")
    ) {
        Text(
            text = "History",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Recent SOS activations and location shares.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(items) { item ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status indicator colored dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(item.dotColor)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.timeDescription,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF1F5F9),
                        thickness = 1.dp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "No SOS alerts triggered this week — that's a\ngood thing.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
