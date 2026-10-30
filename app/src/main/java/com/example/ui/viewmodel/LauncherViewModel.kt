package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppInfo
import com.example.data.model.GamingSession
import com.example.data.model.LauncherPreferences
import com.example.data.model.ProfileMode
import com.example.data.repository.HardwareTelemetry
import com.example.data.repository.LauncherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class LauncherTab {
    HOME,
    APP_DRAWER,
    GAMING_CENTER,
    PRIVACY_CENTER,
    CUSTOMIZATION,
    DIAGNOSTICS,
    ROM_ROADMAP
}

data class LauncherUiState(
    val allApps: List<AppInfo> = emptyList(),
    val drawerApps: List<AppInfo> = emptyList(),
    val favoriteApps: List<AppInfo> = emptyList(),
    val hiddenApps: List<AppInfo> = emptyList(),
    val gameApps: List<AppInfo> = emptyList(),
    val dockApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val currentTab: LauncherTab = LauncherTab.HOME,
    val previousTab: LauncherTab = LauncherTab.HOME,
    val preferences: LauncherPreferences = LauncherPreferences(),
    val telemetry: HardwareTelemetry = HardwareTelemetry(),
    val isDefaultLauncher: Boolean = false,
    val activeSession: GamingSession? = null,
    val sessionElapsedSeconds: Long = 0L,
    val sessionHistory: List<GamingSession> = emptyList(),
    val contextMenuApp: AppInfo? = null,
    val isPinUnlocked: Boolean = false,
    val isPinDialogShown: Boolean = false,
    val pinError: String? = null,
    val pendingProtectedTab: LauncherTab? = null,
    val toastMessage: String? = null
)

class LauncherViewModel(
    val repository: LauncherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        // Collect preferences
        viewModelScope.launch {
            repository.preferencesFlow.collectLatest { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
                repository.refreshApps()
            }
        }

        // Collect installed apps
        viewModelScope.launch {
            repository.installedApps.collectLatest { apps ->
                val prefs = repository.preferencesFlow
                _uiState.update { state ->
                    val hiddenSet = state.preferences.hiddenPackages
                    val favSet = state.preferences.favoritePackages
                    val gameSet = state.preferences.gamePackages

                    val mappedApps = apps.map { app ->
                        app.copy(
                            isGame = gameSet.contains(app.packageName),
                            isFavorite = favSet.contains(app.packageName),
                            isHidden = hiddenSet.contains(app.packageName)
                        )
                    }

                    val drawerList = mappedApps.filter { !it.isHidden }
                    val favList = mappedApps.filter { it.isFavorite }
                    val hiddenList = mappedApps.filter { it.isHidden }
                    val gamesList = mappedApps.filter { it.isGame }

                    // Default dock if not set: pick Phone, SMS, Browser/Camera, Settings, etc.
                    val dockPackages = state.preferences.dockPackages
                    val dockList = if (dockPackages.isNotEmpty()) {
                        dockPackages.mapNotNull { pkg -> mappedApps.find { it.packageName == pkg } }
                    } else {
                        // Pick first 4 common or favorite apps
                        mappedApps.take(4)
                    }

                    state.copy(
                        allApps = mappedApps,
                        drawerApps = filterByQuery(drawerList, state.searchQuery),
                        favoriteApps = favList,
                        hiddenApps = hiddenList,
                        gameApps = gamesList,
                        dockApps = dockList
                    )
                }
            }
        }

        // Collect active session
        viewModelScope.launch {
            repository.activeSession.collectLatest { session ->
                _uiState.update { it.copy(activeSession = session) }
            }
        }

        // Collect session history
        viewModelScope.launch {
            repository.sessionHistory.collectLatest { history ->
                _uiState.update { it.copy(sessionHistory = history) }
            }
        }

        // Telemetry loop
        viewModelScope.launch {
            while (isActive) {
                val telemetry = repository.getHardwareTelemetry()
                val isDefault = repository.isDefaultLauncher()
                _uiState.update {
                    it.copy(
                        telemetry = telemetry,
                        isDefaultLauncher = isDefault
                    )
                }

                // Session elapsed seconds update
                val session = _uiState.value.activeSession
                if (session != null) {
                    val elapsed = (System.currentTimeMillis() - session.startTimeMs) / 1000L
                    _uiState.update { it.copy(sessionElapsedSeconds = elapsed) }
                }

                // Adjust delay based on profile
                val delayTime = when (_uiState.value.preferences.activeProfileMode) {
                    ProfileMode.BATTERY_SAVER -> 12000L
                    ProfileMode.PERFORMANCE_MONITORING -> 2000L
                    else -> 4000L
                }
                delay(delayTime)
            }
        }

        // Initial refresh
        viewModelScope.launch {
            repository.refreshApps()
        }
    }

    private fun filterByQuery(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps
        val cleanQuery = query.trim().lowercase()
        return apps.filter {
            it.label.lowercase().contains(cleanQuery) || it.packageName.lowercase().contains(cleanQuery)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val visibleApps = state.allApps.filter { !it.isHidden }
            state.copy(
                searchQuery = query,
                drawerApps = filterByQuery(visibleApps, query)
            )
        }
    }

    fun navigateTo(tab: LauncherTab) {
        // Check if destination is protected (Privacy Center or Hidden Apps) and PIN is enabled
        val prefs = _uiState.value.preferences
        if (prefs.pinEnabled && prefs.pinHash.isNotBlank() && !_uiState.value.isPinUnlocked) {
            if (tab == LauncherTab.PRIVACY_CENTER) {
                _uiState.update {
                    it.copy(
                        isPinDialogShown = true,
                        pendingProtectedTab = tab,
                        pinError = null
                    )
                }
                return
            }
        }

        _uiState.update {
            it.copy(
                previousTab = it.currentTab,
                currentTab = tab,
                searchQuery = if (tab == LauncherTab.APP_DRAWER) it.searchQuery else ""
            )
        }
    }

    fun navigateBack(): Boolean {
        val current = _uiState.value.currentTab
        if (current != LauncherTab.HOME) {
            _uiState.update {
                it.copy(
                    currentTab = LauncherTab.HOME,
                    searchQuery = ""
                )
            }
            return true
        }
        return false
    }

    fun verifyPin(enteredPin: String) {
        val currentHash = _uiState.value.preferences.pinHash
        val isValid = repository.prefsManager.verifyPin(enteredPin, currentHash)
        if (isValid) {
            val nextTab = _uiState.value.pendingProtectedTab ?: LauncherTab.PRIVACY_CENTER
            _uiState.update {
                it.copy(
                    isPinUnlocked = true,
                    isPinDialogShown = false,
                    pendingProtectedTab = null,
                    pinError = null,
                    currentTab = nextTab
                )
            }
        } else {
            _uiState.update {
                it.copy(pinError = "Incorrect PIN. Please re-enter.")
            }
        }
    }

    fun dismissPinDialog() {
        _uiState.update {
            it.copy(
                isPinDialogShown = false,
                pendingProtectedTab = null,
                pinError = null
            )
        }
    }

    fun lockPrivateAreas() {
        _uiState.update { it.copy(isPinUnlocked = false) }
    }

    fun showContextMenu(app: AppInfo) {
        _uiState.update { it.copy(contextMenuApp = app) }
    }

    fun dismissContextMenu() {
        _uiState.update { it.copy(contextMenuApp = null) }
    }

    fun launchApp(packageName: String) {
        val success = repository.launchApp(packageName)
        if (!success) {
            _uiState.update { it.copy(toastMessage = "Unable to launch $packageName") }
        }
    }

    fun openAppDetails(packageName: String) {
        repository.openAppDetails(packageName)
    }

    fun toggleFavorite(packageName: String) {
        viewModelScope.launch {
            repository.prefsManager.toggleFavorite(packageName)
            repository.refreshApps()
        }
    }

    fun toggleHidden(packageName: String) {
        viewModelScope.launch {
            repository.prefsManager.toggleHidden(packageName)
            repository.refreshApps()
        }
    }

    fun toggleGame(packageName: String) {
        viewModelScope.launch {
            repository.prefsManager.toggleGame(packageName)
            repository.refreshApps()
        }
    }

    fun updateGridColumns(columns: Int) {
        viewModelScope.launch {
            repository.prefsManager.updateGridColumns(columns)
        }
    }

    fun updateIconSize(sizeDp: Int) {
        viewModelScope.launch {
            repository.prefsManager.updateIconSize(sizeDp)
        }
    }

    fun updateShowLabels(show: Boolean) {
        viewModelScope.launch {
            repository.prefsManager.updateShowLabels(show)
        }
    }

    fun updateWallpaperStyle(style: String) {
        viewModelScope.launch {
            repository.prefsManager.updateWallpaperStyle(style)
        }
    }

    fun updateWallpaperDim(dim: Int) {
        viewModelScope.launch {
            repository.prefsManager.updateWallpaperDim(dim)
        }
    }

    fun updateAccent(accent: String) {
        viewModelScope.launch {
            repository.prefsManager.updateAccent(accent)
        }
    }

    fun updateDarkTheme(dark: Boolean) {
        viewModelScope.launch {
            repository.prefsManager.updateDarkTheme(dark)
        }
    }

    fun updateReducedMotion(reduced: Boolean) {
        viewModelScope.launch {
            repository.prefsManager.updateReducedMotion(reduced)
        }
    }

    fun updateProfileMode(mode: ProfileMode) {
        viewModelScope.launch {
            repository.prefsManager.updateProfileMode(mode)
        }
    }

    fun updateBreakAlertMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.prefsManager.updateBreakAlertMinutes(minutes)
        }
    }

    fun setPin(pin: String) {
        viewModelScope.launch {
            repository.prefsManager.setPin(pin)
            _uiState.update { it.copy(isPinUnlocked = true, toastMessage = "Security PIN configured") }
        }
    }

    fun clearPin() {
        viewModelScope.launch {
            repository.prefsManager.clearPin()
            _uiState.update { it.copy(isPinUnlocked = false, toastMessage = "Security PIN removed") }
        }
    }

    fun startGamingSession(packageName: String, gameName: String) {
        repository.startGamingSession(packageName, gameName)
        _uiState.update { it.copy(toastMessage = "Session started: $gameName") }
        repository.launchApp(packageName)
    }

    fun stopGamingSession(notes: String = "") {
        val completed = repository.stopGamingSession(notes)
        if (completed != null) {
            _uiState.update {
                it.copy(
                    sessionElapsedSeconds = 0L,
                    toastMessage = "Session logged (${completed.durationSeconds / 60}m ${completed.durationSeconds % 60}s)"
                )
            }
        }
    }

    fun openDefaultLauncherSettings() {
        repository.openDefaultLauncherSettings()
    }

    fun openSystemSettings(action: String) {
        repository.openSystemSettings(action)
    }

    fun resetAllPreferences() {
        viewModelScope.launch {
            repository.prefsManager.resetAllPreferences()
            repository.refreshApps()
            _uiState.update { it.copy(toastMessage = "Settings restored to defaults") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
