package com.example.uselessuninstall.manager

import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UninstallManagerTest {

    @Test
    fun testIsValidPackageNameWithValidPackages() {
        assertTrue(UninstallManager.isValidPackageName("com.spotify.music"))
        assertTrue(UninstallManager.isValidPackageName("com.instagram.android"))
        assertTrue(UninstallManager.isValidPackageName("com.example.uselessuninstall"))
        assertTrue(UninstallManager.isValidPackageName(SampleApps.duolingo.packageName))
        assertTrue(UninstallManager.isValidPackageName("a.b.c"))
        assertTrue(UninstallManager.isValidPackageName("org.test.sample123"))
    }

    @Test
    fun testIsValidPackageNameWithInvalidPackages() {
        assertFalse(UninstallManager.isValidPackageName(null))
        assertFalse(UninstallManager.isValidPackageName(""))
        assertFalse(UninstallManager.isValidPackageName("   "))
        assertFalse(UninstallManager.isValidPackageName("\t"))
        assertFalse(UninstallManager.isValidPackageName("\n"))
        assertFalse(UninstallManager.isValidPackageName("   \r\n   "))
    }

    @Test
    fun testBuildPackageUriString() {
        val packageName = "com.spotify.music"
        val expected = "package:com.spotify.music"
        val actual = UninstallManager.buildPackageUriString(packageName)

        assertEquals("Package URI must follow standard package:scheme syntax", expected, actual)
    }

    @Test
    fun testBuildPackageUriStringFromAppInfo() {
        val app = SampleApps.candyCrush
        val expected = "package:com.king.candycrushsaga"
        val actual = UninstallManager.buildPackageUriString(app.packageName)

        assertEquals("Package URI string for AppInfo must correctly format package URI", expected, actual)
    }

    @Test
    fun testUriPackageSchemeConstant() {
        assertEquals("package", UninstallManager.URI_PACKAGE_SCHEME)
    }

    @Test
    fun testBuildPackageUriStringFormatWithVariousPackages() {
        val packages = listOf(
            "com.android.chrome",
            "org.mozilla.firefox",
            "com.google.android.youtube",
            "com.example.uselessuninstall"
        )

        for (pkg in packages) {
            val uriString = UninstallManager.buildPackageUriString(pkg)
            assertTrue("Must start with 'package:' prefix", uriString.startsWith("package:"))
            assertEquals("package:$pkg", uriString)
        }
    }
}
