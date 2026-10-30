package com.example.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import com.example.data.datastore.LauncherPreferencesManager
import com.example.data.model.AppInfo
import com.example.data.model.GamingSession
import com.example.data.model.LauncherPreferences
import com.example.data.model.ProfileMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

data class HardwareTelemetry(
    val totalRamBytes: Long = 0L,
    val availRamBytes: Long = 0L,
    val isLowRam: Boolean = false,
    val ramThresholdBytes: Long = 0L,
    val appUsedRamBytes: Long = 0L,
    val batteryPercent: Int = 0,
    val isCharging: Boolean = false,
    val chargeSource: String = "Battery",
    val batteryTempC: Float = 0f,
    val batteryVoltageMv: Int = 0,
    val totalStorageBytes: Long = 0L,
    val availStorageBytes: Long = 0L,
    val networkType: String = "Disconnected",
    val updateTimestamp: Long = System.currentTimeMillis()
)

class LauncherRepository(
    private val context: Context,
    val prefsManager: LauncherPreferencesManager
) {
    private val packageManager: PackageManager = context.packageManager
    private val iconCache = ConcurrentHashMap<String, Drawable>()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps = _installedApps.asStateFlow()

    private val _activeSession = MutableStateFlow<GamingSession?>(null)
    val activeSession = _activeSession.asStateFlow()

    private val _sessionHistory = MutableStateFlow<List<GamingSession>>(emptyList())
    val sessionHistory = _sessionHistory.asStateFlow()

    val preferencesFlow: Flow<LauncherPreferences> = prefsManager.preferencesFlow

    suspend fun refreshApps() = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
        val currentPrefs = preferencesFlow.first()

        val apps = resolveList.mapNotNull { resolve ->
            val pkg = resolve.activityInfo.packageName
            val act = resolve.activityInfo.name
            // We can show all apps or omit self
            val label = try {
                resolve.loadLabel(packageManager).toString()
            } catch (_: Exception) {
                pkg
            }

            // Cache icon
            try {
                if (!iconCache.containsKey(pkg)) {
                    val icon = resolve.loadIcon(packageManager)
                    iconCache[pkg] = icon
                }
            } catch (_: Exception) { }

            val installTime = try {
                val pkgInfo = packageManager.getPackageInfo(pkg, 0)
                pkgInfo.firstInstallTime
            } catch (_: Exception) {
                0L
            }

            AppInfo(
                packageName = pkg,
                activityName = act,
                label = label,
                isGame = currentPrefs.gamePackages.contains(pkg),
                isFavorite = currentPrefs.favoritePackages.contains(pkg),
                isHidden = currentPrefs.hiddenPackages.contains(pkg),
                installTime = installTime
            )
        }.sortedBy { it.label.lowercase() }

        _installedApps.value = apps
    }

    fun getAppIcon(packageName: String): Drawable? {
        iconCache[packageName]?.let { return it }
        return try {
            val icon = packageManager.getApplicationIcon(packageName)
            iconCache[packageName] = icon
            icon
        } catch (_: Exception) {
            null
        }
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) { }
    }

    fun openDefaultLauncherSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            } else {
                Intent(Settings.ACTION_HOME_SETTINGS)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (_: Exception) { }
        }
    }

    fun openSystemSettings(action: String) {
        try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (_: Exception) { }
        }
    }

    fun isDefaultLauncher(): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolve = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolve?.activityInfo?.packageName == context.packageName
        } catch (_: Exception) {
            false
        }
    }

    fun getHardwareTelemetry(): HardwareTelemetry {
        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)

        val runtime = Runtime.getRuntime()
        val appUsed = runtime.totalMemory() - runtime.freeMemory()

        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPercent = if (level >= 0 && scale > 0) (level * 100) / scale else 0

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val chargeSource = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Adapter"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> if (isCharging) "Charging" else "Battery"
        }

        val tempRaw = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempC = tempRaw / 10.0f
        val voltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0

        // Storage
        var totalStorage = 0L
        var availStorage = 0L
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            totalStorage = stat.blockCountLong * stat.blockSizeLong
            availStorage = stat.availableBlocksLong * stat.blockSizeLong
        } catch (_: Exception) { }

        // Network
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNet = connMgr?.activeNetwork
        val caps = connMgr?.getNetworkCapabilities(activeNet)
        val netType = when {
            caps == null -> "Offline"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 5G/4G"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected"
        }

        return HardwareTelemetry(
            totalRamBytes = memInfo.totalMem,
            availRamBytes = memInfo.availMem,
            isLowRam = memInfo.lowMemory,
            ramThresholdBytes = memInfo.threshold,
            appUsedRamBytes = appUsed,
            batteryPercent = batteryPercent,
            isCharging = isCharging,
            chargeSource = chargeSource,
            batteryTempC = tempC,
            batteryVoltageMv = voltage,
            totalStorageBytes = totalStorage,
            availStorageBytes = availStorage,
            networkType = netType
        )
    }

    fun startGamingSession(packageName: String, gameName: String) {
        val currentBattery = getHardwareTelemetry().batteryPercent
        _activeSession.value = GamingSession(
            packageName = packageName,
            gameName = gameName,
            startTimeMs = System.currentTimeMillis(),
            batteryStartPercent = currentBattery
        )
    }

    fun stopGamingSession(notes: String = ""): GamingSession? {
        val current = _activeSession.value ?: return null
        val duration = (System.currentTimeMillis() - current.startTimeMs) / 1000L
        val currentBattery = getHardwareTelemetry().batteryPercent

        val completed = current.copy(
            durationSeconds = duration,
            batteryEndPercent = currentBattery,
            notes = notes
        )
        _activeSession.value = null

        val updated = listOf(completed) + _sessionHistory.value
        _sessionHistory.value = updated.take(20)
        return completed
    }
}
