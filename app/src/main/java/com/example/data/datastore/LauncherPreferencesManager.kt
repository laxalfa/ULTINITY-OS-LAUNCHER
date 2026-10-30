package com.example.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.LauncherPreferences
import com.example.data.model.ProfileMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.dataStore by preferencesDataStore(name = "ultinity_launcher_prefs")

class LauncherPreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val ICON_SIZE_DP = intPreferencesKey("icon_size_dp")
        val SHOW_LABELS = booleanPreferencesKey("show_labels")
        val WALLPAPER_STYLE = stringPreferencesKey("wallpaper_style")
        val CUSTOM_WALLPAPER_URI = stringPreferencesKey("custom_wallpaper_uri")
        val WALLPAPER_DIM = intPreferencesKey("wallpaper_dim")
        val ACCENT_NAME = stringPreferencesKey("accent_name")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        val FAVORITE_PACKAGES = stringSetPreferencesKey("favorite_packages")
        val HIDDEN_PACKAGES = stringSetPreferencesKey("hidden_packages")
        val GAME_PACKAGES = stringSetPreferencesKey("game_packages")
        val DOCK_PACKAGES = stringPreferencesKey("dock_packages")
        val ACTIVE_PROFILE_MODE = stringPreferencesKey("active_profile_mode")
        val BREAK_ALERT_MINUTES = intPreferencesKey("break_alert_minutes")
        val RECENT_SESSIONS_JSON = stringPreferencesKey("recent_sessions_json")
    }

    val preferencesFlow: Flow<LauncherPreferences> = context.dataStore.data.map { prefs ->
        val modeStr = prefs[PreferencesKeys.ACTIVE_PROFILE_MODE] ?: ProfileMode.BALANCED.name
        val activeMode = try {
            ProfileMode.valueOf(modeStr)
        } catch (_: Exception) {
            ProfileMode.BALANCED
        }

        val dockList = prefs[PreferencesKeys.DOCK_PACKAGES]?.split(",")?.filter { it.isNotBlank() }
            ?: emptyList()

        LauncherPreferences(
            gridColumns = prefs[PreferencesKeys.GRID_COLUMNS] ?: 4,
            iconSizeDp = prefs[PreferencesKeys.ICON_SIZE_DP] ?: 52,
            showLabels = prefs[PreferencesKeys.SHOW_LABELS] ?: true,
            wallpaperStyle = prefs[PreferencesKeys.WALLPAPER_STYLE] ?: "CYBER_GRID",
            customWallpaperUri = prefs[PreferencesKeys.CUSTOM_WALLPAPER_URI],
            wallpaperDimPercent = prefs[PreferencesKeys.WALLPAPER_DIM] ?: 20,
            accentName = prefs[PreferencesKeys.ACCENT_NAME] ?: "CYAN",
            isDarkTheme = prefs[PreferencesKeys.DARK_THEME] ?: true,
            reducedMotion = prefs[PreferencesKeys.REDUCED_MOTION] ?: false,
            pinHash = prefs[PreferencesKeys.PIN_HASH] ?: "",
            pinEnabled = prefs[PreferencesKeys.PIN_ENABLED] ?: false,
            favoritePackages = prefs[PreferencesKeys.FAVORITE_PACKAGES] ?: emptySet(),
            hiddenPackages = prefs[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet(),
            gamePackages = prefs[PreferencesKeys.GAME_PACKAGES] ?: emptySet(),
            dockPackages = dockList,
            activeProfileMode = activeMode,
            breakAlertMinutes = prefs[PreferencesKeys.BREAK_ALERT_MINUTES] ?: 45
        )
    }

    val sessionsRawFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.RECENT_SESSIONS_JSON] ?: ""
    }

    suspend fun updateGridColumns(columns: Int) {
        context.dataStore.edit { it[PreferencesKeys.GRID_COLUMNS] = columns }
    }

    suspend fun updateIconSize(sizeDp: Int) {
        context.dataStore.edit { it[PreferencesKeys.ICON_SIZE_DP] = sizeDp }
    }

    suspend fun updateShowLabels(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_LABELS] = show }
    }

    suspend fun updateWallpaperStyle(style: String) {
        context.dataStore.edit { it[PreferencesKeys.WALLPAPER_STYLE] = style }
    }

    suspend fun updateCustomWallpaperUri(uri: String?) {
        context.dataStore.edit {
            if (uri != null) {
                it[PreferencesKeys.CUSTOM_WALLPAPER_URI] = uri
            } else {
                it.remove(PreferencesKeys.CUSTOM_WALLPAPER_URI)
            }
        }
    }

    suspend fun updateWallpaperDim(dim: Int) {
        context.dataStore.edit { it[PreferencesKeys.WALLPAPER_DIM] = dim }
    }

    suspend fun updateAccent(accent: String) {
        context.dataStore.edit { it[PreferencesKeys.ACCENT_NAME] = accent }
    }

    suspend fun updateDarkTheme(dark: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_THEME] = dark }
    }

    suspend fun updateReducedMotion(reduced: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.REDUCED_MOTION] = reduced }
    }

    suspend fun setPin(pin: String) {
        val hash = hashPin(pin)
        context.dataStore.edit {
            it[PreferencesKeys.PIN_HASH] = hash
            it[PreferencesKeys.PIN_ENABLED] = pin.isNotBlank()
        }
    }

    suspend fun clearPin() {
        context.dataStore.edit {
            it[PreferencesKeys.PIN_HASH] = ""
            it[PreferencesKeys.PIN_ENABLED] = false
        }
    }

    fun verifyPin(pin: String, currentHash: String): Boolean {
        if (currentHash.isEmpty()) return true
        return hashPin(pin) == currentHash
    }

    suspend fun toggleFavorite(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[PreferencesKeys.FAVORITE_PACKAGES] ?: emptySet()).toMutableSet()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[PreferencesKeys.FAVORITE_PACKAGES] = current
        }
    }

    suspend fun toggleHidden(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet()).toMutableSet()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[PreferencesKeys.HIDDEN_PACKAGES] = current
        }
    }

    suspend fun toggleGame(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[PreferencesKeys.GAME_PACKAGES] ?: emptySet()).toMutableSet()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[PreferencesKeys.GAME_PACKAGES] = current
        }
    }

    suspend fun updateDock(dock: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.DOCK_PACKAGES] = dock.joinToString(",")
        }
    }

    suspend fun updateProfileMode(mode: ProfileMode) {
        context.dataStore.edit { it[PreferencesKeys.ACTIVE_PROFILE_MODE] = mode.name }
    }

    suspend fun updateBreakAlertMinutes(minutes: Int) {
        context.dataStore.edit { it[PreferencesKeys.BREAK_ALERT_MINUTES] = minutes }
    }

    suspend fun saveSessionsRaw(raw: String) {
        context.dataStore.edit { it[PreferencesKeys.RECENT_SESSIONS_JSON] = raw }
    }

    suspend fun resetAllPreferences() {
        context.dataStore.edit { it.clear() }
    }

    private fun hashPin(pin: String): String {
        val salt = "ULTINITY_SALT_3681"
        val bytes = MessageDigest.getInstance("SHA-256").digest((pin + salt).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
