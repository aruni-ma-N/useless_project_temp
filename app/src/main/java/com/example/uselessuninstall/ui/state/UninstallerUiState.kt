package com.example.uselessuninstall.ui.state

import com.example.uselessuninstall.model.AppInfo

/**
 * Represents the distinct UI states of the Application Analyzer.
 *
 * Driven by [RandomUninstallerViewModel] without coupling Compose directly to PackageManager.
 */
sealed interface UninstallerUiState {
    /**
     * Scanning the device's package registry to retrieve eligible user applications.
     */
    data object Loading : UninstallerUiState

    /**
     * Initial landing screen with diagnostic title, description, and "Initiate System Analysis" action.
     */
    data object Home : UninstallerUiState

    /**
     * Failure state representing retrieval errors, empty package lists, or selection issues.
     *
     * @property message Descriptive failure explanation.
     * @property canRetry Whether the user can trigger a re-scan.
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : UninstallerUiState

    /**
     * Theatrical multi-step analysis sequence screen ("Scanning installed applications...", etc.).
     *
     * @property app The real candidate [AppInfo] chosen by [RandomAppSelector].
     */
    data class Analyzing(val app: AppInfo) : UninstallerUiState

    /**
     * Displaying the identified candidate application with options to "Proceed to Decommission" or "Disregard & Keep".
     */
    data class RandomApp(val app: AppInfo) : UninstallerUiState

    /**
     * Displaying the confirmation dialog: "Security Verification: Are you sure? 🤔".
     */
    data class Confirming(val app: AppInfo) : UninstallerUiState

    /**
     * Displaying the theatrical preparation and countdown screen before exposing the final action.
     *
     * @property app The target app to be uninstalled.
     * @property secondsRemaining The current remaining seconds (default 3 down to 1).
     */
    data class Countdown(
        val app: AppInfo,
        val secondsRemaining: Int = 3
    ) : UninstallerUiState

    /**
     * Displaying the final result screen (either success or cancelled/failed).
     *
     * @property app The app that was targeted.
     * @property isSuccess True if uninstalled successfully, false if cancelled or failed.
     * @property message Optional descriptive message for the result.
     */
    data class Result(
        val app: AppInfo,
        val isSuccess: Boolean,
        val message: String? = null
    ) : UninstallerUiState
}
