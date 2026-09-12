package com.example.uselessuninstall.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

/**
 * Material 3 confirmation dialog with serious tone and 🤔 element.
 *
 * NOTE: Clicking "Authorize Decommission" does NOT directly uninstall the app; it only triggers [onConfirmUninstall]
 * for the caller.
 *
 * @param app The [AppInfo] of the application to be uninstalled.
 * @param onConfirmUninstall Callback triggered when user confirms decommissioning intent.
 * @param onCancel Callback triggered when user dismisses or clicks "Cancel".
 */
@Composable
fun UninstallConfirmationDialog(
    app: AppInfo,
    onConfirmUninstall: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Security Verification 🤔",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Candidate ${app.name} has been marked for uninstallation.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // App mini preview in dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIconDisplay(app = app, size = 44.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Are you certain you wish to proceed to final verification? 🤔",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmUninstall,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Authorize Decommission",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UninstallConfirmationDialogPreview() {
    UselessUninstallTheme {
        UninstallConfirmationDialog(
            app = SampleApps.instagram,
            onConfirmUninstall = {},
            onCancel = {}
        )
    }
}
