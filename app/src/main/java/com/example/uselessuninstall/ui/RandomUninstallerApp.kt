package com.example.uselessuninstall.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.screens.AnalysisScreen
import com.example.uselessuninstall.ui.screens.CountdownScreen
import com.example.uselessuninstall.ui.screens.HomeScreen
import com.example.uselessuninstall.ui.screens.RandomAppScreen
import com.example.uselessuninstall.ui.screens.UninstallResultScreen
import com.example.uselessuninstall.ui.state.UninstallerUiState
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

/**
 * Top-level application coordinator connected to [RandomUninstallerViewModel].
 *
 * Coordinates real device package management, purely random candidate selection,
 * and standard Android uninstallation intents with the serious diagnostic UI.
 *
 * @param viewModel The [RandomUninstallerViewModel] driving application state and logic.
 * @param modifier Modifier applied to the root container.
 */
@Composable
fun RandomUninstallerApp(
    viewModel: RandomUninstallerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    RandomUninstallerContent(
        uiState = uiState,
        modifier = modifier,
        onInitiateAnalysis = { viewModel.startAnalysis() },
        onAnalysisComplete = { viewModel.onAnalysisSequenceCompleted() },
        onCancelAnalysis = { viewModel.cancelToHome() },
        onUninstallClick = { viewModel.startCountdown() },
        onKeepClick = { viewModel.cancelToHome() },
        onConfirmUninstall = { viewModel.startCountdown() },
        onDismissConfirmation = { viewModel.dismissConfirmation() },
        onExecuteFinalUninstall = { app -> viewModel.executeUninstall(context, app) },
        onCancelCountdown = { viewModel.cancelCountdown() },
        onFindAnother = { viewModel.startAnalysis() },
        onBackHome = { viewModel.cancelToHome() },
        onTryAgain = { viewModel.startAnalysis() },
        onRetryScan = { viewModel.loadInstalledApps() }
    )
}

/**
 * Pure, stateless screen dispatcher based on [UninstallerUiState].
 */
@Composable
fun RandomUninstallerContent(
    uiState: UninstallerUiState,
    modifier: Modifier = Modifier,
    onInitiateAnalysis: () -> Unit,
    onAnalysisComplete: () -> Unit,
    onCancelAnalysis: () -> Unit,
    onUninstallClick: (AppInfo) -> Unit,
    onKeepClick: (AppInfo) -> Unit,
    onConfirmUninstall: (AppInfo) -> Unit,
    onDismissConfirmation: (AppInfo) -> Unit,
    onExecuteFinalUninstall: (AppInfo) -> Unit,
    onCancelCountdown: (AppInfo) -> Unit,
    onFindAnother: () -> Unit,
    onBackHome: () -> Unit,
    onTryAgain: (AppInfo) -> Unit,
    onRetryScan: () -> Unit = {}
) {
    Crossfade(
        targetState = uiState,
        label = "ScreenTransition",
        modifier = modifier
    ) { state ->
        when (state) {
            is UninstallerUiState.Loading -> {
                LoadingPackagesScreen()
            }

            is UninstallerUiState.Error -> {
                ErrorDiagnosticScreen(
                    message = state.message,
                    canRetry = state.canRetry,
                    onRetry = onRetryScan
                )
            }

            is UninstallerUiState.Home -> {
                HomeScreen(
                    onFindRandomApp = onInitiateAnalysis
                )
            }

            is UninstallerUiState.Analyzing -> {
                AnalysisScreen(
                    onAnalysisComplete = onAnalysisComplete,
                    onCancel = onCancelAnalysis
                )
            }

            is UninstallerUiState.RandomApp -> {
                RandomAppScreen(
                    app = state.app,
                    onUninstall = { onUninstallClick(state.app) },
                    onKeep = { onKeepClick(state.app) },
                    showConfirmationDialog = false
                )
            }

            is UninstallerUiState.Confirming -> {
                RandomAppScreen(
                    app = state.app,
                    onUninstall = { onUninstallClick(state.app) },
                    onKeep = { onKeepClick(state.app) },
                    showConfirmationDialog = true,
                    onConfirmUninstall = { onConfirmUninstall(state.app) },
                    onDismissConfirmation = { onDismissConfirmation(state.app) }
                )
            }

            is UninstallerUiState.Countdown -> {
                CountdownScreen(
                    app = state.app,
                    initialSeconds = state.secondsRemaining,
                    onExecuteUninstall = { onExecuteFinalUninstall(state.app) },
                    onCancel = { onCancelCountdown(state.app) }
                )
            }

            is UninstallerUiState.Result -> {
                UninstallResultScreen(
                    app = state.app,
                    isSuccess = state.isSuccess,
                    message = state.message,
                    onFindAnother = onFindAnother,
                    onBackHome = onBackHome,
                    onTryAgain = { onTryAgain(state.app) }
                )
            }
        }
    }
}

/**
 * Loading state rendered when reading installed applications from the device.
 */
@Composable
fun LoadingPackagesScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                strokeWidth = 5.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "INITIALIZING PACKAGE SCAN",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Scanning device package registry for eligible application candidates...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Graceful error state rendered when scan or selection fails or no eligible apps exist.
 */
@Composable
fun ErrorDiagnosticScreen(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "⚠️", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Diagnostic Scan Alert",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }

            if (canRetry) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Retry Diagnostic Scan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RandomUninstallerContentPreview() {
    UselessUninstallTheme {
        RandomUninstallerContent(
            uiState = UninstallerUiState.RandomApp(SampleApps.instagram),
            onInitiateAnalysis = {},
            onAnalysisComplete = {},
            onCancelAnalysis = {},
            onUninstallClick = {},
            onKeepClick = {},
            onConfirmUninstall = {},
            onDismissConfirmation = {},
            onExecuteFinalUninstall = {},
            onCancelCountdown = {},
            onFindAnother = {},
            onBackHome = {},
            onTryAgain = {}
        )
    }
}
