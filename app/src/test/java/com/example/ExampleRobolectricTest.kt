package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.datastore.LauncherPreferencesManager
import com.example.data.model.AppInfo
import com.example.data.model.GamingSession
import com.example.data.model.ProfileMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ULTINITY OS", appName)
    }

    @Test
    fun `verify pin hashing and validation logic`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefsManager = LauncherPreferencesManager(context)

        // Empty hash verification
        assertTrue(prefsManager.verifyPin("1234", ""))

        // Manual hash simulation test
        val salt = "ULTINITY_SALT_3681"
        val expectedHash = java.security.MessageDigest.getInstance("SHA-256")
            .digest(("4321" + salt).toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        assertTrue(prefsManager.verifyPin("4321", expectedHash))
        assertFalse(prefsManager.verifyPin("0000", expectedHash))
    }

    @Test
    fun `verify gaming session battery delta calculation`() {
        val session = GamingSession(
            packageName = "com.dts.freefireth",
            gameName = "Free Fire",
            startTimeMs = 1000L,
            durationSeconds = 1200L,
            batteryStartPercent = 85,
            batteryEndPercent = 78
        )
        assertEquals(7, session.batteryDelta)
    }

    @Test
    fun `verify profile mode descriptions`() {
        assertTrue(ProfileMode.BALANCED.description.isNotBlank())
        assertTrue(ProfileMode.BATTERY_SAVER.description.isNotBlank())
        assertTrue(ProfileMode.PERFORMANCE_MONITORING.description.isNotBlank())
    }
}
