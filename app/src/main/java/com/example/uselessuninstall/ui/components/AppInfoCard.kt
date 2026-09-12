package com.example.uselessuninstall.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

/**
 * Reusable card displaying an application's icon, name, and package identifier.
 *
 * @param app The [AppInfo] instance to present.
 * @param modifier Modifier applied to the card container.
 * @param iconSize Dimensions of the app icon.
 * @param isCentered When true, renders in a prominent centered layout (ideal for result/countdown screens).
 */
@Composable
fun AppInfoCard(
    app: AppInfo,
    modifier: Modifier = Modifier,
    iconSize: Dp = 64.dp,
    isCentered: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        if (isCentered) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppIconDisplay(app = app, size = iconSize)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = app.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconDisplay(app = app, size = iconSize)

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Renders an app icon using [AppInfo.iconDrawable], or a styled initial letter avatar if null.
 */
@Composable
fun AppIconDisplay(
    app: AppInfo,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    val bitmap: ImageBitmap? = remember(app.iconDrawable) {
        app.iconDrawable?.let { drawableToImageBitmap(it) }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "${app.name} icon",
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.25f)),
            contentScale = ContentScale.Crop
        )
    } else {
        val initial = app.name.firstOrNull()?.uppercase() ?: "?"
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.25f))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = (size.value * 0.45f).sp
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap.asImageBitmap()
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

@Preview(showBackground = true)
@Composable
fun AppInfoCardRowPreview() {
    UselessUninstallTheme {
        AppInfoCard(
            app = SampleApps.instagram,
            modifier = Modifier.padding(16.dp),
            isCentered = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppInfoCardCenteredPreview() {
    UselessUninstallTheme {
        AppInfoCard(
            app = SampleApps.spotify,
            modifier = Modifier.padding(16.dp),
            isCentered = true
        )
    }
}
