package com.example.uselessuninstall.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.components.AppIconDisplay
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

/**
 * Screen 5: Displays the final outcome (Success or Cancelled/Failed).
 *
 * @param app The [AppInfo] of the targeted application.
 * @param isSuccess True if the uninstallation succeeded; false if cancelled or failed.
 * @param onFindAnother Triggered when the user taps "Run New Analysis".
 * @param onBackHome Triggered when the user chooses to navigate back to the home screen.
 * @param onTryAgain Triggered when uninstallation failed/cancelled and user wishes to retry.
 * @param message Optional custom description or reason string.
 * @param modifier Modifier applied to the screen root.
 */
@Composable
fun UninstallResultScreen(
    app: AppInfo,
    isSuccess: Boolean,
    onFindAnother: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
    onTryAgain: () -> Unit = onFindAnother,
    message: String? = null
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

            // Central status indicator and details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status Graphic Badge
                if (isSuccess) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancelled",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = if (isSuccess) "Decommissioning Requested" else "Operation Aborted",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // App Mini summary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    AppIconDisplay(app = app, size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = message ?: if (isSuccess) {
                        "System uninstallation prompt dispatched for ${app.name}. Device records updated."
                    } else {
                        "The decommissioning protocol for ${app.name} was aborted. System integrity maintained."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // Bottom Actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isSuccess) {
                    Button(
                        onClick = onFindAnother,
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
                            text = "Run New System Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onBackHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Back to Diagnostic Terminal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Button(
                        onClick = onTryAgain,
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
                            text = "Restart Evaluation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onBackHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Back to Diagnostic Terminal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UninstallResultSuccessPreview() {
    UselessUninstallTheme {
        UninstallResultScreen(
            app = SampleApps.instagram,
            isSuccess = true,
            onFindAnother = {},
            onBackHome = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UninstallResultFailurePreview() {
    UselessUninstallTheme {
        UninstallResultScreen(
            app = SampleApps.instagram,
            isSuccess = false,
            onFindAnother = {},
            onBackHome = {}
        )
    }
}
