package com.example.uselessuninstall.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.components.AppInfoCard
import com.example.uselessuninstall.ui.components.UninstallConfirmationDialog
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

/**
 * Screen 2: Displays the identified candidate application.
 *
 * NOTE: The [app] parameter currently receives placeholder preview data in UI preview mode.
 * In the final integration, this will receive the real [AppInfo] supplied by the application logic.
 *
 * @param app The [AppInfo] of the selected application.
 * @param onUninstall Callback when the user taps "Uninstall" (proceeds to confirmation/theatrical countdown).
 * @param onKeep Callback when the user taps "Keep It" (returns home or initiates new analysis).
 * @param modifier Modifier applied to the screen root.
 * @param showConfirmationDialog If true, renders [UninstallConfirmationDialog] over this screen.
 * @param onConfirmUninstall Callback when the user confirms "Yes, Uninstall" in the dialog.
 * @param onDismissConfirmation Callback when the user cancels the confirmation dialog.
 */
@Composable
fun RandomAppScreen(
    app: AppInfo,
    onUninstall: () -> Unit,
    onKeep: () -> Unit,
    modifier: Modifier = Modifier,
    showConfirmationDialog: Boolean = false,
    onConfirmUninstall: () -> Unit = {},
    onDismissConfirmation: () -> Unit = {}
) {
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
            // Header / Serious Result Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Diagnostic Verdict Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ANALYSIS COMPLETE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Serious-looking Result Announcement
                Text(
                    text = "Candidate selected.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "We have identified an application.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Selected App Details Card (Icon, Name, Package)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppInfoCard(
                    app = app,
                    isCentered = true,
                    iconSize = 88.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prominent Question with Thinking Emoji
                Text(
                    text = "Do you want to uninstall this? 🤔",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Buttons: "Proceed to Decommission" & "Keep Application"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onUninstall,
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
                        text = "Proceed to Decommission",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onKeep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = "Disregard & Keep",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Confirmation Dialog Overlay (Screen 3)
    if (showConfirmationDialog) {
        UninstallConfirmationDialog(
            app = app,
            onConfirmUninstall = onConfirmUninstall,
            onCancel = onDismissConfirmation
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RandomAppScreenPreview() {
    UselessUninstallTheme {
        RandomAppScreen(
            app = SampleApps.instagram,
            onUninstall = {},
            onKeep = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RandomAppScreenWithDialogPreview() {
    UselessUninstallTheme {
        RandomAppScreen(
            app = SampleApps.instagram,
            onUninstall = {},
            onKeep = {},
            showConfirmationDialog = true
        )
    }
}
