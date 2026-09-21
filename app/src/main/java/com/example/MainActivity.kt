package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.data.EmergencyContact
import com.example.data.HistoryItem
import com.example.data.HistoryType
import com.example.data.UserProfile
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.defaultHistoryItems
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ResQRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    MAP("Map", Icons.Filled.Map, Icons.Outlined.Map, "nav_map"),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History, "nav_history"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ResQPulseApp()
            }
        }
    }
}

@Composable
fun ResQPulseApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var userProfile by remember { mutableStateOf(UserProfile()) }
    var historyList by remember { mutableStateOf(defaultHistoryItems) }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { phone ->
                userProfile = userProfile.copy(phone = phone)
                isLoggedIn = true
                currentTab = NavTab.HOME
            },
            onSkipLogin = {
                isLoggedIn = true
                currentTab = NavTab.HOME
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = TextSecondary,
                    tonalElevation = androidx.compose.ui.unit.Dp(0f),
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar")
                ) {
                    NavTab.entries.forEach { tab ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ResQRed,
                                selectedTextColor = ResQRed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color(0xFFFFF0F2)
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = currentTab,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    NavTab.HOME -> HomeScreen(
                        onSosTriggered = {
                            val newEvent = HistoryItem(
                                id = System.currentTimeMillis().toString(),
                                title = "Emergency SOS Alert Broadcast Triggered",
                                timeDescription = "Just now · Active broadcast",
                                type = HistoryType.SOS_TRIGGERED,
                                dotColor = ResQRed
                            )
                            historyList = listOf(newEvent) + historyList
                        }
                    )
                    NavTab.MAP -> MapScreen(
                        onLocationShareToggled = { isSharing ->
                            if (isSharing) {
                                val newEvent = HistoryItem(
                                    id = System.currentTimeMillis().toString(),
                                    title = "Live location shared with trusted contacts",
                                    timeDescription = "Just now · Active",
                                    type = HistoryType.LOCATION_SHARED,
                                    dotColor = Color(0xFF22C55E)
                                )
                                historyList = listOf(newEvent) + historyList
                            }
                        }
                    )
                    NavTab.HISTORY -> HistoryScreen(
                        items = historyList
                    )
                    NavTab.PROFILE -> ProfileScreen(
                        userProfile = userProfile,
                        onLogout = {
                            isLoggedIn = false
                        },
                        onAddContact = { newContact ->
                            val updatedContacts = userProfile.emergencyContacts + newContact
                            userProfile = userProfile.copy(emergencyContacts = updatedContacts)
                            val newEvent = HistoryItem(
                                id = System.currentTimeMillis().toString(),
                                title = "Emergency contact added — ${newContact.name} (${newContact.relation})",
                                timeDescription = "Just now",
                                type = HistoryType.CONTACT_ADDED,
                                dotColor = Color(0xFF22C55E)
                            )
                            historyList = listOf(newEvent) + historyList
                        }
                    )
                }
            }
        }
    }
}

// Kept for backward compatibility with existing tests
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        ResQPulseApp()
    }
}
