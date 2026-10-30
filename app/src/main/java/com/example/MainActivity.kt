package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.datastore.LauncherPreferencesManager
import com.example.data.repository.LauncherRepository
import com.example.ui.components.AppContextMenuDialog
import com.example.ui.components.PinPromptDialog
import com.example.ui.components.ProceduralWallpaper
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.CustomRomRoadmapScreen
import com.example.ui.screens.CustomizationScreen
import com.example.ui.screens.DeviceDiagnosticsScreen
import com.example.ui.screens.GamingCenterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyCenterScreen
import com.example.ui.theme.AccentChoice
import com.example.ui.theme.PanelOutline
import com.example.ui.theme.UltinityTheme
import com.example.ui.viewmodel.LauncherTab
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val prefsManager = LauncherPreferencesManager(applicationContext)
                val repository = LauncherRepository(applicationContext, prefsManager)
                @Suppress("UNCHECKED_CAST")
                return LauncherViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            val accentChoice = try {
                AccentChoice.valueOf(uiState.preferences.accentName)
            } catch (_: Exception) {
                AccentChoice.CYAN
            }

            UltinityTheme(
                darkTheme = uiState.preferences.isDarkTheme,
                accentChoice = accentChoice
            ) {
                // Handle system back navigation
                BackHandler(enabled = uiState.currentTab != LauncherTab.HOME) {
                    viewModel.navigateBack()
                }

                // Show toast or snackbar
                LaunchedEffect(uiState.toastMessage) {
                    uiState.toastMessage?.let {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Procedural Canvas Wallpaper
                    ProceduralWallpaper(
                        style = uiState.preferences.wallpaperStyle,
                        dimPercent = uiState.preferences.wallpaperDimPercent,
                        accentColor = accentChoice.primary
                    )

                    // Main App Content Container
                    Scaffold(
                        containerColor = Color.Transparent,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.currentTab) {
                                LauncherTab.HOME -> HomeScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                                LauncherTab.APP_DRAWER -> AppDrawerScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                                LauncherTab.GAMING_CENTER -> GamingCenterScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                                LauncherTab.PRIVACY_CENTER -> PrivacyCenterScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                                LauncherTab.CUSTOMIZATION -> CustomizationScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                                LauncherTab.DIAGNOSTICS -> DeviceDiagnosticsScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                                LauncherTab.ROM_ROADMAP -> CustomRomRoadmapScreen(
                                    viewModel = viewModel
                                )
                            }

                            // Tactical Bottom Dock (Visible on HOME screen)
                            if (uiState.currentTab == LauncherTab.HOME) {
                                TacticalDock(
                                    onOpenDialer = { launchSystemDialer() },
                                    onOpenMessages = { launchSystemMessages() },
                                    onOpenDrawer = { viewModel.navigateTo(LauncherTab.APP_DRAWER) },
                                    onOpenGaming = { viewModel.navigateTo(LauncherTab.GAMING_CENTER) },
                                    onOpenBrowser = { launchSystemBrowser() },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .navigationBarsPadding()
                                        .padding(bottom = 12.dp)
                                )
                            }
                        }
                    }

                    // Context Menu Dialog
                    uiState.contextMenuApp?.let { app ->
                        AppContextMenuDialog(
                            app = app,
                            onDismiss = { viewModel.dismissContextMenu() },
                            onLaunch = { viewModel.launchApp(app.packageName) },
                            onToggleFavorite = { viewModel.toggleFavorite(app.packageName) },
                            onToggleGame = { viewModel.toggleGame(app.packageName) },
                            onToggleHidden = { viewModel.toggleHidden(app.packageName) },
                            onOpenDetails = { viewModel.openAppDetails(app.packageName) }
                        )
                    }

                    // Security PIN Verification Dialog
                    if (uiState.isPinDialogShown) {
                        PinPromptDialog(
                            errorText = uiState.pinError,
                            onDismiss = { viewModel.dismissPinDialog() },
                            onVerify = { viewModel.verifyPin(it) }
                        )
                    }
                }
            }
        }
    }

    private fun launchSystemDialer() {
        try {
            startActivity(Intent(Intent.ACTION_DIAL).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        } catch (_: Exception) {
            Toast.makeText(this, "Dialer app not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchSystemMessages() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("sms:")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            } catch (_: Exception) {
                Toast.makeText(this, "Messaging app not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchSystemBrowser() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this, "Browser app not found", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun TacticalDock(
    onOpenDialer: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenGaming: () -> Unit,
    onOpenBrowser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0C101A).copy(alpha = 0.95f))
            .border(1.dp, PanelOutline, RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("tactical_dock_container")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DockIconButton(
                icon = Icons.Default.Phone,
                contentDescription = "Phone",
                onClick = onOpenDialer
            )

            DockIconButton(
                icon = Icons.Default.Chat,
                contentDescription = "Messages",
                onClick = onOpenMessages
            )

            // Center Tactical App Drawer Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onOpenDrawer() }
                    .testTag("dock_app_drawer_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "Open App Drawer",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }

            DockIconButton(
                icon = Icons.Default.SportsEsports,
                contentDescription = "Gaming Center",
                onClick = onOpenGaming
            )

            DockIconButton(
                icon = Icons.Default.Language,
                contentDescription = "Web Browser",
                onClick = onOpenBrowser
            )
        }
    }
}

@Composable
private fun DockIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFF141A28))
            .border(1.dp, PanelOutline, CircleShape)
            .clickable { onClick() }
            .testTag("dock_btn_${contentDescription.lowercase()}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
    }
}
