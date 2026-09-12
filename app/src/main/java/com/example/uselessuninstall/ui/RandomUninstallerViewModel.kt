package com.example.uselessuninstall.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uselessuninstall.data.AndroidInstalledAppRetriever
import com.example.uselessuninstall.data.InstalledAppRetriever
import com.example.uselessuninstall.manager.AndroidUninstallManager
import com.example.uselessuninstall.manager.DefaultRandomAppSelector
import com.example.uselessuninstall.manager.RandomAppSelector
import com.example.uselessuninstall.manager.UninstallManager
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.ui.state.UninstallerUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Architectural coordinator connecting [InstalledAppRetriever], [RandomAppSelector],
 * and [UninstallManager] to the Compose UI layer.
 *
 * Rules & Invariants:
 * - Does NOT contain Compose UI elements.
 * - Does NOT call PackageManager directly (delegated to [InstalledAppRetriever]).
 * - Does NOT evaluate application usefulness; selection is purely delegated to [RandomAppSelector].
 * - Does NOT trigger uninstallation automatically; [UninstallManager] is invoked only upon explicit user authorization.
 *
 * @param appRetriever Component retrieving eligible installed applications from PackageManager.
 * @param appSelector Component randomly selecting an application candidate.
 * @param uninstallManager Component issuing the standard Android uninstallation Intent.
 * @param ioDispatcher Background coroutine dispatcher for package inspection.
 * @param customScope Optional custom [CoroutineScope] for unit testing without relying on Main Looper.
 */
class RandomUninstallerViewModel(
    private val appRetriever: InstalledAppRetriever,
    private val appSelector: RandomAppSelector = DefaultRandomAppSelector(),
    private val uninstallManager: UninstallManager = AndroidUninstallManager(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val customScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope
        get() = customScope ?: viewModelScope

    // Internal and public state holding the list of retrieved real user-installed apps
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    // Internal and public state holding the currently selected real application candidate
    private val _selectedApp = MutableStateFlow<AppInfo?>(null)
    val selectedApp: StateFlow<AppInfo?> = _selectedApp.asStateFlow()

    // High-level UI state driving Compose screens
    private val _uiState = MutableStateFlow<UninstallerUiState>(UninstallerUiState.Loading)
    val uiState: StateFlow<UninstallerUiState> = _uiState.asStateFlow()

    init {
        loadInstalledApps()
    }

    /**
     * Queries [InstalledAppRetriever] asynchronously to retrieve all eligible user-installed apps.
     */
    fun loadInstalledApps() {
        _uiState.value = UninstallerUiState.Loading
        scope.launch {
            try {
                val apps = withContext(ioDispatcher) {
                    appRetriever.getInstalledUserApps()
                }
                _installedApps.value = apps

                if (apps.isEmpty()) {
                    _uiState.value = UninstallerUiState.Error(
                        message = "No eligible applications found. All detected packages are pre-installed or system-protected.",
                        canRetry = true
                    )
                } else {
                    _uiState.value = UninstallerUiState.Home
                }
            } catch (e: Exception) {
                _uiState.value = UninstallerUiState.Error(
                    message = "System scan failure: ${e.localizedMessage ?: "Unable to read installed applications."}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * Begins the diagnostic flow by invoking [RandomAppSelector] on the retrieved application pool
     * and launching the theatrical analysis sequence.
     */
    fun startAnalysis() {
        val currentApps = _installedApps.value
        if (currentApps.isEmpty()) {
            _uiState.value = UninstallerUiState.Error(
                message = "No eligible applications available for analysis. Please refresh the scan.",
                canRetry = true
            )
            return
        }

        // Pure random selection from the real application pool
        val chosen = appSelector.selectRandomApp(currentApps)
        if (chosen == null) {
            _uiState.value = UninstallerUiState.Error(
                message = "Selection engine failure: Unable to select a candidate application.",
                canRetry = true
            )
            return
        }

        // Store real chosen AppInfo
        _selectedApp.value = chosen
        // Transition to theatrical analysis sequence
        _uiState.value = UninstallerUiState.Analyzing(chosen)
    }

    /**
     * Called when the 8-step theatrical analysis sequence finishes.
     * Transitions to the Candidate Selected screen displaying the REAL selected [AppInfo].
     */
    fun onAnalysisSequenceCompleted() {
        val app = _selectedApp.value
        if (app != null) {
            _uiState.value = UninstallerUiState.RandomApp(app)
        } else {
            _uiState.value = UninstallerUiState.Error(
                message = "Candidate application data unavailable.",
                canRetry = true
            )
        }
    }

    /**
     * User tapped "Proceed to Decommission" on the candidate screen.
     */
    fun requestConfirmation() {
        val app = _selectedApp.value ?: return
        _uiState.value = UninstallerUiState.Confirming(app)
    }

    /**
     * User dismissed the confirmation dialog.
     */
    fun dismissConfirmation() {
        val app = _selectedApp.value ?: return
        _uiState.value = UninstallerUiState.RandomApp(app)
    }

    /**
     * User confirmed intent; transitions to theatrical preparation and countdown.
     * NOTE: Does NOT launch the uninstall Intent.
     */
    fun startCountdown() {
        val app = _selectedApp.value ?: return
        _uiState.value = UninstallerUiState.Countdown(app = app, secondsRemaining = 3)
    }

    /**
     * User aborted the countdown or preparation sequence.
     */
    fun cancelCountdown() {
        val app = _selectedApp.value ?: return
        _uiState.value = UninstallerUiState.RandomApp(app)
    }

    /**
     * User navigated back to home or cancelled completely.
     */
    fun cancelToHome() {
        _selectedApp.value = null
        _uiState.value = UninstallerUiState.Home
    }

    /**
     * Dispatches the uninstallation request via [UninstallManager] ONLY upon explicit user confirmation.
     *
     * Handles:
     * - No selected AppInfo
     * - Invalid package name
     * - No Activity available to launch the Intent
     *
     * @param context Android [Context] (Activity) used to trigger the system uninstallation Intent.
     * @param app Optional explicit [AppInfo] candidate to uninstall. Falls back to [_selectedApp.value].
     */
    fun executeUninstall(context: Context, app: AppInfo? = null) {
        val targetApp = app ?: _selectedApp.value
        if (targetApp == null) {
            _uiState.value = UninstallerUiState.Error(
                message = "No application candidate selected for uninstallation.",
                canRetry = true
            )
            return
        }

        if (targetApp.packageName.isBlank()) {
            _uiState.value = UninstallerUiState.Error(
                message = "Invalid package name for application: ${targetApp.appName}.",
                canRetry = true
            )
            return
        }

        val dispatched = uninstallManager.requestUninstall(context, targetApp)

        _uiState.value = UninstallerUiState.Result(
            app = targetApp,
            isSuccess = dispatched,
            message = if (dispatched) {
                "System uninstallation prompt dispatched for ${targetApp.appName}."
            } else {
                "Unable to launch system uninstallation for ${targetApp.appName}. No activity available to handle the uninstall Intent."
            }
        )
    }

    /**
     * Factory for creating instances of [RandomUninstallerViewModel] with device dependencies.
     */
    class Factory(
        private val context: Context,
        private val appRetriever: InstalledAppRetriever = AndroidInstalledAppRetriever(context.applicationContext),
        private val appSelector: RandomAppSelector = DefaultRandomAppSelector(),
        private val uninstallManager: UninstallManager = AndroidUninstallManager()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RandomUninstallerViewModel::class.java)) {
                return RandomUninstallerViewModel(
                    appRetriever = appRetriever,
                    appSelector = appSelector,
                    uninstallManager = uninstallManager
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
