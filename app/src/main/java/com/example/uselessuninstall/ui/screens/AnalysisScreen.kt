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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme
import kotlinx.coroutines.delay

/**
 * Screen presenting a theatrical, serious-looking analysis sequence.
 *
 * Steps through proprietary diagnostic messages with a serious, slightly suspicious tone,
 * leading into the candidate selected display.
 *
 * @param onAnalysisComplete Callback triggered once the entire sequence has executed.
 * @param onCancel Callback if the user aborts the analysis.
 * @param modifier Modifier applied to the screen root.
 * @param autoProgress True to automatically advance the analysis steps; false for static previews.
 */
@Composable
fun AnalysisScreen(
    onAnalysisComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    autoProgress: Boolean = true
) {
    val steps = rememberSaveable {
        listOf(
            "Scanning installed applications...",
            "Reading package metadata...",
            "Analysing application behaviour...",
            "Evaluating uninstall candidates...",
            "Cross-referencing application data...",
            "Running proprietary analysis...",
            "Finalising decision...",
            "Decision complete."
        )
    }

    var currentStepIndex by rememberSaveable { mutableIntStateOf(0) }

    if (autoProgress) {
        LaunchedEffect(currentStepIndex) {
            if (currentStepIndex < steps.lastIndex) {
                delay(650L)
                currentStepIndex += 1
            } else {
                delay(700L)
                onAnalysisComplete()
            }
        }
    }

    val progress = (currentStepIndex + 1).toFloat() / steps.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Diagnostic Header & Radar Spinner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "SYSTEM SCAN IN PROGRESS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(100.dp),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔬",
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Current Active Step Heading
                AnimatedContent(
                    targetState = steps[currentStepIndex],
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ActiveStepAnimation"
                ) { stepText ->
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Phase ${currentStepIndex + 1} of ${steps.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Middle Section: Terminal-style Diagnostic Log Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNOSTIC LOG",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display log lines up to current index
                    steps.take(currentStepIndex + 1).takeLast(4).forEachIndexed { index, step ->
                        val isCurrent = step == steps[currentStepIndex]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isCurrent) "▶" else "✓",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Bottom Section: Abort Action
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Abort Analysis",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisScreenPreview() {
    UselessUninstallTheme {
        AnalysisScreen(
            onAnalysisComplete = {},
            onCancel = {},
            autoProgress = false
        )
    }
}
