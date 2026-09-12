package com.example.uselessuninstall.model

import android.graphics.drawable.Drawable

/**
 * Modular data class representing an application installed on the device.
 *
 * @property appName The user-facing label or title of the application (e.g., "Instagram").
 * @property packageName The unique package identifier of the application (e.g., "com.instagram.android").
 * @property appIcon The application's icon graphic represented as an Android [Drawable].
 */
data class AppInfo(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable? = null
) {

    /**
     * Backward-compatibility property mapping [appName] for existing UI references.
     */
    val name: String get() = appName

    /**
     * Backward-compatibility property mapping [appIcon] for existing UI references.
     */
    val iconDrawable: Drawable? get() = appIcon
}

/**
 * Mock applications used for Compose Previews, UI testing, and standalone interactive flow.
 */
object SampleApps {
    val instagram = AppInfo(
        appName = "Instagram",
        packageName = "com.instagram.android"
    )

    val spotify = AppInfo(
        appName = "Spotify",
        packageName = "com.spotify.music"
    )

    val candyCrush = AppInfo(
        appName = "Candy Crush Saga",
        packageName = "com.king.candycrushsaga"
    )

    val duolingo = AppInfo(
        appName = "Duolingo",
        packageName = "com.duolingo"
    )

    val twitter = AppInfo(
        appName = "X",
        packageName = "com.twitter.android"
    )

    val reddit = AppInfo(
        appName = "Reddit",
        packageName = "com.reddit.frontpage"
    )

    val sampleList: List<AppInfo> = listOf(
        instagram,
        spotify,
        candyCrush,
        duolingo,
        twitter,
        reddit
    )
}
