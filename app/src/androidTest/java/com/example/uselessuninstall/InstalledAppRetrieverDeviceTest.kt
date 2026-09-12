package com.example.uselessuninstall

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uselessuninstall.data.AndroidInstalledAppRetriever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test executed on a connected physical Android device.
 *
 * Verifies that [AndroidInstalledAppRetriever] correctly queries the device's real [android.content.pm.PackageManager],
 * retrieves user applications with valid metadata and icons, and excludes system packages.
 */
@RunWith(AndroidJUnit4::class)
class InstalledAppRetrieverDeviceTest {

    private val tag = "InstalledAppRetrieverDeviceTest"

    @Test
    fun verifyInstalledUserAppsOnDevice() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageManager = context.packageManager
        val retriever = AndroidInstalledAppRetriever(context)

        // 1. Retrieve apps using real PackageManager
        val apps = retriever.getInstalledUserApps()

        Log.i(tag, "==================================================")
        Log.i(tag, "Total user-installed applications found: ${apps.size}")
        Log.i(tag, "==================================================")

        // 2. Verify non-empty list
        assertTrue(
            "Expected at least one user-installed application on this physical device",
            apps.isNotEmpty()
        )

        // 3. Verify each retrieved app's fields and system app exclusion
        for ((index, app) in apps.withIndex()) {
            Log.i(tag, "[${index + 1}/${apps.size}] Name: '${app.appName}', Package: '${app.packageName}', Icon: ${app.appIcon?.javaClass?.simpleName}")

            // Verify appName is populated and not blank
            assertTrue(
                "appName should not be blank for package: ${app.packageName}",
                app.appName.isNotBlank()
            )

            // Verify packageName is populated and not blank
            assertTrue(
                "packageName should not be blank for app: ${app.appName}",
                app.packageName.isNotBlank()
            )

            // Verify appIcon is populated (not null)
            assertNotNull(
                "appIcon must not be null for ${app.appName} (${app.packageName})",
                app.appIcon
            )

            // Verify self is excluded
            assertNotEquals(
                "The app itself (${context.packageName}) must not be in the retrieved pool",
                context.packageName,
                app.packageName
            )

            // Verify the app is genuinely not a system app according to PackageManager flags
            val appInfo = packageManager.getApplicationInfo(app.packageName, 0)
            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            assertFalse(
                "App '${app.appName}' (${app.packageName}) has FLAG_SYSTEM set and should have been excluded",
                isSystemApp
            )
        }
    }
}
