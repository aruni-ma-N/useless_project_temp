package com.example.uselessuninstall

import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.state.UninstallerUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppInfoTest {

    @Test
    fun testAppInfoCreation() {
        val app = AppInfo(appName = "Instagram", packageName = "com.instagram.android")
        assertEquals("Instagram", app.appName)
        assertEquals("Instagram", app.name)
        assertEquals("com.instagram.android", app.packageName)
        assertEquals(null, app.appIcon)
    }

    @Test
    fun testSampleAppsList() {
        val apps = SampleApps.sampleList
        assertFalse(apps.isEmpty())
        assertTrue(apps.any { it.name == "Instagram" })
        assertTrue(apps.any { it.name == "Spotify" })
    }

    @Test
    fun testUiStateTransitions() {
        val app = SampleApps.instagram

        // 1. Initial state
        var state: UninstallerUiState = UninstallerUiState.Home
        assertTrue(state is UninstallerUiState.Home)

        // 2. Finding a random app
        state = UninstallerUiState.RandomApp(app)
        assertEquals(app, (state as UninstallerUiState.RandomApp).app)

        // 3. Requesting confirmation
        state = UninstallerUiState.Confirming(app)
        assertEquals(app, (state as UninstallerUiState.Confirming).app)

        // 4. Starting countdown
        state = UninstallerUiState.Countdown(app = app, secondsRemaining = 5)
        assertEquals(5, (state as UninstallerUiState.Countdown).secondsRemaining)

        // 5. Final Result
        state = UninstallerUiState.Result(app = app, isSuccess = true)
        assertTrue((state as UninstallerUiState.Result).isSuccess)
    }
}
