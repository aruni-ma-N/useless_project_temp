package com.example.uselessuninstall

import android.content.pm.ApplicationInfo
import com.example.uselessuninstall.data.AndroidInstalledAppRetriever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InstalledAppRetrieverTest {

    private val currentAppPkg = "com.example.uselessuninstall"

    @Test
    fun testIsUserAppFiltersSystemApp() {
        val systemApp = ApplicationInfo().apply {
            packageName = "com.android.settings"
            flags = ApplicationInfo.FLAG_SYSTEM
        }

        assertFalse(
            "Pre-installed system apps must be excluded",
            AndroidInstalledAppRetriever.isUserApp(systemApp, currentAppPkg)
        )
    }

    @Test
    fun testIsUserAppIncludesNonSystemApp() {
        val userApp = ApplicationInfo().apply {
            packageName = "com.spotify.music"
            flags = 0 // No FLAG_SYSTEM
        }

        assertTrue(
            "User-installed apps must be included",
            AndroidInstalledAppRetriever.isUserApp(userApp, currentAppPkg)
        )
    }

    @Test
    fun testIsUserAppExcludesSelf() {
        val selfApp = ApplicationInfo().apply {
            packageName = currentAppPkg
            flags = 0
        }

        assertFalse(
            "Current application must be excluded",
            AndroidInstalledAppRetriever.isUserApp(selfApp, currentAppPkg)
        )
    }
}
