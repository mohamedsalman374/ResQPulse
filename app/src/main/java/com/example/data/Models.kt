package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmbulanceBg
import com.example.ui.theme.AmbulanceFg
import com.example.ui.theme.FireBg
import com.example.ui.theme.FireFg
import com.example.ui.theme.PoliceBg
import com.example.ui.theme.PoliceFg

data class EmergencyContact(
    val id: String,
    val name: String,
    val relation: String,
    val phone: String,
    val isPrimary: Boolean = false
)

enum class HistoryType {
    LOCATION_SHARED,
    HOSPITAL_VIEWED,
    CONTACT_ADDED,
    SOS_TRIGGERED
}

data class HistoryItem(
    val id: String,
    val title: String,
    val timeDescription: String,
    val type: HistoryType,
    val dotColor: Color = Color(0xFF22C55E)
)

data class Hospital(
    val id: String,
    val name: String,
    val distance: String,
    val duration: String,
    val address: String,
    val phone: String,
    val emergencyPhone: String = "108",
    val specialties: List<String> = listOf("24/7 Trauma Care", "ICU", "Cardiac Care", "Ambulance"),
    val gridX: Float = 0.5f,
    val gridY: Float = 0.5f
)

data class UserProfile(
    val name: String = "Mathan",
    val email: String = "mathan@example.com",
    val phone: String = "+91 98765 43210",
    val bloodGroup: String = "O+",
    val medicalNotes: List<String> = listOf("PCOD", "Thyroid (TSH 1.78)", "Vitamin D deficiency", "Allergic to dust"),
    val emergencyContacts: List<EmergencyContact> = listOf(
        EmergencyContact("1", "Mother", "Primary", "+91 98765 43210", isPrimary = true),
        EmergencyContact("2", "Father", "Primary", "+91 91234 56789", isPrimary = true),
        EmergencyContact("3", "Sister", "Family", "+91 87654 32109"),
        EmergencyContact("4", "Friend (emergency)", "Trusted contact", "+91 93456 78901")
    )
)

enum class EmergencyServiceType {
    AMBULANCE,
    POLICE,
    FIRE
}

data class QuickEmergencyService(
    val type: EmergencyServiceType,
    val title: String,
    val subtitle: String,
    val dialNumber: String,
    val bgColor: Color,
    val fgColor: Color
)

val defaultServices = listOf(
    QuickEmergencyService(
        type = EmergencyServiceType.AMBULANCE,
        title = "Ambulance",
        subtitle = "Medical\nemergency",
        dialNumber = "108",
        bgColor = AmbulanceBg,
        fgColor = AmbulanceFg
    ),
    QuickEmergencyService(
        type = EmergencyServiceType.POLICE,
        title = "Police",
        subtitle = "Law & safety",
        dialNumber = "100",
        bgColor = PoliceBg,
        fgColor = PoliceFg
    ),
    QuickEmergencyService(
        type = EmergencyServiceType.FIRE,
        title = "Fire",
        subtitle = "Fire emergency",
        dialNumber = "101",
        bgColor = FireBg,
        fgColor = FireFg
    )
)
