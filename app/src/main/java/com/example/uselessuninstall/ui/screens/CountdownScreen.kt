package com.example.uselessuninstall.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.components.AppIconDisplay
import com.example.uselessuninstall.ui.components.CountdownNumber
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme
import kotlinx.coroutines.delay

/**
 * Distinct progression phases of the theatrical preparation stage.
 */
enum class TheatricalStage {
    PREPARING_DECISION,
    VERIFYING_DECISION,
    ARE_YOU_SURE,
    COUNTDOWN,
    READY_FOR_ACTION
}

/**
 * Screen 4: Theatrical preparation sequence and 3 → 2 → 1 countdown screen.
 *
 * Execution flow:
 * 1. "Preparing final decision..." (with Loading indicator)
 * 2. "Verifying decision..."
 * 3. "Are you sure??? 🤔"
 * 4. Countdown: 3 → 2 → 1
 * 5. Exposes the final action button ("Execute Uninstall") that will later call UninstallManager.
 *
 * NOTE: The countdown does NOT itself uninstall anything.
 *
 * @param app The [AppInfo] targeted for decommissioning.
 * @param onExecuteUninstall Callback triggered when the user taps the final action button.
 * @param onCancel Callback if the user aborts the countdown or preparation.
 * @param modifier Modifier applied to the screen root.
 * @param initialSeconds Starting countdown duration (defaults to 3).
 * @param autoCountdown True to run timers automatically; false for previewing.
 */
@Composable
fun CountdownScreen(
    app: AppInfo,
    onExecuteUninstall: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    initialSeconds: Int = 3,
    autoCountdown: Boolean = true
) {
    var stage by rememberSaveable { mutableStateOf(TheatricalStage.PREPARING_DECISION) }
    var secondsRemaining by rememberSaveable { mutableIntStateOf(initialSeconds) }

    if (autoCountdown) {
        LaunchedEffect(stage, secondsRemaining) {
            when (stage) {
                TheatricalStage.PREPARING_DECISION -> {
                    delay(1200L)
                    stage = TheatricalStage.VERIFYING_DECISION
                }
                TheatricalStage.VERIFYING_DECISION -> {
                    delay(1200L)
                    stage = TheatricalStage.ARE_YOU_SURE
                }
                TheatricalStage.ARE_YOU_SURE -> {
                    delay(1200L)
                    stage = TheatricalStage.COUNTDOWN
                }
                TheatricalStage.COUNTDOWN -> {
                    if (secondsRemaining > 1) {
                        delay(1000L)
                        secondsRemaining -= 1
                    } else {
                        delay(1000L)
                        // Transition to stage where the final action is exposed
                        stage = TheatricalStage.READY_FOR_ACTION
                    }
                }
                TheatricalStage.READY_FOR_ACTION -> {
                    // Waiting for explicit user action; does not auto-execute
                }
            }
        }
    }

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
            // Target App Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppIconDisplay(app = app, size = 36.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Central Theatrical Visual Area
            AnimatedContent(
                targetState = stage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TheatricalStageTransition"
            ) { currentStage ->
                when (currentStage) {
                    TheatricalStage.PREPARING_DECISION -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 5.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = "Preparing final decision...",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Compiling decommissioning parameters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TheatricalStage.VERIFYING_DECISION -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🛡️", fontSize = 34.sp)
                            }
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = "Verifying decision...",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Validating system integrity checksums",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TheatricalStage.ARE_YOU_SURE -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(text = "🤔", fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Are you sure??? 🤔",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Final opportunity to reconsider.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TheatricalStage.COUNTDOWN -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Finalizing in...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CountdownNumber(number = secondsRemaining)
                        }
                    }

                    TheatricalStage.READY_FOR_ACTION -> {
                        // Theatrical countdown complete: Exposing the final action button
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "⚠️", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Decision Finalized",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ready to request Android system uninstallation for ${app.name}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Actions Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (stage == TheatricalStage.READY_FOR_ACTION) {
                    // Final Action Exposed: Will later trigger UninstallManager.requestUninstall()
                    Button(
                        onClick = onExecuteUninstall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Execute Uninstall Request",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Abort Protocol",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Cancel Protocol",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "System confirmation dialog will follow upon authorization.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountdownScreenPreviewPreparing() {
    UselessUninstallTheme {
        CountdownScreen(
            app = SampleApps.instagram,
            onExecuteUninstall = {},
            onCancel = {},
            initialSeconds = 3,
            autoCountdown = false
        )
    }
}
